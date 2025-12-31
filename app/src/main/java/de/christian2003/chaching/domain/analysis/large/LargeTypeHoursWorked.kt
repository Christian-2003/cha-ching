package de.christian2003.chaching.domain.analysis.large


/**
 * Value object contains the analysis result of the hours worked for a type.
 *
 * @param sum                   Sum of all transfers of a type.
 * @param avgPerTransfer        Average per transfer of a type.
 * @param avgPerNormalizedDate  Average per normalized date (e.g. month, quarter or year) of a type.
 */
data class LargeTypeHoursWorked(
    val sum: Int,
    val avgPerTransfer: Int,
    val avgPerNormalizedDate: Int
) {

    /**
     * Initializes the value object and ensures that all properties are valid.
     */
    init {
        require(sum >= 0) { "Sum must not be negative" }
        require(avgPerTransfer >= 0) { "Average per transfer must not be negative" }
        require(avgPerNormalizedDate >= 0) { "Average per normalized date must not be negative" }
    }

}
