package de.christian2003.chaching.plugin.presentation.view.analysis

import java.time.LocalDate
import java.util.Objects


class AnalysisPeriod(

    val startDate: LocalDate,

    val endDate: LocalDate

) {

    companion object {

        val CURRENT_YEAR = AnalysisPeriod(
            startDate = LocalDate.now().minusYears(1),
            endDate = LocalDate.now()
        )

        val LAST_YEAR = AnalysisPeriod(
            startDate = LocalDate.now().minusYears(2),
            endDate = LocalDate.now().minusYears(1)
        )

    }


    override fun hashCode(): Int {
        return Objects.hash(startDate, endDate)
    }


    override fun equals(other: Any?): Boolean {
        return if (other is AnalysisPeriod) {
            other.startDate == startDate && other.endDate == endDate
        } else {
            false
        }
    }

}
