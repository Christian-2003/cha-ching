package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.LargeAnalysisUseCase_Factory
import de.christian2003.chaching.application.analysis.large.dto.TransformerDateResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerTypeResult
import de.christian2003.chaching.domain.analysis.large.LargeDiagram
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpan
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpanResult
import de.christian2003.chaching.domain.analysis.large.LargeTypeResult
import java.time.LocalDate


/**
 * Generator can generate an instance of LargeTypeSpan based on a TransformerResult-instance
 * provided by the AnalysisDataTransformer.
 */
class LargeTimeSpanGenerator {

    /**
     * Generator used to generate the large type result.
     */
    private val typeResultGenerator: LargeTypeResultGenerator = LargeTypeResultGenerator()


    /**
     * Generates a LargeTimeSpan-instance based on the result of the AnalysisDataTransformer.
     *
     * @param start             Start date for the time span.
     * @param end               End date for the time span.
     * @param transformerResult Result from the transformer used for generation.
     * @return                  Generated LargeTimeSpan instance.
     */
    fun generate(
        start: LocalDate,
        end: LocalDate,
        transformerResult: TransformerResult
    ): LargeTimeSpan {
        val normalizedDates: List<LocalDate> = generateNormalizedDates(transformerResult)
        val incomes: LargeTimeSpanResult = generateTimeSpanResult(transformerResult.incomes)
        val expenses: LargeTimeSpanResult = generateTimeSpanResult(transformerResult.expenses)
        val budgetsPerNormalizedDate: LargeDiagram = generateBudgetsPerNormalizedDate(transformerResult)

        val result = LargeTimeSpan(
            start = start,
            end = end,
            normalizedDates = normalizedDates,
            incomes = incomes,
            expenses = expenses,
            budgetsPerNormalizedDate = budgetsPerNormalizedDate
        )

        return result
    }


    /**
     * Generates the list of normalized dates that is returned by the analysis. If neither incomes
     * nor expenses have any results, the returned list is empty.
     *
     * @param transformerResult Transformer result from which to generate the list of normalized
     *                          dates.
     * @return                  List of normalized dates.
     */
    private fun generateNormalizedDates(transformerResult: TransformerResult): List<LocalDate> {
        val normalizedDates: MutableList<LocalDate> = mutableListOf()

        val dateResults: List<TransformerDateResult> = if (transformerResult.incomes.isNotEmpty()) {
            transformerResult.incomes.first().dateResults
        } else if (transformerResult.expenses.isNotEmpty()) {
            transformerResult.expenses.first().dateResults
        } else {
            emptyList()
        }

        dateResults.forEach { dateResult ->
            normalizedDates.add(dateResult.normalizedDate)
        }

        return normalizedDates
    }


    /**
     * Generates a LargeTimeSpanResult-instance from the provided type results that was returned
     * from the AnalysisDataTransformer.
     *
     * @param transformerTypeResults    List of type results provided by the transformer.
     * @return                          Generated LargeTimeSpanResult-instance.
     */
    private fun generateTimeSpanResult(transformerTypeResults: List<TransformerTypeResult>): LargeTimeSpanResult {
        val typeResults: MutableList<LargeTypeResult> = mutableListOf()
        var totalSum = 0.0
        var totalTransferCount = 0

        transformerTypeResults.forEach { transformerTypeResult ->
            val typeResult: LargeTypeResult = typeResultGenerator.generate(transformerTypeResult)
            typeResults.add(typeResult)
            totalSum += typeResult.valueResult.sum
            totalTransferCount += typeResult.transferCount
        }

        val numberOfLocalizedDates: Int = if (transformerTypeResults.isEmpty()) {
            0
        } else {
            transformerTypeResults.first().dateResults.size
        }

        val totalAvgPerTransfer: Double = if (totalTransferCount == 0) {
            totalSum
        } else {
            totalSum / totalTransferCount
        }
        val totalAvgPerNormalizedDate: Double = if (numberOfLocalizedDates == 0) {
            totalSum
        } else {
            totalSum / numberOfLocalizedDates
        }

        val result = LargeTimeSpanResult(
            totalSum = totalSum,
            totalAvgPerTransfer = totalAvgPerTransfer,
            totalAvgPerNormalizedDate = totalAvgPerNormalizedDate,
            typeResults = typeResults
        )

        return result
    }


    /**
     * Generates the diagram which contains the budgets for each normalized date.
     *
     * @param transformerResult Transformer result.
     * @return                  Diagram with the budget values.
     */
    private fun generateBudgetsPerNormalizedDate(transformerResult: TransformerResult): LargeDiagram {
        val incomesList: MutableList<Int> = mutableListOf()
        transformerResult.incomes.firstOrNull()?.dateResults?.forEach { _ -> incomesList.add(0) }
        incomesList.forEachIndexed { index, value ->
            transformerResult.incomes.forEach { transformerResult ->
                incomesList[index] += transformerResult.dateResults.getOrNull(index)?.sum ?: 0
            }
        }

        val expensesList: MutableList<Int> = mutableListOf()
        transformerResult.expenses.firstOrNull()?.dateResults?.forEach { _ -> expensesList.add(0) }
        expensesList.forEachIndexed { index, value ->
            transformerResult.expenses.forEach { transformerResult ->
                expensesList[index] += transformerResult.dateResults.getOrNull(index)?.sum ?: 0
            }
        }

        val budgetsList: MutableList<Double> = mutableListOf()
        incomesList.forEachIndexed { index, incomeValue ->
            val budget: Double = centsToEuros(incomeValue - (expensesList.getOrNull(index) ?: 0))
            budgetsList.add(budget)
        }

        val result = LargeDiagram(
            values = budgetsList
        )

        return result
    }


    /**
     * Converts the passed cents value to euros.
     *
     * @param cents Cents value to convert.
     * @return      Euros.
     */
    private fun centsToEuros(cents: Int): Double {
        return cents.toDouble() / 100.0
    }

}
