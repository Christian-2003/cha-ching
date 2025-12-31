package de.christian2003.chaching.plugin.presentation.view.analysis.model

/**
 * DTO for the overview of incomes OR expenses for the overview tab.
 *
 * @param sum                                               Total sum of transfers.
 * @param avgPerTransaction                                 Average sum per transaction.
 * @param avgPerNormalizedDate                              Average sum of transactions per normalized
 *                                                          date.
 * @param sumDifferenceToPreviousTimeSpan                   Difference to previous time span.
 * @param avgPerTransactionDifferenceToPreviousTimeSpan     Difference average per transaction to
 *                                                          previous time span.
 * @param avgPerNormalizedDateDifferenceToPreviousTimeSpan  Difference average per normalized date to
 *                                                          previous time span.
 */
data class DataTabOverviewDto(
    val sum: Double,
    val avgPerTransaction: Double,
    val avgPerNormalizedDate: Double,
    val sumDifferenceToPreviousTimeSpan: Double,
    val avgPerTransactionDifferenceToPreviousTimeSpan: Double,
    val avgPerNormalizedDateDifferenceToPreviousTimeSpan: Double
)
