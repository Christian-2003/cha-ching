package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.domain.analysis.AnalysisDiagram
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

class AnalysisService(

    /**
     * Repository through which to access transfers.
     */
    private val transferRepository: TransferRepository,

    /**
     * Repository through which to access types.
     */
    private val typeRepository: TypeRepository,

) {

    private val transfersByType: MutableMap<Type, MutableList<AnalysisItem>> = mutableMapOf()

    private val cumulatedTransfersByType: MutableMap<Type, MutableList<AnalysisItem>> = mutableMapOf()




    suspend fun analyzeData(startDay: LocalDate, endDay: LocalDate, precision: AnalysisPrecision): AnalysisResult {
        val transfers: List<Transfer> = transferRepository.getAllTransfersInDateRange(startDay, endDay).first()
        val types: List<Type> = typeRepository.getAllTypes().first()

        resetServiceForAnalysis(types)

        //Group transfers by analysis precision:
        val transfersGroupedByPrecision: Map<LocalDate, List<Transfer>> = transfers.groupBy { transfer ->
            when (precision) {
                AnalysisPrecision.QUARTER -> transfer.valueDate.withMonth(transfer.valueDate.month.value - ((transfer.valueDate.month.value - 1) % 3))
                AnalysisPrecision.YEAR -> transfer.valueDate.withMonth(1).withDayOfMonth(1)
                else -> transfer.valueDate.withDayOfMonth(1)
            }
        }

        calculateAnalysisResultsForPrecision(transfersGroupedByPrecision, types)

        return createAnalysisResult(precision)
    }




    private fun createAnalysisResult(precision: AnalysisPrecision): AnalysisResult {
        val transfersByTypeLines: MutableList<AnalysisDiagramLine> = mutableListOf()
        val cumulatedTransfersByTypeLines: MutableList<AnalysisDiagramLine> = mutableListOf()

        transfersByType.forEach { (type, analysisItems) ->
            transfersByTypeLines.add(AnalysisDiagramLine(analysisItems, type))
        }
        cumulatedTransfersByType.forEach { (type, analysisItems) ->
            cumulatedTransfersByTypeLines.add(AnalysisDiagramLine(analysisItems, type))
        }

        val transfersByTypeDiagram = AnalysisDiagram(transfersByTypeLines, precision)
        val cumulatedTransfersByTypeDiagram = AnalysisDiagram(cumulatedTransfersByTypeLines, precision)

        return AnalysisResult(
            transfersByTypeDiagram = transfersByTypeDiagram,
            cumulatedTransfersByTypeDiagram = cumulatedTransfersByTypeDiagram
        )
    }


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
                    cumulatedTransfersForType.add(AnalysisItem(sum + cumulatedTransfersForType[cumulatedTransfersByType.size - 2].value, month))
                }
            }
        }
    }


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


    private fun resetServiceForAnalysis(types: List<Type>) {
        transfersByType.clear()
        cumulatedTransfersByType.clear()

        types.forEach { type ->
            transfersByType.put(type, mutableListOf())
            cumulatedTransfersByType.put(type, mutableListOf())
        }
    }

}
