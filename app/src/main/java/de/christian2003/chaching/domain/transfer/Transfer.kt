package de.christian2003.chaching.domain.transfer

import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.UUID


/**
 * Domain entity models a transfer. Each transfer consists of a transfer value (stored in cents),
 * a value date as well as a transfer type. Additionally, some metadata is stored as well. Each
 * transfer must always be created for a single transfer type, which describes the created transfer
 * in greater detail.
 *
 * @param transferValue Value for the transfer.
 * @param hoursWorked   Hours worked for this transfer.
 * @param type          Type of the transfer. The type is references through it's ID.
 * @param id            Unique ID of the type.
 * @param metadata      Metadata for the transfer.
 */
class Transfer(
    transferValue: TransferValue,
    hoursWorked: Int,
    val type: UUID,
    val id: UUID = UUID.randomUUID(),
    var metadata: TransferMetadata = TransferMetadata()
) {

    /**
     * Value for the transfer.
     */
    var transferValue: TransferValue = transferValue
        set(value) {
            field = value
            metadata = metadata.copy(edited = LocalDateTime.now())
        }

    /**
     * Hours worked for this transfer.
     */
    var hoursWorked: Int = hoursWorked
        set(value) {
            field = value
            metadata = metadata.copy(edited = LocalDateTime.now())
        }


    /**
     * Initializes the transfer.
     */
    init {
        this.transferValue = transferValue
        this.hoursWorked = hoursWorked
    }


    /**
     * Returns the value of the transfer as formatted string. For example, a value of 153005 cents
     * (which means 1530.05 €) will be returned as "1,530.05". Depending on the locale, other
     * formats such as "1.530,05" can be returned as well.
     * The value returned will NEVER include any currency symbol.
     *
     * @return  Formatted value.
     */
    @Deprecated("Use service instead")
    fun getFormattedValue() : String {
        val numberFormat: NumberFormat = DecimalFormat("#,###.00")
        val formattedNumber: String = numberFormat.format(transferValue.value.toDouble() / 100.0)
        return formattedNumber
    }


    /**
     * Hash code for the transfer.
     *
     * @return  Hash code of the ID of this transfer.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Returns whether the transfer passed is identical to this transfer.
     *
     * @return  Whether the ID of the transfer passed is identical to the ID of this transfer.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Transfer) && (other.id == this.id)
    }

}
