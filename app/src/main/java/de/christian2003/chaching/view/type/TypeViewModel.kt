package de.christian2003.chaching.view.type

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.plugin.db.ChaChingRepository
import de.christian2003.chaching.plugin.db.entities.TypeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.model.help.HelpCards
import de.christian2003.chaching.domain.type.TypeIcon


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
    private var typeEntity: TypeEntity? = null


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
    var namePlaceholder: String by mutableStateOf("")

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
            var size: Int = repository.allTypesDeprecated.first().size
            if (typeId == null) {
                size++
            }
            namePlaceholder = getApplication<Application>().resources.getString(R.string.type_unnamed, size)
            if (typeId != null) {
                //Edit type
                isCreating = false
                typeEntity = repository.selectTypeById(typeId)
                name = typeEntity!!.name
                isHoursWorkedEditable = typeEntity!!.isHoursWorkedEditable
                icon = typeEntity!!.icon
            }
            else {
                //Create new type:
                typeEntity = null
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
        var typeEntity: TypeEntity? = this@TypeViewModel.typeEntity
        if (typeEntity == null) {
            typeEntity = TypeEntity(
                name = name,
                icon = icon,
                isHoursWorkedEditable = isHoursWorkedEditable
            )
            repository.insertType(typeEntity)
        }
        else {
            typeEntity.name = name
            typeEntity.icon = icon
            typeEntity.isHoursWorkedEditable = isHoursWorkedEditable
            typeEntity.edited = LocalDateTime.now()
            repository.updateType(typeEntity)
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
