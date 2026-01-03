package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpanResult
import de.christian2003.chaching.domain.analysis.large.LargeTypeResult


/**
 * DTO contains all analysis results for the data tab.
 *
 * @param overview          Overview for the tab.
 * @param valuesDiagram     Values diagram.
 * @param cumulatedDiagram  Cumulated values diagram.
 * @param types             Individual result for each type.
 */
data class DataTabDto(
    val overview: DataTabOverviewDto,
    val valuesDiagram: DiagramDto,
    val cumulatedDiagram: DiagramDto,
    val types: List<DataTabTypeDto>
) {

    companion object {

        /**
         * Generates an instance of DataTabDto.
         *
         * @param options           Options for the data tab.
         * @param analysisResult    Analysis result used for generation.
         * @param diagramLabels     List of labels for the diagrams.
         */
        fun getInstance(
            options: DataTabOptions,
            analysisResult: LargeAnalysisResult,
            diagramLabels: List<String>
        ): DataTabDto {
            val currentTimeSpanResult: LargeTimeSpanResult = when(options) {
                DataTabOptions.Incomes -> analysisResult.currentSpan.incomes
                DataTabOptions.Expenses -> analysisResult.currentSpan.expenses
            }
            val previousTimeSpanResult: LargeTimeSpanResult = when(options) {
                DataTabOptions.Incomes -> analysisResult.previousSpan.incomes
                DataTabOptions.Expenses -> analysisResult.previousSpan.expenses
            }

            //Calculate total transfer count:
            var totalTransferCount = 0
            currentTimeSpanResult.typeResults.forEach { typeResult ->
                totalTransferCount += typeResult.transferCount
            }

            //Generate overview:
            val overview = DataTabOverviewDto(
                sum = currentTimeSpanResult.totalSum,
                avgPerTransfer = currentTimeSpanResult.totalAvgPerTransfer,
                avgPerNormalizedDate = currentTimeSpanResult.totalAvgPerNormalizedDate,
                sumDifferenceToPreviousTimeSpan = currentTimeSpanResult.totalSum - previousTimeSpanResult.totalSum,
                avgPerTransferDifferenceToPreviousTimeSpan = currentTimeSpanResult.totalAvgPerTransfer - previousTimeSpanResult.totalAvgPerTransfer,
                avgPerNormalizedDateDifferenceToPreviousTimeSpan = currentTimeSpanResult.totalAvgPerNormalizedDate - previousTimeSpanResult.totalAvgPerNormalizedDate,
                transferCount = totalTransferCount
            )

            //Generate values diagram:
            val valuesDiagram: DiagramDto = DiagramDto.getInstance(
                timeSpanResult = currentTimeSpanResult,
                diagramType = DiagramDto.DiagramType.Values,
                labels = diagramLabels
            )

            //Generate cumulated diagram:
            val cumulatedDiagram: DiagramDto = DiagramDto.getInstance(
                timeSpanResult = currentTimeSpanResult,
                diagramType = DiagramDto.DiagramType.Cumulated,
                labels = diagramLabels
            )

            //Generate results for types:
            val types: MutableList<DataTabTypeDto> = mutableListOf()
            currentTimeSpanResult.typeResults.filter { typeResult ->
                typeResult.valueResult.sum > 0.0
            }.sortedByDescending { typeResult ->
                typeResult.valueResult.sum
            }.forEach { typeResult ->
                val previousTypeResult: LargeTypeResult? = previousTimeSpanResult.typeResults.find { it.typeId == typeResult.typeId }
                val type: DataTabTypeDto = DataTabTypeDto.getInstance(
                    currentTypeResult = typeResult,
                    previousTypeResult = previousTypeResult,
                    diagramLabels = diagramLabels
                )
                types.add(type)
            }

            //Generate result:
            val result = DataTabDto(
                overview = overview,
                valuesDiagram = valuesDiagram,
                cumulatedDiagram = cumulatedDiagram,
                types = types
            )

            return result
        }

    }

}
