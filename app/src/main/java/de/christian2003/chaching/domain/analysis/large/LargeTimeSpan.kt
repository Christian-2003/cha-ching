package de.christian2003.chaching.domain.analysis.large

import java.time.LocalDate


/**
 * Value object contains the analysis result for a time span.
 *
 * @param start             Start date of the time span.
 * @param end               End date of the time span.
 * @param normalizedDates   List of normalized dates for the time span.
 * @param incomes           Result of the incomes.
 * @param expenses          Result of the expenses.
 */
data class LargeTimeSpan(
    val start: LocalDate,
    val end: LocalDate,
    val normalizedDates: List<LocalDate>,
    val incomes: LargeTimeSpanResult,
    val expenses: LargeTimeSpanResult
) {

    /**
     * Initializes the value object and ensures that all properties are valid.
     */
    init {
        require(!end.isBefore(start)) { "End date cannot be before start date" }
    }

}
