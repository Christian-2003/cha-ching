package de.christian2003.chaching.domain.analysis.small

import java.time.LocalDate


/**
 * Value object contains the result of the small analysis for an entire month.
 *
 * @param start     First date of the analyzed period.
 * @param end       Last date of the analyzed period.
 * @param budget    Budget for the month.
 * @param incomes   Result of incomes.
 * @param expenses  Result of expenses.
 */
data class SmallMonthResult(
    val start: LocalDate,
    val end: LocalDate,
    val budget: Double,
    val incomes: SmallAnalysisData,
    val expenses: SmallAnalysisData
)
