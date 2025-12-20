package de.christian2003.chaching.application.analysis.small

import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisData
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.domain.analysis.small.SmallMonthResult
import de.christian2003.chaching.domain.analysis.small.SmallTypeResult
import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject


/**
 * Use case for the small and less powerful analysis. The result is displayed on the MainScreen.
 *
 * @param repository    Repository through which to access data.
 */
class SmallAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {

    /**
     * Max number of type results.
     */
    private var typeResultLimit: Int = 3


    /**
     * Analyzes the data from the last month.
     *
     * @param month Month for which to generate the analysis.
     * @return      Analysis result.
     */
    suspend fun analyzeData(month: LocalDate): SmallAnalysisResult {
        //Get start and end dates:
        val start: LocalDate = month.with(TemporalAdjusters.firstDayOfMonth())
        val end: LocalDate = month.with(TemporalAdjusters.lastDayOfMonth())

        //Get result of current month:
        val currentMonthResult: SmallMonthResult = generateSmallMonthResult(start, end)

        //Get time period for last month:
        val previousStart: LocalDate = start.minusMonths(1)
        val previousEnd: LocalDate = end.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth())

        //Get result for previous month:
        val previousMonthResult: SmallMonthResult = generateSmallMonthResult(previousStart, previousEnd)

        //Return the final result:
        val result = SmallAnalysisResult(
            currentMonth = currentMonthResult,
            previousMonth = previousMonthResult
        )
        return result
    }


    /**
     * Generates the mont result for the month in between the defined start and end dates.
     *
     * @param start Start date for the month.
     * @param end   End date for the month.
     * @return      Result for the month.
     */
    private suspend fun generateSmallMonthResult(start: LocalDate, end: LocalDate): SmallMonthResult {
        val transfers: List<Transfer> = repository.getAllTransfersInDateRange(start, end).first()

        //Generate results for incomes and expenses:
        val incomes: SmallAnalysisData = generateSmallAnalysisData(transfers, true)
        val expenses: SmallAnalysisData = generateSmallAnalysisData(transfers, false)

        //Return month result:
        val monthResult = SmallMonthResult(
            start = start,
            end = end,
            budget = incomes.totalSum - expenses.totalSum,
            incomes = incomes,
            expenses = expenses
        )
        return monthResult
    }


    /**
     * Generates the small analysis data for the passed list of transfers. The result contains either
     * only incomes or only expenses, depending on what to summarize.
     *
     * @param transfers List of transfers to summarize.
     * @param isSalary  Whether to regard only incomes (= true) or only expenses (= false).
     * @return          Summarized data for the incomes or expenses.
     */
    private fun generateSmallAnalysisData(transfers: List<Transfer>, isSalary: Boolean): SmallAnalysisData {
        //Transfers grouped by type:
        val groupedTransfers: Map<UUID, List<Transfer>> = transfers.groupBy { it.type }

        //Summarize transfer values for incomes:
        var totalSum = 0
        val typeResults: MutableList<SmallTypeResult> = mutableListOf()
        groupedTransfers.forEach { typeId, transfers ->
            var sum = 0
            transfers.forEach { transfer ->
                if (transfer.transferValue.isSalary == isSalary) {
                    sum += transfer.transferValue.value
                }
            }

            if (sum > 0) {
                val typeResult = SmallTypeResult(
                    typeId = typeId,
                    sum = centsToEuros(sum)
                )
                typeResults.add(typeResult)
            }
            totalSum += sum
        }

        //Sort type results by value:
        typeResults.sortByDescending { it.sum }

        //Make sure there are no more type results than limit provided. If there are too many results,
        //the last few are summarized into a single result, so that the list size is equal to
        //'typeResultLimit + 1' afterwards, where the additional type result summarized all dropped
        //type results:
        if (typeResults.size > typeResultLimit) {
            var sum = 0.0
            while (typeResults.size > typeResultLimit) {
                val typeResult: SmallTypeResult = typeResults.last()
                sum += typeResult.sum
                typeResults.remove(typeResult)
            }
            if (sum > 0.0) {
                val summarizedTypeResult = SmallTypeResult(
                    typeId = null,
                    sum = sum
                )
                typeResults.add(summarizedTypeResult)
            }
        }

        val analysisData = SmallAnalysisData(
            totalSum = centsToEuros(totalSum),
            typeResults = typeResults
        )
        return analysisData
    }


    /**
     * Converts the cents value to euros.
     *
     * @param cents Cents value.
     * @return      Euro value.
     */
    private fun centsToEuros(cents: Int): Double {
        return cents.toDouble() / 100.0
    }

}
