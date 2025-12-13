package de.christian2003.chaching.plugin.presentation.view.transfer

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.application.usecases.transfer.CreateTransferUseCase
import de.christian2003.chaching.application.usecases.transfer.GetTransferByIdUseCase
import de.christian2003.chaching.application.usecases.transfer.UpdateTransferUseCase
import de.christian2003.chaching.application.usecases.type.GetTypeByIdUseCase
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.view.help.HelpCards
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * View model for the TransferScreen.
 *
 * @param application               Application.
 * @param savedStateHandle          Saved state handle.
 * @param getTypeByIdUseCase        Use case to get a type by it's ID.
 * @param getTransferByIdUseCase    Use case to get a transfer by it's ID.
 * @param createTransferUseCase     Use case to create a new transfer.
 * @param updateTransferUseCase     Use case to update an existing transfer.
 */
@HiltViewModel
class TransferViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getTypeByIdUseCase: GetTypeByIdUseCase,
    getTransferByIdUseCase: GetTransferByIdUseCase,
    private val createTransferUseCase: CreateTransferUseCase,
    private val updateTransferUseCase: UpdateTransferUseCase
): AndroidViewModel(application) {

    /**
     * Transfer to edit. If a new transfer is created, this is null.
     */
    private var transfer: Transfer? = null

    /**
     * Number format used to format numbers according to the user's locale.
     */
    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.getDefault())


    /**
     * Type of the transfer.
     */
    var type: Type? by mutableStateOf(null)

    /**
     * String representation of the value.
     */
    var value: String by mutableStateOf("")

    /**
     * Error message for the value entered. If the value is valid, this is null.
     */
    var valueErrorMessage: String? by mutableStateOf(null)

    /**
     * String representation of the hours worked.
     */
    var hoursWorked: String by mutableStateOf("")

    /**
     * Error message for the hours worked entered. If the value is valid, this is null.
     */
    var hoursWorkedErrorMessage: String? by mutableStateOf(null)

    /**
     * Value date of the transfer.
     */
    var valueDate: LocalDate by mutableStateOf(LocalDate.now())

    /**
     * Indicates whether the date picker is visible.
     */
    var isDatePickerVisible: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the hoursWorked-field is visible.
     */
    var isHoursWorkedEditable: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the transfer is a salary.
     */
    var isSalary: Boolean by mutableStateOf(true)

    /**
     * Indicates whether the screen is used to create a new transfer.
     */
    var isCreating: Boolean = false

    /**
     * Indicates whether the values currently entered by the user can be used to save the transfer.
     */
    var isSavable: Boolean by mutableStateOf(false)

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCards.CREATE_TRANSFER.getVisible(application))


    /**
     * Initializes the view model.
     */
    init {
        val typeId: UUID = try {
            UUID.fromString(savedStateHandle["typeId"])
        } catch (_: Exception) {
            throw IllegalStateException("Cannot create or edit transfer without type")
        }
        val transferId: UUID? = try {
            UUID.fromString(savedStateHandle["transferId"])
        } catch (_: Exception) {
            null
        }

        viewModelScope.launch(Dispatchers.IO) {
            //Get type:
            val type: Type? = getTypeByIdUseCase.getTypeById(typeId)
            if (type == null) {
                throw IllegalStateException("Cannot create transfer where 'type = null'.")
            }
            this@TransferViewModel.type = type
            isHoursWorkedEditable = type.metadata.isHoursWorkedEditable

            if (transferId != null) {
                //Edit transfer:
                val transfer: Transfer? = getTransferByIdUseCase.getTransferById(transferId)
                if (transfer == null) {
                    throw IllegalStateException("Cannot edit transfer that does not exist")
                }
                isCreating = false
                this@TransferViewModel.transfer = transfer

                //Cannot use number format because it puts group separator into the formatted string,
                //which confuses the visual transformation for the TextField.
                val roundingFormat = DecimalFormat("#.##")
                value = roundingFormat.format(transfer.transferValue.value.toDouble() / 100.0)

                valueErrorMessage = null
                hoursWorked = transfer.hoursWorked.toString()
                hoursWorkedErrorMessage = null
                valueDate = transfer.transferValue.date
                isSalary = transfer.transferValue.isSalary
                isSavable = true
            }
            else {
                //Create new transfer:
                isCreating = true
                transfer = null
                value = ""
                valueErrorMessage = null
                hoursWorked = ""
                hoursWorkedErrorMessage = null
                valueDate = LocalDate.now()
                isSalary = type.metadata.isSalaryByDefault
                isSavable = false
            }
        }
    }


    /**
     * Updates the value entered by the user.
     *
     * @param value Value entered by the user.
     */
    fun updateValue(value: String) {
        if (value.isEmpty()) {
            this.value = value
            valueErrorMessage = getApplication<Application>().getString(R.string.error_emptyText)
            updateIsSavable()
            return
        }

        try {
            numberFormat.parse(this@TransferViewModel.value)!!.toDouble()
            this.value = value
            valueErrorMessage = null
        } catch (_: Exception) {
            this.value = value
            valueErrorMessage = getApplication<Application>().getString(R.string.error_valueError)
        }
        updateIsSavable()
    }


    /**
     * Updates the hours worked that are entered by the user.
     *
     * @param hoursWorked   Value entered by the user.
     */
    fun updateHoursWorked(hoursWorked: String) {
        this.hoursWorked = hoursWorked

        if (hoursWorked.isEmpty()) {
            hoursWorkedErrorMessage = null
            updateIsSavable()
            return
        }

        val i: Int? = hoursWorked.toIntOrNull()
        hoursWorkedErrorMessage = if (i == null) {
            getApplication<Application>().getString(R.string.error_hoursWorkedError)
        } else {
            null
        }
        updateIsSavable()
    }


    /**
     * Updates the "isSavable" field according to the current state of the view model.
     */
    fun updateIsSavable() {
        isSavable = valueErrorMessage == null && hoursWorkedErrorMessage == null && value.isNotEmpty()
    }


    /**
     * Saves the transfer if possible. If the values entered are invalid, nothing happens. The
     * method either inserts a new transfer or updates an existing one.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        val value: Int? = try {
            (numberFormat.parse(this@TransferViewModel.value)!!.toDouble() * 100).roundToInt()
        } catch (_: Exception) {
            null
        }
        val hoursWorked: Int? = if (this@TransferViewModel.hoursWorked.isNotEmpty()) {
            try {
                this@TransferViewModel.hoursWorked.toInt()
            } catch (_: Exception) {
                null
            }
        } else {
            0
        }

        val application: Application = getApplication()
        if (value == null) {
            valueErrorMessage = application.getString(R.string.error_valueError)
        }
        if (hoursWorked == null) {
            valueErrorMessage = application.getString(R.string.error_hoursWorkedError)
        }
        if (value == null || hoursWorked == null) {
            return@launch
        }

        if (transfer == null) {
            createTransferUseCase.createTransfer(
                value = value,
                date = valueDate,
                isSalary = isSalary,
                hoursWorked = hoursWorked,
                typeId = type!!.id
            )
        }
        else {
            updateTransferUseCase.updateTransfer(
                transferId = transfer!!.id,
                value = value,
                date = valueDate,
                isSalary = isSalary,
                hoursWorked = hoursWorked
            )
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        isHelpCardVisible = false
        HelpCards.CREATE_TRANSFER.setVisible(getApplication<Application>().baseContext, false)
    }

}
