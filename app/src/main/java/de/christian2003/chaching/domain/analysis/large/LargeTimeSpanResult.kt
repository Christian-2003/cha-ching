package de.christian2003.chaching.domain.analysis.large


/**
 * Value object contains the analysis result for either incomes OR expenses for a time span.
 *
 * @param totalSum                  Total sum of incomes OR expenses.
 * @param totalAvgPerTransfer       Total average per transfer of incomes OR expenses.
 * @param totalAvgPerNormalizedDate Total average of transfer values per normalized date (e.g.
 *                                  month, quarter or year) of incomes OR expenses.
 * @param typeResults               Analysis results per type.
 */
data class LargeTimeSpanResult(
    val totalSum: Double,
    val totalAvgPerTransfer: Double,
    val totalAvgPerNormalizedDate: Double,
    val typeResults: List<LargeTypeResult>
) {

    /**
     * Initializes the value object and ensures that all properties are valid.
     */
    init {
        require(totalSum >= 0.0) { "Total sum must not be negative" }
        require(totalAvgPerTransfer >= 0.0) { "Total average per transfer must not be negative" }
        require(totalAvgPerNormalizedDate >= 0.0) { "Total average per normalized date must not be negative" }
    }

}
