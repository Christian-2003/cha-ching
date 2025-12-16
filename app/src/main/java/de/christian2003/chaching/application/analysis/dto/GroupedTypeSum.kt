package de.christian2003.chaching.application.analysis.dto

import java.time.LocalDate


/**
 * Stores the summary for a single type for a precision.
 *
 * @param date      Date for which the data is summarized.
 * @param incomes   Type sum for the incomes.
 * @param expenses  Type sum for the expenses.
 */
data class GroupedTypeSum(
    val date: LocalDate,
    val incomes: TypeSum,
    val expenses: TypeSum
)
