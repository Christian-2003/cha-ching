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


class TransferViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var repository: ChaChingRepository

    private var transfer: Transfer? = null

    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.getDefault())


    lateinit var type: Type

    var value: TextFieldValue by mutableStateOf(TextFieldValue(""))

    var valueErrorMessage: String? by mutableStateOf(null)

    var hoursWorked: String by mutableStateOf("")

    var valueDate: LocalDate by mutableStateOf(LocalDate.now())

    var isCreating: Boolean = false


    fun init(repository: ChaChingRepository, typeId: UUID, transferId: UUID?) = viewModelScope.launch(Dispatchers.IO) {
        this@TransferViewModel.repository = repository

        val type: Type? = repository.selectTypeById(typeId)
        if (type == null) {
            throw IllegalStateException("Cannot create transfer where 'type = null'.")
            return@launch
        }
        this@TransferViewModel.type = type

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
            }
            val formattedValue = numberFormat.format(transferWithType.transfer.valueDate)
            valueErrorMessage = null
            value = TextFieldValue(formattedValue, TextRange(formattedValue.length))
            hoursWorked = transferWithType.transfer.hoursWorked.toString()
            valueDate = transferWithType.transfer.valueDate
        }
        else {
            //Create new transfer:
            isCreating = true
            transfer = null
            value = TextFieldValue("")
            valueErrorMessage = null
            hoursWorked = ""
            valueDate = LocalDate.now()
        }
    }


    fun updateValue(value: TextFieldValue) {
        if (value.text.isEmpty()) {
            this.value = value
            valueErrorMessage = getApplication<Application>().getString(R.string.error_emptyText)
            return
        }

        try {
            val valueAsDouble: Double = numberFormat.parse(value.text).toDouble()

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
        } catch (e: Exception) {
            this.value = value
            valueErrorMessage = getApplication<Application>().getString(R.string.error_valueError)
        }
    }

}
