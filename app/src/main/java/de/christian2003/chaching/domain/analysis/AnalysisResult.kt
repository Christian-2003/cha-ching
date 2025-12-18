package de.christian2003.chaching.domain.analysis

import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import java.time.LocalDate


/**
 * Value object models the result from the analysis.
 *
 * @param normalizedDates   List of normalized dates for which the analysis result is generated.
 * @param precision         Precision by which the normalized dates are generated.
 * @param totalIncomes      Summary of the total incomes.
 * @param totalExpenses     Summary of the total expenses.
 * @param typeResults       List of the analysis results by type.
 */
data class AnalysisResult(
    val normalizedDates: List<LocalDate>,
    val precision: AnalysisPrecision,
    val totalIncomes: ResultSummary,
    val totalExpenses: ResultSummary,
    val typeResults: List<TypeResult>
)
