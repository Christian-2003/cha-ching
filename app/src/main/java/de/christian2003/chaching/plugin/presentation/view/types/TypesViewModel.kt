package de.christian2003.chaching.plugin.presentation.view.types

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.usecases.type.GetAllTypesNotInTrashUseCase
import de.christian2003.chaching.application.usecases.type.MoveTypeToTrashUseCase
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.HelpCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the TypesScreen.
 *
 * @param application                   Application.
 * @param getAllTypesNotInTrashUseCase  Use case to get a list of all types.
 * @param moveTypeToTrashUseCase        Use case to move types to the trash bin.
 */
@HiltViewModel
class TypesViewModel @Inject constructor(
    application: Application,
    getAllTypesNotInTrashUseCase: GetAllTypesNotInTrashUseCase,
    private val moveTypeToTrashUseCase: MoveTypeToTrashUseCase
): AndroidViewModel(application) {

    /**
     * List of all types available.
     */
    val allTypes: Flow<List<Type>> = getAllTypesNotInTrashUseCase.getAllTypesNotInTrash()

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCards.TYPES_LIST.getVisible(application))

    /**
     * Indicates the type that the user has selected for deletion. This is null if the user does
     * not want to delete any type.
     */
    var typeToDelete: Type? by mutableStateOf(null)


    /**
     * Deletes the type currently stored in "typeToDelete".
     */
    fun deleteType() = viewModelScope.launch(Dispatchers.IO) {
        val type = typeToDelete
        if (type != null) {
            typeToDelete = null
            moveTypeToTrashUseCase.moveTypeToTrash(type.id)
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCards.TYPES_LIST.setVisible(getApplication<Application>().baseContext, false)
    }

}
