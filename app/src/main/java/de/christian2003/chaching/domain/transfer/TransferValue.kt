package de.christian2003.chaching.domain.transfer

import java.time.LocalDate


/**
 * Value for a transfer.
 *
 * @param value     Stores the value of the transfer in cents. For example, a value of 15.05 € would
 *                  be stored as 1505 cents.
 * @param date      Stores the value date on which the transfer takes effect.
 * @param isSalary  Indicates whether the transfer is a salary.
 *                  Currently, this is unused. Later, this field should indicate whether the transfer
 *                  is an income (= true) or an expense (= false). This way, the value can always
 *                  store positive numbers.
 */
data class TransferValue(
    val value: Int,
    val date: LocalDate,
    val isSalary: Boolean = true
) {

    /**
     * Initializes the transfer value.
     */
    init {
        require(value >= 0) { "Value cannot be negative" }
    }

}
