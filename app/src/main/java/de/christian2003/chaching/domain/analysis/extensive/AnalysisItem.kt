package de.christian2003.chaching.domain.analysis.extensive

import java.time.LocalDate
import java.util.Objects


/**
 * Stores an analysis item.
 */
data class AnalysisItem(

    /**
     * Stores the value for the item in cents.
     */
    val value: Int,

    /**
     * Stores the local date of the item. This is always the first day of the analysis precision.
     * If analysis precision is monthly, this is the first day of the month, if analysis precision
     * is quarterly, this is the first day of the quarter and if analysis precision is yearly, this
     * is the first day of the year.
     */
    val date: LocalDate

) {

    /**
     * Tests whether the instance passed is equal to this analysis item.
     *
     * @param other Object to test for equality.
     * @return      Whether the object passed is equal to this analysis item.
     */
    override fun equals(other: Any?): Boolean {
        return if (other is AnalysisItem) {
            other.value == value && other.date == date
        } else {
            false
        }
    }


    /**
     * Hash code for the analysis item.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        return Objects.hash(value, date)
    }

}
