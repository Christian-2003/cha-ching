package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.domain.analysis.extensive.AnalysisDiagramLine
import de.christian2003.chaching.domain.analysis.extensive.AnalysisItem
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.extensive.AnalysisResult
import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import kotlin.collections.MutableMap


/**
 * Analysis result generates the result for all transfers within the database in a specified period
 * of time.
 */
@Deprecated("Use use case instead")
class AnalysisServiceImpl @Inject constructor(

    /**
     * Repository through which to access transfers.
     */
    private val transferRepository: TransferRepository,

    /**
     * Repository through which to access types.
     */
    private val typeRepository: TypeRepository

): AnalysisService {

    /**
     * Builder to build the analysis result.
     */
    private lateinit var analysisResultBuilder: AnalysisResult.Builder

    /**
     * List of transfers grouped by type.
     */
    private val transfersByType: MutableMap<Type, MutableList<AnalysisItem>> = mutableMapOf()

    /**
     * List of cumulated transfers grouped by types.
     */
    private val cumulatedTransfersByType: MutableMap<Type, MutableList<AnalysisItem>> = mutableMapOf()


    /**
     * Analyzes all data in between the passed start and end days. The analysis results will be
     * generated for the precision specified.
     *
     * @param startDay  First day of the analysis results to include.
     * @param endDay    Last day of the analysis results to include.
     * @param precision Precision for the analysis results.
     * @return          Analysis result.
     */
    override suspend fun analyzeData(startDay: LocalDate, endDay: LocalDate, precision: AnalysisPrecision): AnalysisResult {
        val transfers: List<Transfer> = transferRepository.getAllTransfersInDateRange(startDay, endDay).first()
        val types: List<Type> = typeRepository.getAllTypes().first()

        resetServiceForAnalysis(types, precision)

        //Group transfers by analysis precision:
        val transfersGroupedByPrecision: Map<LocalDate, List<Transfer>> = transfers.reversed().groupBy { transfer ->
            when (precision) {
                AnalysisPrecision.Quarter -> transfer.transferValue.date.withMonth(transfer.transferValue.date.month.value - ((transfer.transferValue.date.month.value - 1) % 3)).withDayOfMonth(1)
                AnalysisPrecision.Year -> transfer.transferValue.date.withMonth(1).withDayOfMonth(1)
                else -> transfer.transferValue.date.withDayOfMonth(1)
            }
        }

        calculateAnalysisResultsForPrecision(transfersGroupedByPrecision, types)
        fillBlankPrecisionSpaces(precision)
        createAnalysisResult()

        return analysisResultBuilder.build()
    }


    /**
     * Creates the analysis result. After this method is called, "analysisResultBuilder.build()" can
     * be called to build a valid analysis result.
     */
    private fun createAnalysisResult() {
        transfersByType.forEach { (type, analysisItems) ->
            analysisResultBuilder.addTransferByType(AnalysisDiagramLine(analysisItems, type))
            var totalForType = 0
            var numberOfTransfers = 0
            analysisItems.forEach { analysisItem ->
                totalForType += analysisItem.value
                numberOfTransfers++
            }
            analysisResultBuilder.addTotalTransferByType(type, totalForType)
            analysisResultBuilder.addAverageTransferByType(type, totalForType / numberOfTransfers)
        }
        cumulatedTransfersByType.forEach { (type, analysisItems) ->
            analysisResultBuilder.addCumulatedTransferByType(AnalysisDiagramLine(analysisItems, type))
        }
    }


    /**
     * Fills blank months / quarters / years (depending on precision) within the current analysis
     * results. After this method is called, the analysis results will have AnalysisItems for each
     * date depending on the precision.
     *
     * @param precision Precision for which to insert dates.
     */
    private fun fillBlankPrecisionSpaces(precision: AnalysisPrecision) {
        val firstDate: LocalDate = findFirstPrecisionDate()
        val lastDate: LocalDate = findLastPrecisionDate(firstDate)
        val allDates: List<LocalDate> = createListWithAllDates(firstDate, lastDate, precision)

        transfersByType.forEach { (type, analysisItems) ->
            val fullDates: MutableList<AnalysisItem> = mutableListOf()
            allDates.forEach { date ->
                var analysisItemWithDate: AnalysisItem? = null
                analysisItems.forEach { analysisItem ->
                    if (analysisItem.date == date) {
                        analysisItemWithDate = analysisItem
                        return@forEach
                    }
                }
                fullDates.add(analysisItemWithDate ?: AnalysisItem(0, date))
            }
            transfersByType[type] = fullDates
        }

        cumulatedTransfersByType.forEach { (type, analysisItems) ->
            val fullDates: MutableList<AnalysisItem> = mutableListOf()
            var cumulatedValue = 0
            allDates.forEach { date ->
                var analysisItemWithDate: AnalysisItem? = null
                analysisItems.forEach { analysisItem ->
                    if (analysisItem.date == date) {
                        analysisItemWithDate = analysisItem
                        cumulatedValue = analysisItem.value
                        return@forEach
                    }
                }
                fullDates.add(analysisItemWithDate ?: AnalysisItem(cumulatedValue, date))
            }
            cumulatedTransfersByType[type] = fullDates
        }
    }


    /**
     * Finds the first date within the current analysis results.
     *
     * @return  First date.
     */
    private fun findFirstPrecisionDate(): LocalDate {
        var firstDate: LocalDate = LocalDate.now()
        transfersByType.forEach { (_, analysisItems) ->
            analysisItems.forEach { analysisItem ->
                if (analysisItem.date.toEpochDay() < firstDate.toEpochDay()) {
                    firstDate = analysisItem.date
                }
            }
        }
        return firstDate
    }


    /**
     * Finds the last date within the current analysis results.
     *
     * @param startDate First possible date to be returned.
     * @return          Last date.
     */
    private fun findLastPrecisionDate(startDate: LocalDate): LocalDate {
        var lastDate: LocalDate = startDate
        transfersByType.forEach { (_, analysisItems) ->
            analysisItems.forEach { analysisItem ->
                if (analysisItem.date.toEpochDay() > lastDate.toEpochDay()) {
                    lastDate = analysisItem.date
                }
            }
        }
        return lastDate
    }


    /**
     * Creates a list containing dates (according to the precision specified) in the range specified.
     *
     * @param firstDate First date in the range to be included in the list.
     * @param lastDate  Last date in the range to be included in the list.
     * @param precision Precision for the dates in the list.
     * @return          List with all dates of the passed precision in the range specified.
     */
    private fun createListWithAllDates(firstDate: LocalDate, lastDate: LocalDate, precision: AnalysisPrecision): List<LocalDate> {
        val dates: MutableList<LocalDate> = mutableListOf()
        when (precision) {
            AnalysisPrecision.Month -> {
                var month = firstDate.month.value
                var year = firstDate.year
                while ((year < lastDate.year) || (month <= lastDate.month.value && year == lastDate.year)) {
                    dates.add(LocalDate.of(year, month, 1))
                    month++
                    if (month > 12) {
                        month = 1
                        year++
                    }
                }
            }
            AnalysisPrecision.Quarter -> {
                var quarter = getQuarterForDate(firstDate)
                var year = firstDate.year
                while ((year < lastDate.year) || (quarter <= getQuarterForDate(lastDate) && year == lastDate.year)) {
                    dates.add(LocalDate.of(year, when(quarter) { 1 -> 1; 2 -> 4; 3 -> 7; else -> 10}, 1))
                    quarter++
                    if (quarter > 4) {
                        quarter = 1
                        year++
                    }
                }
            }
            AnalysisPrecision.Year -> {
                for (i in firstDate.year..lastDate.year) {
                    dates.add(LocalDate.of(i, 1, 1))
                }
                return dates
            }
        }
        return dates
    }


    /**
     * Gets the yearly quarter of the date passed from 1 (Q1) to 4 (Q4).
     *
     * @param date  Date whose yearly quarter to return.
     * @return      Yearly quarter of the date specified.
     */
    private fun getQuarterForDate(date: LocalDate): Int {
        return date.month.value - ((date.month.value - 1) % 3)
    }


    /**
     * Calculates the analysis results based on the passed transfers that are already grouped by
     * precision. The results will be stored in "transfersByType" and "cumulatedTransfersByType".
     *
     * @param transfersGroupedByPrecision   Transfers grouped by precision.
     * @param types                         List of all types.
     */
    private fun calculateAnalysisResultsForPrecision(transfersGroupedByPrecision: Map<LocalDate, List<Transfer>>, types: List<Type>) {
        transfersGroupedByPrecision.forEach { (month, transfers) ->
            val sumOfTransfersByType: Map<Type, Int> = calculateSumOfTransfersByType(transfers, types)

            sumOfTransfersByType.forEach { (type, sum) ->
                transfersByType[type]!!.add(AnalysisItem(sum, month))
                val cumulatedTransfersForType: MutableList<AnalysisItem> = cumulatedTransfersByType[type]!!
                if (cumulatedTransfersForType.isEmpty()) {
                    cumulatedTransfersForType.add(AnalysisItem(sum, month))
                }
                else {
                    cumulatedTransfersForType.add(AnalysisItem(sum + cumulatedTransfersForType[cumulatedTransfersForType.size - 1].value, month))
                }
            }
        }
    }


    /**
     * Calculates the sum of the transfers passed for each type.
     *
     * @param transfers List of transfers for which to calculate the sum by type.
     * @param types     List of types by which to group the sum of transfers.
     * @return          Sum of transfers grouped by type.
     */
    private fun calculateSumOfTransfersByType(transfers: List<Transfer>, types: List<Type>): Map<Type, Int> {
        val sumByType: MutableMap<Type, Int> = mutableMapOf()
        transfers.forEach { transfer ->
            val type: Type = getTypeForTransfer(transfer, types)
            if (sumByType.contains(type)) {
                sumByType[type] = sumByType[type]!! + transfer.transferValue.value
            }
            else {
                sumByType[type] = transfer.transferValue.value
            }
        }
        return sumByType
    }


    /**
     * Returns the type for the transfer specified.
     *
     * @param transfer  Transfer whose type to return
     * @param types     List of all types.
     * @return          Type of the transfer specified.
     */
    private fun getTypeForTransfer(transfer: Transfer, types: List<Type>): Type {
        //Transfers can only exist if types exist. Since this analysis can only run if transfers exist,
        //this concludes that types exist as well if this method is called. Therefore, it is not
        //required to check whether types is not empty before types.get(0) is called!
        var transferType: Type = types.get(0)
        types.forEach { type ->
            if (transfer.type == type.id) {
                transferType = type
                return@forEach
            }
        }
        return transferType
    }


    /**
     * Resets this analysis service instance so that a new analysis can run. This needs to be called
     * every time before to start an analysis.
     *
     * @param types     List of all types.
     * @param precision Analysis precision.
     */
    private fun resetServiceForAnalysis(types: List<Type>, precision: AnalysisPrecision) {
        analysisResultBuilder = AnalysisResult.Builder(precision)

        transfersByType.clear()
        cumulatedTransfersByType.clear()

        types.forEach { type ->
            transfersByType.put(type, mutableListOf())
            cumulatedTransfersByType.put(type, mutableListOf())
        }
    }

}
