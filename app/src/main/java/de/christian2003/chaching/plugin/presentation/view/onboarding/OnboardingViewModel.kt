package de.christian2003.chaching.plugin.presentation.view.onboarding

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import de.christian2003.chaching.R
import de.christian2003.chaching.application.usecases.transfer.CreateTransferUseCase
import de.christian2003.chaching.application.usecases.type.CreateTypeUseCase
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.type.TypeIcon
import de.christian2003.chaching.domain.type.TypeMetadata


/**
 * View model for the OnboardingScreen.
 */
class OnboardingViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var createTypeUseCase: CreateTypeUseCase

    /**
     * Indicates whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false


    /**
     * Map of default types maps each default type to a boolean which indicates whether the
     * corresponding type has been selected by the user.
     */
    val defaultTypes: MutableMap<Type, Boolean> = mutableStateMapOf()

    /**
     * Indicates whether the user has selected a type (i.e. at least one default type within
     * "defaultTypes" is mapped to 'true').
     */
    var typesSelected: Boolean by mutableStateOf(false)


    /**
     * Initializes the repository.
     *
     * @param createTypeUseCase Use case to create a new type.
     */
    fun init(createTypeUseCase: CreateTypeUseCase) {
        if (!isInitialized) {
            this.createTypeUseCase = createTypeUseCase
            generateDefaultTypes()
            isInitialized = true
        }
    }


    /**
     * Change the selection one of the default types.
     *
     * @param type      Type for which to change selection.
     * @param selected  Whether the type shall be selected.
     */
    fun changeTypeSelected(type: Type, selected: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        defaultTypes[type] = selected
        defaultTypes.forEach { (_, selected) ->
            if (selected) {
                typesSelected = true
                return@launch
            }
        }
        typesSelected = false
    }


    /**
     * Saves the types selected to the repository.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        val types: MutableMap<Type, Boolean> = defaultTypes
        types.forEach { (type, selected) ->
            if (selected) {
                createTypeUseCase.createType(
                    name = type.name,
                    icon = type.icon,
                    isHoursWorkedEditable = type.metadata.isHoursWorkedEditable,
                    isEnabledInQuickAccess = true
                )
            }
        }
    }


    /**
     * Generates the default types from which the user needs to choose at least one.
     */
    private fun generateDefaultTypes() {
        val context: Context = getApplication<Application>().baseContext

        defaultTypes.put(
            Type(
                name = context.getString(R.string.onboarding_defaultTypeSalary),
                icon = TypeIcon.COIN,
                metadata = TypeMetadata(
                    isHoursWorkedEditable = true
                )
            ),
            false
        )
        defaultTypes.put(
            Type(
                name = context.getString(R.string.onboarding_defaultTypeSickPay),
                icon = TypeIcon.HOME,
                metadata = TypeMetadata(
                    isHoursWorkedEditable = false
                )
            ),
            false
        )
        defaultTypes.put(
            Type(
                name = context.getString(R.string.onboarding_defaultTypeHolidayPay),
                icon = TypeIcon.VACATION,
                metadata = TypeMetadata(
                    isHoursWorkedEditable = true
                )
            ),
            false
        )
        defaultTypes.put(
            Type(
                name = context.getString(R.string.onboarding_defaultTypeSpecialPay),
                icon = TypeIcon.BANK,
                metadata = TypeMetadata(
                    isHoursWorkedEditable = false
                )
            ),
            false
        )
        defaultTypes.put(
            Type(
                name = context.getString(R.string.onboarding_defaultTypeShareInterest),
                icon = TypeIcon.SHARES,
                metadata = TypeMetadata(
                    isHoursWorkedEditable = false
                )
            ),
            false
        )
    }

}
