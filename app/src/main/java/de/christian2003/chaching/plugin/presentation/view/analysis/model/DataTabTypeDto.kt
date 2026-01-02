package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.large.LargeTypeResult
import java.util.UUID


/**
 * DTO contains the result data for a single type.
 *
 * @param typeId                ID of the type.
 * @param overview              Overview for the type.
 * @param valuesDiagram         Diagram containing all values of the type.
 * @param differencesDiagram    Diagram showing the difference to the previous time span.
 */
data class DataTabTypeDto(
    val typeId: UUID,
    val overview: DataTabOverviewDto,
    val valuesDiagram: DiagramDto,
    val differencesDiagram: DiagramDto
) {

    companion object {

        fun getInstance(
            currentTypeResult: LargeTypeResult,
            previousTypeResult: LargeTypeResult?,
            diagramLabels: List<String>
        ): DataTabTypeDto {
            //Overview:
            val overview = DataTabOverviewDto(
                sum = currentTypeResult.valueResult.sum,
                avgPerTransfer = currentTypeResult.valueResult.avgPerTransfer,
                avgPerNormalizedDate = currentTypeResult.valueResult.avgPerNormalizedDate,
                sumDifferenceToPreviousTimeSpan = if (previousTypeResult != null) {
                    currentTypeResult.valueResult.sum - previousTypeResult.valueResult.sum
                } else {
                    0.0
                },
                avgPerTransferDifferenceToPreviousTimeSpan = if (previousTypeResult != null) {
                    currentTypeResult.valueResult.avgPerTransfer - previousTypeResult.valueResult.avgPerTransfer
                } else {
                    0.0
                },
                avgPerNormalizedDateDifferenceToPreviousTimeSpan = if (previousTypeResult != null) {
                    currentTypeResult.valueResult.avgPerNormalizedDate - previousTypeResult.valueResult.avgPerNormalizedDate
                } else {
                    0.0
                },
                transferCount = currentTypeResult.transferCount
            )

            //Values diagram:
            val valuesDiagram: DiagramDto = DiagramDto.getInstance(
                typeResult = currentTypeResult,
                labels = diagramLabels
            )

            //Differences diagram:
            val differencesDiagram: DiagramDto = DiagramDto.getInstance(
                currentTypeResult = currentTypeResult,
                previousTypeResult = previousTypeResult,
                labels = diagramLabels
            )

            //Result:
            val result = DataTabTypeDto(
                typeId = currentTypeResult.typeId,
                overview = overview,
                valuesDiagram =  valuesDiagram,
                differencesDiagram = differencesDiagram
            )

            return result
        }

    }

}
