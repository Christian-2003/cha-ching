package de.christian2003.chaching.application.services

import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import java.time.LocalDate


/**
 * Service to get the normalized date for any LocalDate-instance.
 */
class NormalizedDateConverterService {

    /**
     * Based on the analysis precision, this converts the passed local date as follows:
     * - AnalysisPrecision.MONTH:   Date with the first day of the month is returned.
     * - AnalysisPrecision.QUARTER: Date with the first day of the quarter is returned.
     * - AnalysisPrecision.YEAR:    Date with the first day of the year is returned.
     *
     * @param date      Date to convert.
     * @param precision Precision for which to generate the normalized date.
     * @return          Converted date.
     */
    fun getNormalizedDate(date: LocalDate, precision: AnalysisPrecision): LocalDate {
        when (precision) {
            AnalysisPrecision.Month -> {
                return date.withDayOfMonth(1)
            }
            AnalysisPrecision.Quarter -> {
                val quarterMonth = ((date.month.value - 1) / 3) * 3 + 1 //Q1=1, Q2=4, Q3=7, Q4=10
                return date.withDayOfMonth(1).withMonth(quarterMonth)
            }
            AnalysisPrecision.Year -> {
                return date.withDayOfYear(1)
            }
        }
    }

}
