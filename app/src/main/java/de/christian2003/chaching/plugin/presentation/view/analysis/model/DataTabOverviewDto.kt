package de.christian2003.chaching.plugin.presentation.view.analysis.model

/**
 * DTO for the overview of incomes OR expenses for the overview tab.
 *
 * @param sum                                               Total sum of transfers.
 * @param avgPerTransfer                                 Average sum per transaction.
 * @param avgPerNormalizedDate                              Average sum of transactions per normalized
 *                                                          date.
 * @param sumDifferenceToPreviousTimeSpan                   Difference to previous time span.
 * @param avgPerTransferDifferenceToPreviousTimeSpan     Difference average per transaction to
 *                                                          previous time span.
 * @param avgPerNormalizedDateDifferenceToPreviousTimeSpan  Difference average per normalized date to
 *                                                          previous time span.
 * @param transferCount                                     Number of analyzed transfers in the current
 *                                                          time span.
 */
data class DataTabOverviewDto(
    val sum: Double,
    val avgPerTransfer: Double,
    val avgPerNormalizedDate: Double,
    val sumDifferenceToPreviousTimeSpan: Double,
    val avgPerTransferDifferenceToPreviousTimeSpan: Double,
    val avgPerNormalizedDateDifferenceToPreviousTimeSpan: Double,
    val transferCount: Int
)
