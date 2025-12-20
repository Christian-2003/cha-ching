package de.christian2003.chaching.application.services

import de.christian2003.chaching.domain.transfer.TransferValue
import java.text.NumberFormat


/**
 * Service to format values.
 */
class ValueFormatterService {

    /**
     * Formats the specified double value and returns it as a string.
     *
     * @param value Value to format.
     * @return      Formatted value.
     */
    fun format(value: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
        return formatter.format(value)
    }


    /**
     * Formats the specified value and returns it as string.
     *
     * @param cents         Value in cents.
     * @param isNegative    Indicates whether the value is negative.
     * @return              Formatted value.
     */
    fun format(cents: Int, isNegative: Boolean = false): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
        val formattedNumber: String = if (isNegative) {
            formatter.format(cents.toDouble() / -100.0)
        } else {
            formatter.format(cents.toDouble() / 100.0)
        }
        return formattedNumber
    }


    /**
     * Formats the specified transfer value and returns it as a string.
     *
     * @param value Value to format.
     * @return      Formatted value.
     */
    fun format(value: TransferValue): String {
        return format(value.value, !value.isSalary)
    }

}
