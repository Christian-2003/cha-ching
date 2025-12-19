package de.christian2003.chaching.plugin.presentation.view.analysis

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Objects


/**
 * Analysis period models a period of time that can be selected by the user for analysis.
 */
class AnalysisPeriod(

    /**
     * Start date of the time period.
     */
    val startDate: LocalDate,

    /**
     * End date of the time period.
     */
    val endDate: LocalDate

) {

    /**
     * Returns the hash code for the instance.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        return Objects.hash(startDate, endDate)
    }


    /**
     * Tests whether the instance passed is identical to this instance.
     *
     * @param other Instance to test.
     * @return      Whether both instances are identical.
     */
    override fun equals(other: Any?): Boolean {
        return if (other is AnalysisPeriod) {
            other.startDate == startDate && other.endDate == endDate
        } else {
            false
        }
    }


    companion object {

        /**
         * Default instance storing the time period for the last 12 months.
         */
        val CURRENT_YEAR = AnalysisPeriod(
            startDate = LocalDate.now().minusYears(1).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1),
            endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
        )

        /**
         * Default instance storing a time period of 12 months one year ago.
         */
        val LAST_YEAR = AnalysisPeriod(
            startDate = LocalDate.now().minusYears(2).with(TemporalAdjusters.lastDayOfMonth()).plusDays(1),
            endDate = LocalDate.now().minusYears(2).with(TemporalAdjusters.lastDayOfMonth())
        )

    }
}
