package de.christian2003.chaching.view.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.application.backup.BackupService
import de.christian2003.chaching.application.backup.ImportStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var backupService: BackupService

    private var isInitialized: Boolean = false


    var importUri: Uri? by mutableStateOf(null)


    fun init(backupService: BackupService) {
        if (!isInitialized) {
            this.backupService = backupService
            isInitialized = true
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
