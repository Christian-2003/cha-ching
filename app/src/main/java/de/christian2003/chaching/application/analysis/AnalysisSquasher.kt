package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.domain.analysis.AnalysisDiagramLine
import de.christian2003.chaching.domain.analysis.AnalysisItem
import de.christian2003.chaching.domain.analysis.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.AnalysisResult
import de.christian2003.chaching.domain.type.Type
import java.time.LocalDate


/**
 * Analysis squasher reduces the number of result items (by type).
 */
class AnalysisSquasher(

    /**
     * Analysis service whose result to squash.
     */
    private val analysisService: AnalysisService,

    /**
     * Number of types to include in the result at maximum. If there are more than n types, the result
     * will include n types as well as 1 type that includes the sum of all other types not directly
     * included in the result.
     */
    private val numberOfResults: Int = 2

): AnalysisService {

    /**
     * Intermediate result to squash.
     */
    lateinit var intermediateResult: AnalysisResult

    /**
     * Builder for the squashed analysis result.
     */
    lateinit var resultBuilder: AnalysisResult.Builder


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
        intermediateResult = analysisService.analyzeData(startDay, endDay, precision)
        resultBuilder = AnalysisResult.Builder(precision)
        intermediateResult.transfersByTypeDiagram

        //Determine the types tp include:
        val typesToInclude: List<Type> = calculateTypesToIncludeInSquashedResult()

        //Squash intermediate result:
        squashTotalTransfersByTypes(typesToInclude)
        squashAverageTransfersByTypes(typesToInclude)
        squashTransfersByTypeDiagram(typesToInclude)
        squashCumulatedTransfersByTypeDiagram(typesToInclude)

        //Return squashed result:
        return resultBuilder.build()
    }


    /**
     * Calculates the types to include in the squashed result.
     *
     * @return  List of types to include.
     */
    private fun calculateTypesToIncludeInSquashedResult(): List<Type> {
        val totalTransfersByTypeSorted: List<Pair<Type?, Int>> = intermediateResult.totalTransferByType
            .toList()
            .sortedByDescending { (_, totalTransferValue) -> totalTransferValue }

        val typesToInclude: MutableList<Type> = mutableListOf()

        var i = 0
        while (i < numberOfResults) {
            val type: Type? = totalTransfersByTypeSorted[i++].first
            if (type != null) {
                typesToInclude.add(type)
            }
            else {
                i--
            }
        }

        return typesToInclude
    }


    /**
     * Squashes the total transfers by type.
     *
     * @param typesToInclude    List of types to include.
     */
    private fun squashTotalTransfersByTypes(typesToInclude: List<Type>) {
        var totalTransfersSum = 0
        intermediateResult.totalTransferByType.forEach { (type, totalValue) ->
            if (typesToInclude.contains(type)) {
                resultBuilder.addTotalTransferByType(type, totalValue)
            }
            else {
                totalTransfersSum += totalValue
            }
        }
        resultBuilder.addTotalTransferByType(null, totalTransfersSum)
    }


    /**
     * Squashes the average transfers by type.
     *
     * @param typesToInclude    List of types to include.
     */
    private fun squashAverageTransfersByTypes(typesToInclude: List<Type>) {
        var totalAveragesSum = 0
        var numberOfAverageTransfers = 0
        intermediateResult.averageTransferByType.forEach { (type, totalValue) ->
            if (typesToInclude.contains(type)) {
                resultBuilder.addAverageTransferByType(type, totalValue)
            }
            else {
                totalAveragesSum += totalValue
                numberOfAverageTransfers++
            }
        }
        resultBuilder.addAverageTransferByType(null, totalAveragesSum / numberOfAverageTransfers)
    }


    /**
     * Squashes the transfers by type diagram.
     *
     * @param typesToInclude    List of types to include.
     */
    private fun squashTransfersByTypeDiagram(typesToInclude: List<Type>) {
        val squashedLineData: MutableList<AnalysisItem> = mutableListOf()
        intermediateResult.transfersByTypeDiagram.lines.forEach { line ->
            if (typesToInclude.contains(line.type)) {
                resultBuilder.addTransferByType(line)
            }
            else {
                if (squashedLineData.isEmpty()) {
                    squashedLineData.addAll(line.data)
                }
                else if (squashedLineData.size == line.data.size) {
                    for (i in 0..(squashedLineData.size - 1)) {
                        squashedLineData[i] = AnalysisItem(squashedLineData[i].value + line.data[i].value, squashedLineData[i].date)
                    }
                }
            }
        }
        if (squashedLineData.isNotEmpty()) {
            resultBuilder.addTransferByType(AnalysisDiagramLine(squashedLineData, null))
        }
    }


    /**
     * Squashes the cumulated transfers by type diagram.
     *
     * @param typesToInclude    List of types to include.
     */
    private fun squashCumulatedTransfersByTypeDiagram(typesToInclude: List<Type>) {
        val squashedLineData: MutableList<AnalysisItem> = mutableListOf()
        intermediateResult.cumulatedTransfersByTypeDiagram.lines.forEach { line ->
            if (typesToInclude.contains(line.type)) {
                resultBuilder.addCumulatedTransferByType(line)
            }
            else {
                if (squashedLineData.isEmpty()) {
                    squashedLineData.addAll(line.data)
                }
                else if (squashedLineData.size == line.data.size) {
                    for (i in 0..(squashedLineData.size - 1)) {
                        squashedLineData[i] = AnalysisItem(squashedLineData[i].value + line.data[i].value, squashedLineData[i].date)
                    }
                }
            }
        }
        if (squashedLineData.isNotEmpty()) {
            resultBuilder.addCumulatedTransferByType(AnalysisDiagramLine(squashedLineData, null))
        }
    }

}
