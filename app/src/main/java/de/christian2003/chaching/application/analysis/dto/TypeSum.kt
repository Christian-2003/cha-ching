package de.christian2003.chaching.application.analysis.dto


/**
 * Summarizes the transfers for a specific type.
 *
 * @param sum           Sum of the values of the transfers.
 * @param count         Count of the transfers that were summarized.
 * @param hoursWorked   Hours worked for the type.
 */
data class TypeSum(
    val sum: Int,
    val count: Int,
    val hoursWorked: Int
)
