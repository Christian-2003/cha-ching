package de.christian2003.chaching.domain.analysis.small


/**
 * Value object contains the result for either incomes OR expenses of the short analysis. It stores
 * the total sum as well as the result by each type.
 *
 * @param totalSum      Total sum of incomes OR expenses.
 * @param typeResults   Sum of incomes OR expenses for each type.
 */
data class SmallAnalysisData(
    val totalSum: Double,
    val typeResults: List<SmallTypeResult>
)
