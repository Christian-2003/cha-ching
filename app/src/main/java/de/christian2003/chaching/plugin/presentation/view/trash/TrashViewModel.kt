package de.christian2003.chaching.plugin.presentation.view.trash

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.services.DateTimeFormatterService
import de.christian2003.chaching.application.usecases.type.DeleteTypeUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesInTrashUseCase
import de.christian2003.chaching.application.usecases.type.RestoreTypeFromTrashUseCase
import de.christian2003.chaching.domain.type.DeletedType
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.db.DeletedTypeDao
import de.christian2003.chaching.plugin.presentation.view.help.HelpCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * View model for the screen displaying the trash bin.
 *
 * @param application                   Application.
 * @param getAllTypesInTrashUseCase     Use case to get a list of all types in the trash bin.
 * @param restoreTypeFromTrashUseCase   Use case to restore a type from the trash bin.
 * @param deleteTypeUseCase             Use case to permanently delete a type from the app.
 * @param dateTimeFormatterService      Service used to format date times.
 */
@HiltViewModel
class TrashViewModel @Inject constructor(
    application: Application,
    getAllTypesInTrashUseCase: GetAllTypesInTrashUseCase,
    private val restoreTypeFromTrashUseCase: RestoreTypeFromTrashUseCase,
    private val deleteTypeUseCase: DeleteTypeUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService
): AndroidViewModel(application) {

    /**
     * List of types that are in the trash bin currently.
     */
    val typesInTrash: Flow<List<DeletedType>> = getAllTypesInTrashUseCase.getAllTypesInTrash()

    /**
     * Indicates whether the help card is currently visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCards.HELP_TRASH.getVisible(application))
        private set

    /**
     * Type that is currently waiting for confirmation for deletion.
     */
    var typeToDelete: Type? by mutableStateOf(null)


    /**
     * Restores the specified type from the trash bin.
     */
    fun restoreType(deletedType: DeletedType) = viewModelScope.launch(Dispatchers.IO) {
        restoreTypeFromTrashUseCase.restoreTypeFromTrash(deletedType.type.id)
    }


    /**
     * Dismisses the delete dialog and optionally deletes the type that is marked for deletion
     * currently.
     *
     * @param typeToDelete  Type to delete or null to dismiss without deleting.
     */
    fun dismissDeleteDialog(typeToDelete: Type? = null) = viewModelScope.launch(Dispatchers.IO) {
        this@TrashViewModel.typeToDelete = null
        if (typeToDelete != null) {
            deleteTypeUseCase.deleteType(typeToDelete.id)
        }
    }


    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTimeFormatterService.formatRelative(dateTime)
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCards.HELP_TRASH.setVisible(application, false)
        isHelpCardVisible = false
    }

}
