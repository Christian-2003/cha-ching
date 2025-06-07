package de.christian2003.chaching.view.type

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.model.help.HelpCards
import de.christian2003.chaching.model.transfers.TypeIcon


/**
 * View model for the TypeScreen.
 */
class TypeViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Repository from which to source data.
     */
    private lateinit var repository: ChaChingRepository

    private var isInitialized: Boolean = false

    /**
     * Type that is being edited. If a new type is being created, this is null.
     */
    private var type: Type? = null


    /**
     * Name of the type.
     */
    var name: String by mutableStateOf("")

    /**
     * Whether the hoursWorked-field of transfers for this type shall be editable.
     */
    var isHoursWorkedEditable by mutableStateOf(true)

    /**
     * Icon selected by the user.
     */
    var icon: TypeIcon by mutableStateOf(TypeIcon.CURRENCY)

    /**
     * Placeholder for the name to show in the app bar in case the user removes the name.
     */
    var namePlaceholder: String = ""

    /**
     * Indicates whether the screen is currently creating a new type.
     */
    var isCreating: Boolean = false

    /**
     * Indicates whether the help card is visible to the user.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(false)


    /**
     * Instantiates the repository.
     *
     * @param repository    Repository from which to source data.
     * @param typeId        UUID of the type to edit. Pass null to create a new type.
     */
    fun init(repository: ChaChingRepository, typeId: UUID?) = viewModelScope.launch(Dispatchers.IO) {
        if (!isInitialized) {
            this@TypeViewModel.repository = repository
            isHelpCardVisible = HelpCards.CREATE_TYPE.getVisible(getApplication<Application>().baseContext)
            var size: Int = repository.allTypes.first().size
            if (typeId == null) {
                size++
            }
            namePlaceholder = getApplication<Application>().resources.getString(R.string.type_unnamed, size)
            if (typeId != null) {
                //Edit type
                isCreating = false
                type = repository.selectTypeById(typeId)
                name = type!!.name
                isHoursWorkedEditable = type!!.isHoursWorkedEditable
                icon = type!!.icon
            }
            else {
                //Create new type:
                type = null
                isCreating = true
                name = ""
                isHoursWorkedEditable = true
                icon = TypeIcon.CURRENCY
            }
            isInitialized = true
        }
    }


    /**
     * Saves the type by either inserting a new type or updating the type in the database.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        var type: Type? = this@TypeViewModel.type
        if (type == null) {
            type = Type(
                name = name,
                icon = icon,
                isHoursWorkedEditable = isHoursWorkedEditable
            )
            repository.insertType(type)
        }
        else {
            type.name = name
            type.icon = icon
            type.isHoursWorkedEditable = isHoursWorkedEditable
            type.edited = LocalDateTime.now()
            repository.updateType(type)
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCards.CREATE_TYPE.setVisible(getApplication<Application>().baseContext, false)
    }

}
