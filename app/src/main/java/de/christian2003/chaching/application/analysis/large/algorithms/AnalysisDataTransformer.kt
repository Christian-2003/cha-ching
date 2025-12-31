package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.dto.SummarizerGroupedTypeResult
import de.christian2003.chaching.application.analysis.large.dto.SummarizerTypeResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerDateResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerTypeResult
import java.time.LocalDate
import java.util.UUID


/**
 * Transformer transforms the result from the summarizer. The summarizer returns the result grouped
 * as follows:
 * Type   ->   Date   ->   Incomes / Expenses   ->   Actual values
 *
 * The transformer transforms the result as follows:
 * Incomes / Expenses   ->   Type    ->   Date   ->   Actual values
 *
 * The transformed format resembles the final analysis result more closely, which is why we transform
 * the data in this step.
 */
class AnalysisDataTransformer {

    /**
     * Transforms the result from the AnalysisDataSummarizer. The transformed result is no longer
     * grouped by type on the top-level, but rather by incomes and expenses.
     *
     * @param groupedTypeSums   Result from the summarizer to transform.
     * @return                  Transformed result.
     */
    fun transform(groupedTypeSums: Map<UUID, List<SummarizerGroupedTypeResult>>): TransformerResult {
        val incomeResults: MutableList<TransformerTypeResult> = mutableListOf()
        val expenseResults: MutableList<TransformerTypeResult> = mutableListOf()

        groupedTypeSums.forEach { typeId, groupedTypeResults ->
            val incomeResult: TransformerTypeResult = generateTypeResult(typeId, groupedTypeResults, true)
            val expenseResult: TransformerTypeResult = generateTypeResult(typeId, groupedTypeResults, false)

            incomeResults.add(incomeResult)
            expenseResults.add(expenseResult)
        }

        val result = TransformerResult(
            incomes = incomeResults,
            expenses = expenseResults
        )

        return result
    }


    /**
     * Generates a type result for the specified type using the provided list of grouped type results
     * from the data summarizer.
     *
     * @param typeId                ID of the type for which to generate the result.
     * @param groupedTypeResults    List of grouped type results to transform.
     * @param forIncome             Flag indicates whether the incomes (= true) or expenses (= false)
     *                              from the grouped type results shall be transformed.
     * @return                      Transformed type result.
     */
    private fun generateTypeResult(typeId: UUID, groupedTypeResults: List<SummarizerGroupedTypeResult>, forIncome: Boolean): TransformerTypeResult {
        val dateResults: MutableList<TransformerDateResult> = mutableListOf()

        groupedTypeResults.forEach { groupedTypeResult ->
            val typeResult: SummarizerTypeResult = if (forIncome) {
                groupedTypeResult.incomes
            } else {
                groupedTypeResult.expenses
            }

            val dateResult: TransformerDateResult = generateDateResult(groupedTypeResult.date, typeResult)
            dateResults.add(dateResult)
        }

        val result = TransformerTypeResult(
            typeId = typeId,
            dateResults = dateResults
        )

        return result
    }


    /**
     * Generates a date result.
     *
     * @param date          Date for which to generate the result.
     * @param typeResult    Type result from the summarizer from which to generate the new result.
     * @return              Transformed date result.
     */
    private fun generateDateResult(date: LocalDate, typeResult: SummarizerTypeResult): TransformerDateResult {
        val result = TransformerDateResult(
            normalizedDate = date,
            sum = typeResult.sum,
            count = typeResult.count,
            hoursWorked = typeResult.hoursWorked
        )

        return result
    }

}
