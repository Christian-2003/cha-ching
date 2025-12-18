package de.christian2003.chaching.domain.analysis

import java.util.UUID


/**
 * Value object models the analysis result for a single type. Examples are:
 * - Summary of the type 'Salary'
 * - Summary of the type 'Taxes'
 * - Summary of the type 'Insurance'
 *
 * @param typeId    ID of the type for this result.
 * @param incomes   Summary of the incomes for this type.
 * @param expenses  Summary of the expenses for this type.
 */
data class TypeResult(
    val typeId: UUID,
    val incomes: TypeResultSummary,
    val expenses: TypeResultSummary
)
