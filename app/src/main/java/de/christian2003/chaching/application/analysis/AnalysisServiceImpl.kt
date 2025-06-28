package de.christian2003.chaching.application.analysis

import android.util.Log
import de.christian2003.chaching.domain.analysis.AnalysisDiagramLine
import de.christian2003.chaching.domain.analysis.AnalysisItem
import de.christian2003.chaching.domain.analysis.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.AnalysisResult
import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import kotlin.collections.MutableMap


/**
 * Analysis result generates the result for all transfers within the database in a specified period
 * of time.
 */
class AnalysisServiceImpl(

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
        val transfersGroupedByPrecision: Map<LocalDate, List<Transfer>> = transfers.groupBy { transfer ->
            when (precision) {
                AnalysisPrecision.QUARTER -> transfer.valueDate.withMonth(transfer.valueDate.month.value - ((transfer.valueDate.month.value - 1) % 3))
                AnalysisPrecision.YEAR -> transfer.valueDate.withMonth(1).withDayOfMonth(1)
                else -> transfer.valueDate.withDayOfMonth(1)
            }
        }

        calculateAnalysisResultsForPrecision(transfersGroupedByPrecision, types)
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
                Log.d("Analysis", "Added analysis item")
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
                sumByType[type] = sumByType[type]!! + transfer.value
            }
            else {
                sumByType[type] = transfer.value
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
