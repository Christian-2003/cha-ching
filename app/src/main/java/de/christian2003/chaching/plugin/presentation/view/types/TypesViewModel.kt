package de.christian2003.chaching.plugin.presentation.view.types

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.usecases.type.DeleteTypeUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesUseCase
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.view.help.HelpCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the TypesScreen.
 *
 * @param application           Application.
 * @param getAllTypesUseCase    Use case to get a list of all types.
 * @param deleteTypeUseCase     Use case to delete an existing type.
 */
@HiltViewModel
class TypesViewModel @Inject constructor(
    application: Application,
    getAllTypesUseCase: GetAllTypesUseCase,
    private val deleteTypeUseCase: DeleteTypeUseCase
): AndroidViewModel(application) {

    /**
     * List of all types available.
     */
    val allTypes: Flow<List<Type>> = getAllTypesUseCase.getAllTypes()

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
            deleteTypeUseCase.deleteType(type.id)
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
