package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.application.analysis.dto.DataLines
import de.christian2003.chaching.application.analysis.dto.GroupedStatisticsResult
import de.christian2003.chaching.application.analysis.large.dto.SummarizerGroupedTypeResult
import de.christian2003.chaching.application.analysis.large.algorithms.AnalysisDataSummarizer
import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.application.services.NormalizedDateConverterService
import de.christian2003.chaching.domain.analysis.AnalysisDiagram
import de.christian2003.chaching.domain.analysis.AnalysisResult
import de.christian2003.chaching.domain.analysis.ResultSummary
import de.christian2003.chaching.domain.analysis.TypeResult
import de.christian2003.chaching.domain.analysis.TypeResultSummary
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject


class ExtensiveAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository,
    private val normalizedDateConverterService: NormalizedDateConverterService
) {

    suspend fun analyzeData(precision: AnalysisPrecision, start: LocalDate, end: LocalDate): AnalysisResult {
        //Get all transfers:
        val transfers: List<Transfer> = repository.getAllTransfersInDateRange(start, end).first()
        val types: List<Type> = repository.getAllTypes().first()

        //Summarize data:
        val analysisDataSummarizer = AnalysisDataSummarizer(precision, start, end, normalizedDateConverterService)
        val summarizedResults: Map<UUID, List<SummarizerGroupedTypeResult>> = analysisDataSummarizer.summarizeData(transfers, types)

        //Get list of normalized dates:
        val normalizedDates: List<LocalDate> = getListOfNormalizedDates(summarizedResults)

        //Convert result to type results:
        val typeResults: List<TypeResult> = generateTypeResults(summarizedResults)

        //Generate total summaries:
        val totalIncomes: ResultSummary = generateIncomesResultSummary(summarizedResults, normalizedDates.size)
        val totalExpenses: ResultSummary = generateExpensesResultSummary(summarizedResults, normalizedDates.size)

        //Create final result:
        val analysisResult = AnalysisResult(
            normalizedDates = normalizedDates,
            precision = precision,
            typeResults = typeResults,
            totalIncomes = totalIncomes,
            totalExpenses = totalExpenses
        )

        return analysisResult
    }


    private fun generateTypeResults(summarizedResults: Map<UUID, List<SummarizerGroupedTypeResult>>): List<TypeResult> {
        val groupedTypeSumToDataLineConverter = GroupedTypeSumToDataLineConverter()
        val groupedTypeSumsToStatistics = GroupedTypeSumsToStatistics()
        val cumulatedDateLineGenerator = CumulatedDateLineGenerator()

        val typeResults: MutableList<TypeResult> = mutableListOf()

        summarizedResults.forEach { typeId, groupedTypeSums ->
            val dataLines: DataLines = groupedTypeSumToDataLineConverter.getDataLine(groupedTypeSums)
            val statistics: GroupedStatisticsResult = groupedTypeSumsToStatistics.toStatistics(groupedTypeSums)
            val cumulatedIncomes: List<Int> = cumulatedDateLineGenerator.generateCumulatedDataLine(dataLines.incomes)
            val cumulatedExpenses: List<Int> = cumulatedDateLineGenerator.generateCumulatedDataLine(dataLines.expenses)

            val typeResult = TypeResult(
                typeId = typeId,
                incomes = TypeResultSummary(
                    transferSum = centsToEuros(statistics.incomes.total),
                    transferCount = statistics.incomes.totalCount,
                    transferAvg = centsToEuros(statistics.incomes.averagePerTransfer),
                    normalizedDateAvg = centsToEuros(statistics.incomes.averagePerNormalizedDate),
                    valuesDiagram = AnalysisDiagram(
                        values = centsListToEurosList(dataLines.incomes),
                        max = centsToEuros(dataLines.incomes.max()),
                        min = centsToEuros(dataLines.incomes.min())
                    ),
                    cumulatedDiagram = AnalysisDiagram(
                        values = centsListToEurosList(cumulatedIncomes),
                        max = centsToEuros(cumulatedIncomes.max()),
                        min = centsToEuros(cumulatedIncomes.min())
                    )
                ),
                expenses = TypeResultSummary(
                    transferSum = centsToEuros(statistics.expenses.total),
                    transferCount = statistics.expenses.totalCount,
                    transferAvg = centsToEuros(statistics.expenses.averagePerTransfer),
                    normalizedDateAvg = centsToEuros(statistics.expenses.averagePerNormalizedDate),
                    valuesDiagram = AnalysisDiagram(
                        values = centsListToEurosList(dataLines.expenses),
                        max = centsToEuros(dataLines.expenses.max()),
                        min = centsToEuros(dataLines.expenses.min())
                    ),
                    cumulatedDiagram = AnalysisDiagram(
                        values = centsListToEurosList(cumulatedExpenses),
                        max = centsToEuros(cumulatedExpenses.max()),
                        min = centsToEuros(cumulatedExpenses.min())
                    )
                )
            )

            typeResults.add(typeResult)
        }

        return typeResults
    }


    private fun generateIncomesResultSummary(summarizedResults: Map<UUID, List<SummarizerGroupedTypeResult>>, normalizedDateCount: Int): ResultSummary {
        var incomesTransferSum = 0
        var incomesTransferCount = 0

        summarizedResults.forEach { _, groupedTypeSum ->
            groupedTypeSum.forEach { groupedTypeSum ->
                incomesTransferSum += groupedTypeSum.incomes.sum
                incomesTransferCount += groupedTypeSum.incomes.count
            }
        }

        val incomesTransferAvg: Int = if (incomesTransferCount > 0) {
            incomesTransferSum / incomesTransferCount
        } else {
            incomesTransferSum
        }

        val incomesNormalizedDateAvg: Int = if (normalizedDateCount > 0) {
            incomesTransferSum / normalizedDateCount
        } else {
            incomesTransferSum
        }

        val resultSummary = ResultSummary(
            transferSum = centsToEuros(incomesTransferSum),
            transferCount = incomesTransferCount,
            transferAvg = centsToEuros(incomesTransferAvg),
            normalizedDateAvg = centsToEuros(incomesNormalizedDateAvg)
        )

        return resultSummary
    }


    private fun generateExpensesResultSummary(summarizedResults: Map<UUID, List<SummarizerGroupedTypeResult>>, normalizedDateCount: Int): ResultSummary {
        var expensesTransferSum = 0
        var expensesTransferCount = 0

        summarizedResults.forEach { _, groupedTypeSum ->
            groupedTypeSum.forEach { groupedTypeSum ->
                expensesTransferSum += groupedTypeSum.expenses.sum
                expensesTransferCount += groupedTypeSum.expenses.count
            }
        }

        val expensesTransferAvg: Int = if (expensesTransferCount > 0) {
            expensesTransferCount / expensesTransferCount
        } else {
            expensesTransferCount
        }

        val expensesNormalizedDateAvg: Int = if (normalizedDateCount > 0) {
            expensesTransferSum / normalizedDateCount
        } else {
            expensesTransferSum
        }

        val resultSummary = ResultSummary(
            transferSum = centsToEuros(expensesTransferSum),
            transferCount = expensesTransferCount,
            transferAvg = centsToEuros(expensesTransferAvg),
            normalizedDateAvg = centsToEuros(expensesNormalizedDateAvg)
        )

        return resultSummary
    }


    private fun getListOfNormalizedDates(summarizedResults: Map<UUID, List<SummarizerGroupedTypeResult>>): List<LocalDate> {
        val normalizedDates: MutableList<LocalDate> = mutableListOf()

        if (summarizedResults.isNotEmpty()) {
            //Get normalized dates:
            val groupedTypeSums: List<SummarizerGroupedTypeResult> = summarizedResults.values.first()
            if (groupedTypeSums.isNotEmpty()) {
                groupedTypeSums.forEach { groupedTypeSum ->
                    normalizedDates.add(groupedTypeSum.date)
                }
            }
        }

        return normalizedDates
    }


    /**
     * Converts a cents value to a euro value.
     *
     * @param cents Cents value to convert.
     * @return      Euro value.
     */
    private fun centsToEuros(cents: Int): Double {
        return cents.toDouble() / 100.0
    }


    /**
     * Converts a list of cents to a list of euros.
     *
     * @param centsList List of cents to convert.
     * @return          List of euros.
     */
    private fun centsListToEurosList(centsList: List<Int>): List<Double> {
        val eurosList: MutableList<Double> = mutableListOf()

        centsList.forEach { cents ->
            eurosList.add(centsToEuros(cents))
        }

        return eurosList
    }

}
