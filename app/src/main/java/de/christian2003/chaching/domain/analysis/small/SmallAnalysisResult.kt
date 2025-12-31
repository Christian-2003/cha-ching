package de.christian2003.chaching.domain.analysis.small

import java.time.LocalDateTime


/**
 * Value object models the result of the small analysis.
 *
 * @param currentMonth                  Result for the current month.
 * @param previousMonth                 Result for the previous month can be used for comparison.
 * @param smallAnalysisProductPrices  Overview comparison connection.
 * @param time                          Date time at which the analysis finished.
 */
data class SmallAnalysisResult(
    val currentMonth: SmallMonthResult,
    val previousMonth: SmallMonthResult,
    val smallAnalysisProductPrices: SmallAnalysisProductPrices = SmallAnalysisProductPrices.getRandomComparisonConnection(),
    val time: LocalDateTime = LocalDateTime.now()
)
