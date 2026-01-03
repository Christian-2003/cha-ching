package de.christian2003.chaching.domain.analysis.large

import java.util.UUID


/**
 * Value object contains the analysis result for a single type.
 *
 * @param typeId            ID of the type whose result is stored in this instance.
 * @param valueResult       Analysis result for the transfer values of this type.
 * @param hoursWorkedResult Analysis result for the transfers hours worked of this type.
 * @param transferCount     Number of transfers that were analyzed for this type.
 * @param valuesDiagram     Diagram containing the transfer values for each normalized date.
 * @param cumulatedDiagram  Diagram containing the cumulated transfer valued for each normalized date.
 */
data class LargeTypeResult(
    val typeId: UUID,
    val valueResult: LargeTypeValue,
    val hoursWorkedResult: LargeTypeHoursWorked,
    val transferCount: Int,
    val valuesDiagram: LargeDiagram,
    val cumulatedDiagram: LargeDiagram
) {

    /**
     * Initializes the value object and ensures that all properties are valid.
     */
    init {
        require(transferCount >= 0) { "Transfer count must not be negative" }
    }

}
