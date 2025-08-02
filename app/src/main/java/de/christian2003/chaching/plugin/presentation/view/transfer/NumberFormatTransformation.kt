package de.christian2003.chaching.plugin.presentation.view.transfer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormatSymbols
import java.util.Locale


/**
 * Implements visual transformation to use with a text field when formatting the text field value
 * to a decimal format.
 */
class NumberFormatTransformation(

    /**
     * Locale on which to base the decimal format to use for transformation.
     */
    private val locale: Locale = Locale.getDefault(),


): VisualTransformation {

    /**
     * Separator used for decimal places, e.g. (1.23 or 1,23).
     */
    private val decimalSeparator: Char = DecimalFormatSymbols.getInstance(locale).decimalSeparator

    /**
     * Separator used for grouping large numbers, e.g. (1,234,567 or 1.234.567).
     */
    private val groupingSeparator: Char = DecimalFormatSymbols.getInstance(locale).groupingSeparator


    /**
     * Filters the text passed to a format to display to the user within a text field.
     *
     * @param text  Text to be formatted.
     * @return      Formatted text.
     */
    override fun filter(text: AnnotatedString): TransformedText {
        val original: String = text.text

        val parts: List<String> = original.split(decimalSeparator, limit = 2)
        val integerPartRaw: String = parts.getOrNull(0)?.filter { it.isDigit() } ?: ""
        val decimalPartRaw: String = parts.getOrNull(1) ?: ""

        val integerPartFormatted: String = integerPartRaw.reversed().chunked(3).joinToString(groupingSeparator.toString()).reversed()

        val formatted: String = if (parts.size > 1) {
            "$integerPartFormatted$decimalSeparator$decimalPartRaw"
        } else {
            integerPartFormatted
        }

        val originalToFormatted: MutableMap<Int, Int> = mutableMapOf()
        var rawIndex = 0
        var formattedIndex = 0

        while (rawIndex < integerPartRaw.length && formattedIndex < integerPartFormatted.length) {
            originalToFormatted[rawIndex] = formattedIndex
            if (integerPartFormatted[formattedIndex] == groupingSeparator) {
                formattedIndex++
                continue
            }
            rawIndex++
            formattedIndex++
        }
        originalToFormatted[integerPartRaw.length] = integerPartFormatted.length

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    var transformedOffset: Int = if (offset <= integerPartRaw.length) {
                        originalToFormatted[offset] ?: formatted.length
                    } else {
                        val decimalOffset = offset - integerPartRaw.length - 1
                        integerPartFormatted.length + 1 + decimalOffset
                    }
                    if (transformedOffset > formatted.length) {
                        transformedOffset = formatted.length
                    }
                    return transformedOffset
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return if (offset <= integerPartFormatted.length) {
                        var raw = 0
                        var count = 0
                        while (raw < integerPartFormatted.length && count < offset) {
                            if (integerPartFormatted[raw] != groupingSeparator) {
                                count++
                            }
                            raw++
                        }
                        count
                    } else {
                        val decimalOffset = offset - integerPartFormatted.length - 1
                        integerPartRaw.length + 1 + decimalOffset
                    }
                }
            }
        )
    }

}
