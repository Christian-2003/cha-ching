package de.christian2003.chaching.plugin.presentation.view.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.backup.BackupService
import de.christian2003.chaching.application.backup.ImportStrategy
import de.christian2003.chaching.application.services.DateTimeFormatterService
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.application.usecases.apps.GetAllAppsUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesInTrashUseCase
import de.christian2003.chaching.domain.apps.AppItem
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.time.LocalDate
import javax.inject.Inject


/**
 * View model for the screen displaying the app settings.
 *
 * @param application               Application.
 * @param getAllAppsUseCase         Use case to get a list of all apps.
 * @param getAllTypesInTrashUseCase Use case to get all types that are in the trash bin.
 * @param valueFormatterService     Service to format values.
 * @param dateTimeFormatterService  Service to format date times.
 * @param backupService             Backup service.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    getAllAppsUseCase: GetAllAppsUseCase,
    getAllTypesInTrashUseCase: GetAllTypesInTrashUseCase,
    private val valueFormatterService: ValueFormatterService,
    private val dateTimeFormatterService: DateTimeFormatterService,
    private val backupService: BackupService,
    val client: OkHttpClient
): AndroidViewModel(application) {

    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var importUri: Uri? by mutableStateOf(null)

    val apps: MutableList<AppItem> = mutableStateListOf()

    var dialog: SettingsScreenDialog by mutableStateOf(SettingsScreenDialog.None)

    var useGlobalTheme: Boolean by mutableStateOf(preferences.getBoolean("global_theme", false))
        private set

    var numberOfTypesInTrash: Int by mutableIntStateOf(0)
        private set

    /**
     * Contrast for the theme colors.
     */
    var themeContrast: ThemeContrast by mutableStateOf(ThemeContrast.entries[preferences.getInt("theme_contrast", 0)])
        private set


    init {
        getAllTypesInTrashUseCase.getAllTypesInTrash().onEach { typesInTrash ->
            numberOfTypesInTrash = typesInTrash.size
        }.launchIn(viewModelScope)

        viewModelScope.launch(Dispatchers.IO) {
            val apps: List<AppItem> = getAllAppsUseCase.getAllApps()
            this@SettingsViewModel.apps.clear()
            this@SettingsViewModel.apps.addAll(apps)
        }
    }


    fun exportDataToJsonFile(uri: Uri, onFinished: (Boolean) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val serialized: String? = backupService.serialize()
        var success: Boolean = serialized != null
        if (serialized != null) {
            success = writeToFile(uri, serialized)
        }

        withContext(Dispatchers.Main) {
            onFinished(success)
        }
    }


    fun importDataFromJsonFile(uri: Uri, onFinished: (Boolean) -> Unit, importStrategy: ImportStrategy) = viewModelScope.launch(Dispatchers.IO) {
        val serialized: String? = readFromFile(uri)
        var success: Boolean = serialized != null
        if (serialized != null) {
            success = backupService.deserialize(serialized, importStrategy)
        }

        withContext(Dispatchers.Main) {
            onFinished(success)
        }
    }


    /**
     * Updates whether to use the global theme.
     *
     * @param useGlobalTheme    Whether to use global theme.
     */
    fun updateUseGlobalTheme(useGlobalTheme: Boolean) {
        preferences.edit {
            putBoolean("global_theme", useGlobalTheme)
        }
        this.useGlobalTheme = useGlobalTheme
    }


    /**
     * Updates the theme contrast.
     *
     * @param themeContrast Theme contrast.
     */
    fun updateThemeContrast(themeContrast: ThemeContrast) {
        preferences.edit {
            putInt("theme_contrast", themeContrast.ordinal)
        }
        this.themeContrast = themeContrast
    }


    fun formatValue(value: TransferValue): String {
        return valueFormatterService.format(value)
    }

    fun formatDate(date: LocalDate): String {
        return dateTimeFormatterService.format(date)
    }


    private fun writeToFile(uri: Uri, content: String): Boolean {
        val context: Context = getApplication<Application>().baseContext
        try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(content.toByteArray())
                stream.flush()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            return false
        }
        return true
    }


    private fun readFromFile(uri: Uri): String? {
        val context: Context = getApplication<Application>().baseContext
        var content: String? = null
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val buffer = ByteArray(1024)
                val stringBuilder = StringBuilder()
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    stringBuilder.append(String(buffer, 0, bytesRead))
                }
                content = stringBuilder.toString()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            content = null
        }
        return content
    }

}
