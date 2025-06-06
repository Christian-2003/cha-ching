package de.christian2003.chaching.view.transfer

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.TransferWithType
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.model.help.HelpCards
import java.time.LocalDateTime


/**
 * View model for the TransferScreen.
 */
class TransferViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Repository from which to source data.
     */
    private lateinit var repository: ChaChingRepository

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
    lateinit var type: Type

    /**
     * String representation of the value.
     */
    var value: TextFieldValue by mutableStateOf(TextFieldValue(""))

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
    var isHoursWorkedEditable: Boolean = false

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
    var isHelpCardVisible: Boolean by mutableStateOf(false)


    /**
     * Instantiates the view model.
     *
     * @param repository    Repository from which to source data.
     * @param typeId        ID of the type with which to create the transfer.
     * @param transferId    ID of the transfer to edit. To create a new transfer, pass null.
     */
    fun init(repository: ChaChingRepository, typeId: UUID, transferId: UUID?) = viewModelScope.launch(Dispatchers.IO) {
        this@TransferViewModel.repository = repository
        isHelpCardVisible = HelpCards.CREATE_TRANSFER.getVisible(getApplication<Application>().baseContext)

        //Get type:
        val type: Type? = repository.selectTypeById(typeId)
        if (type == null) {
            throw IllegalStateException("Cannot create transfer where 'type = null'.")
            return@launch
        }
        this@TransferViewModel.type = type
        isHoursWorkedEditable = type.isHoursWorkedEditable

        if (transferId != null) {
            //Edit transfer:
            val transferWithType: TransferWithType? = repository.selectTransferWithTypeById(transferId)
            if (transferWithType == null) {
                throw IllegalStateException("Cannot edit transfer that does not exist")
            }
            isCreating = false
            transfer = transferWithType.transfer
            if (this@TransferViewModel.type.typeId != transferWithType.type.typeId) {
                this@TransferViewModel.type = transferWithType.type
                isHoursWorkedEditable = transferWithType.type.isHoursWorkedEditable
            }
            val formattedValue = numberFormat.format(transferWithType.transfer.value.toDouble() / 100)
            value = TextFieldValue(formattedValue, TextRange(formattedValue.length))
            valueErrorMessage = null
            hoursWorked = transferWithType.transfer.hoursWorked.toString()
            hoursWorkedErrorMessage = null
            valueDate = transferWithType.transfer.valueDate
            isSavable = true
        }
        else {
            //Create new transfer:
            isCreating = true
            transfer = null
            value = TextFieldValue("")
            valueErrorMessage = null
            hoursWorked = ""
            hoursWorkedErrorMessage = null
            valueDate = LocalDate.now()
            isSavable = false
        }
    }


    /**
     * Updates the value entered by the user.
     *
     * @param value Value entered by the user.
     */
    fun updateValue(value: TextFieldValue) {
        if (value.text.isEmpty()) {
            this.value = value
            valueErrorMessage = getApplication<Application>().getString(R.string.error_emptyText)
            updateIsSavable()
            return
        }

        try {
            val valueAsDouble: Double = numberFormat.parse(value.text)!!.toDouble()

            val lastCharEntered: Char = value.text[value.text.length - 1]
            if (lastCharEntered == ',' || lastCharEntered == '.') {
                val formattedNumber = numberFormat.format(valueAsDouble) + lastCharEntered
                this.value = TextFieldValue(formattedNumber, TextRange(formattedNumber.length))
            }
            else {
                val formattedNumber = numberFormat.format(valueAsDouble)
                if (formattedNumber.length > value.text.length) {
                    this.value = TextFieldValue(formattedNumber, TextRange(value.selection.end + 1))
                }
                else if (formattedNumber.length < value.text.length) {
                    this.value = TextFieldValue(formattedNumber, TextRange(value.selection.end - 1))
                }
                else {
                    this.value = TextFieldValue(formattedNumber, value.selection)
                }

            }
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
        isSavable = valueErrorMessage == null && hoursWorkedErrorMessage == null && value.text.isNotEmpty()
    }


    /**
     * Saves the transfer if possible. If the values entered are invalid, nothing happens. The
     * method either inserts a new transfer or updates an existing one.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        var value: Int? = try {
            (numberFormat.parse(this@TransferViewModel.value.text)!!.toDouble() * 100).toInt()
        } catch (_: Exception) {
            null
        }
        var hoursWorked: Int? = if (this@TransferViewModel.hoursWorked.isNotEmpty()) {
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
            transfer = Transfer(
                value = value,
                hoursWorked = hoursWorked,
                valueDate = valueDate,
                isSalary = true,
                type = type.typeId
            )
            repository.insertTransfer(transfer!!)
        }
        else {
            transfer!!.value = value
            transfer!!.hoursWorked = hoursWorked
            transfer!!.valueDate = valueDate
            transfer!!.edited = LocalDateTime.now()
            repository.updateTransfer(transfer!!)
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
