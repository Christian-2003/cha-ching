package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpanResult
import de.christian2003.chaching.plugin.presentation.view.analysis.DataTabOptions


/**
 * DTO contains all analysis results for the data tab.
 *
 * @param overview          Overview for the tab.
 * @param valuesDiagram     Values diagram.
 * @param cumulatedDiagram  Cumulated values diagram.
 */
data class DataTabDto(
    val overview: DataTabOverviewDto,
    val valuesDiagram: DiagramDto,
    val cumulatedDiagram: DiagramDto
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

            //Generate overview:
            val overview = DataTabOverviewDto(
                sum = currentTimeSpanResult.totalSum,
                avgPerTransaction = currentTimeSpanResult.totalAvgPerTransfer,
                avgPerNormalizedDate = currentTimeSpanResult.totalSum,
                sumDifferenceToPreviousTimeSpan = currentTimeSpanResult.totalSum - previousTimeSpanResult.totalSum,
                avgPerTransactionDifferenceToPreviousTimeSpan = currentTimeSpanResult.totalAvgPerTransfer - previousTimeSpanResult.totalAvgPerTransfer,
                avgPerNormalizedDateDifferenceToPreviousTimeSpan = currentTimeSpanResult.totalAvgPerNormalizedDate - previousTimeSpanResult.totalAvgPerNormalizedDate
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

            //Generate result:
            val result = DataTabDto(
                overview = overview,
                valuesDiagram = valuesDiagram,
                cumulatedDiagram = cumulatedDiagram
            )

            return result
        }

    }

}
