package de.christian2003.chaching.view.types

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Type
import de.christian2003.chaching.model.help.HelpCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * View model for the TypesScreen.
 */
class TypesViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Repository from which to source data.
     */
    private lateinit var repository: ChaChingRepository

    private var isInitialized: Boolean = false

    /**
     * List of all types available.
     */
    lateinit var allTypes: Flow<List<Type>>

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates the type that the user has selected for deletion. This is null if the user does
     * not want to delete any type.
     */
    var typeToDelete: Type? by mutableStateOf(null)


    /**
     * Instantiates the view model.
     *
     * @param repository    Repository from which to source data.
     */
    fun init(repository: ChaChingRepository) {
        if (!isInitialized) {
            this.repository = repository
            isHelpCardVisible = HelpCards.TYPES_LIST.getVisible(getApplication<Application>().baseContext)
            allTypes = repository.allTypes
            isInitialized = true
        }
    }


    /**
     * Deletes the type currently stored in "typeToDelete".
     */
    fun deleteType() = viewModelScope.launch(Dispatchers.IO) {
        val type = typeToDelete
        if (type != null) {
            typeToDelete = null
            repository.deleteType(type)
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
