package de.christian2003.chaching.plugin.presentation.view.type

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.application.usecases.type.CreateTypeUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesUseCase
import de.christian2003.chaching.application.usecases.type.GetTypeByIdUseCase
import de.christian2003.chaching.application.usecases.type.UpdateTypeUseCase
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.HelpCards
import de.christian2003.chaching.domain.type.TypeIcon
import javax.inject.Inject


/**
 * View model for the TypeScreen.
 *
 * @param application           Application.
 * @param savedStateHandle      Saved state handle.
 * @param getAllTypesUseCase    Use case to get a list of all types.
 * @param getTypeByIdUseCase    Use case to get a type by it's ID.
 * @param createTypeUseCase     Use case to create a new type.
 * @param updateTypeUseCase     Use case to update an existing type.
 */
@HiltViewModel
class TypeViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getAllTypesUseCase: GetAllTypesUseCase,
    getTypeByIdUseCase: GetTypeByIdUseCase,
    private val createTypeUseCase: CreateTypeUseCase,
    private val updateTypeUseCase: UpdateTypeUseCase
): AndroidViewModel(application) {

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
    var isHoursWorkedEditable: Boolean by mutableStateOf(true)

    /**
     * Whether the type is visible in the "+"-FAB on the main screen.
     */
    var isEnabledInQuickAccess: Boolean by mutableStateOf(true)

    /**
     * Whether transfers od this type are created as salary by default.
     */
    var isSalaryByDefault: Boolean by mutableStateOf(true)

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
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCards.CREATE_TYPE.getVisible(application))

    /**
     * Indicates whether the help dialog for the quick info is visible.
     */
    var isQuickAccessHelpVisible: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     */
    init {
        val typeId: UUID? = try {
            UUID.fromString(savedStateHandle["typeId"])
        } catch (_: Exception) {
            null
        }

        viewModelScope.launch {
            var size: Int = getAllTypesUseCase.getAllTypes().first().size
            if (typeId == null) {
                size++
            }
            namePlaceholder = getApplication<Application>().resources.getString(R.string.type_unnamed, size)
            if (typeId != null) {
                //Edit type
                isCreating = false
                type = getTypeByIdUseCase.getTypeById(typeId)
                name = type!!.name
                isHoursWorkedEditable = type!!.metadata.isHoursWorkedEditable
                isSalaryByDefault = type!!.metadata.isSalaryByDefault
                isEnabledInQuickAccess = type!!.metadata.isEnabledInQuickAccess
                icon = type!!.icon
            }
            else {
                //Create new type:
                type = null
                isCreating = true
                name = ""
                isHoursWorkedEditable = true
                isSalaryByDefault = true
                isEnabledInQuickAccess = true
                icon = TypeIcon.CURRENCY
            }
        }
    }


    /**
     * Saves the type by either inserting a new type or updating the type in the database.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        val type: Type? = this@TypeViewModel.type
        if (type == null) {
            createTypeUseCase.createType(
                name = name,
                icon = icon,
                isHoursWorkedEditable = isHoursWorkedEditable,
                isEnabledInQuickAccess = isEnabledInQuickAccess,
                isSalaryByDefault = isSalaryByDefault
            )
        }
        else {
            updateTypeUseCase.updateType(
                typeId = type.id,
                name = name,
                icon = icon,
                isHoursWorkedEditable = isHoursWorkedEditable,
                isEnabledInQuickAccess = isEnabledInQuickAccess,
                isSalaryByDefault = isSalaryByDefault
            )
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
