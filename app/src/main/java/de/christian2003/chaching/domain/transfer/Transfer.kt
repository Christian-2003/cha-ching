package de.christian2003.chaching.domain.transfer

import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


/**
 * Domain entity models a transfer. Each transfer consists of a transfer value (stored in cents),
 * a value date as well as a transfer type. Additionally, some metadata is stored as well. Each
 * transfer must always be created for a single transfer type, which describes the created transfer
 * in greater detail.
 */
class Transfer(

    /**
     * Stores the value of the transfer in cents. For example, a value of 15.05 € would be stored as
     * 1505 cents.
     */
    var value: Int,

    /**
     * Stores the hours worked for this transfer.
     */
    var hoursWorked: Int,

    /**
     * Indicates whether the transfer is a salary.
     * Currently, this is unused. Later, this field should indicate whether the transfer is an income
     * (= true) or an expense (= false). This way, the value can always store positive numbers.
     */
    var isSalary: Boolean,

    /**
     * Stores the value date on which the transfer takes effect.
     */
    var valueDate: LocalDate,

    /**
     * Type of the transfer. The type is references through it's ID.
     */
    val type: UUID,

    /**
     * Unique ID of the type.
     */
    val id: UUID = UUID.randomUUID(),

    /**
     * Date time on which the transfer was created. This is for statistical purposes.
     */
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date time ojn which the transfer was last edited. This is for statistical purposes.
     */
    var edited: LocalDateTime = LocalDateTime.now()

) {

    /**
     * Returns the value of the transfer as formatted string. For example, a value of 153005 cents
     * (which means 1530.05 €) will be returned as "1,530.05". Depending on the locale, other
     * formats such as "1.530,05" can be returned as well.
     * The value returned will NEVER include any currency symbol.
     *
     * @return  Formatted value.
     */
    fun getFormattedValue() : String {
        val numberFormat: NumberFormat = DecimalFormat("#,###.00")
        val formattedNumber: String = numberFormat.format(value.toDouble() / 100.0)
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
        return if (other is Transfer) {
            other.id == id
        } else {
            false
        }
    }

}
