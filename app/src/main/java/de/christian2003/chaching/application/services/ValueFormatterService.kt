package de.christian2003.chaching.application.services

import de.christian2003.chaching.domain.transfer.TransferValue
import java.text.DecimalFormat
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
        val numberFormat: NumberFormat = DecimalFormat("#,###.00")
        return numberFormat.format(value)
    }


    /**
     * Formats the specified value and returns it as string.
     *
     * @param cents         Value in cents.
     * @param isNegative    Indicates whether the value is negative.
     * @return              Formatted value.
     */
    fun format(cents: Int, isNegative: Boolean = false): String {
        val numberFormat: NumberFormat = DecimalFormat("#,###.00")
        val formattedNumber: String = if (isNegative) {
            numberFormat.format(cents.toDouble() / -100.0)
        } else {
            numberFormat.format(cents.toDouble() / 100.0)
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
