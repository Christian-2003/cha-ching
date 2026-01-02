package de.christian2003.chaching.plugin.presentation.view.analysis.model

import android.util.Log
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpanResult
import de.christian2003.chaching.domain.analysis.large.LargeTypeDiagram
import de.christian2003.chaching.domain.analysis.large.LargeTypeResult
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ChartColumn
import java.util.UUID


/**
 * DTO for the column charts displayed on the analysis screen.
 *
 * @param chartColumns      List of columns for the chart.
 * @param dataLineTypeIds   Type IDs of the data lines.
 */
data class DiagramDto(
    val chartColumns: List<ChartColumn>,
    val dataLineTypeIds: List<UUID>
) {

    /**
     * Enum fields inform about the type of diagram to create.
     */
    enum class DiagramType {
        Values,
        Cumulated
    }

    companion object {

        /**
         * Generates a diagram DTO from the specified time span result.
         *
         * @param timeSpanResult    Time span result from which to generate the diagram DTO.
         * @param diagramType       Type of diagram to return.
         * @param labels            List of labels.
         * @param limit             Max number of distinct values per column. If there are more values
         *                          available, an additional column is added that summarizes all remaining
         *                          values. In this case, the result contains limit + 1 values
         */
        fun getInstance(
            timeSpanResult: LargeTimeSpanResult,
            diagramType: DiagramType,
            labels: List<String>,
            limit: Int = 3
        ): DiagramDto {
            val filteredTypeResults: List<LargeTypeResult> = timeSpanResult.typeResults.sortedByDescending {
                it.valueResult.sum
            }.filter {
                it.valueResult.sum > 0.0
            }

            //Determine number of columns:
            val numberOfColumns: Int = when (diagramType) {
                DiagramType.Values -> filteredTypeResults.firstOrNull()?.valuesDiagram?.values?.size ?: 0
                DiagramType.Cumulated -> filteredTypeResults.firstOrNull()?.cumulatedDiagram?.values?.size ?: 0
            }

            //Generate column values:
            val columns: List<MutableList<Double>> = (0 until numberOfColumns).map { mutableListOf() }
            filteredTypeResults.forEach { typeResult ->
                val diagram: LargeTypeDiagram = when (diagramType) {
                    DiagramType.Values -> typeResult.valuesDiagram
                    DiagramType.Cumulated -> typeResult.cumulatedDiagram
                }

                diagram.values.forEachIndexed { index, value ->
                    if (columns[index].size > limit) {
                        columns[index][limit - 1] += value
                    }
                    else {
                        columns[index].add(value)
                    }
                }
            }

            //Convert values to chart columns:
            val chartColumns: MutableList<ChartColumn> = mutableListOf()
            columns.forEachIndexed { index, values ->
                val chartColumn = ChartColumn(
                    values = values,
                    label = labels.getOrNull(index) ?: ""
                )
                chartColumns.add(chartColumn)
            }

            //Generate data line type IDs:
            val dataLineTypeIds: MutableList<UUID> = mutableListOf()
            filteredTypeResults.forEach { typeResult ->
                dataLineTypeIds.add(typeResult.typeId)
            }

            //Generate result:
            val result = DiagramDto(
                chartColumns = chartColumns,
                dataLineTypeIds = dataLineTypeIds
            )

            return result
        }


        fun getInstance(
            typeResult: LargeTypeResult,
            labels: List<String>
        ): DiagramDto {
            val chartColumns: MutableList<ChartColumn> = mutableListOf()
            typeResult.valuesDiagram.values.forEachIndexed { index, value ->
                val chartColumn = ChartColumn(
                    values = listOf(value),
                    label = labels.getOrNull(index) ?: ""
                )
                chartColumns.add(chartColumn)
            }

            val result = DiagramDto(
                chartColumns = chartColumns,
                dataLineTypeIds = listOf(typeResult.typeId)
            )

            return result
        }


        fun getInstance(
            currentTypeResult: LargeTypeResult,
            previousTypeResult: LargeTypeResult?,
            labels: List<String>
        ): DiagramDto {
            val chartColumns: MutableList<ChartColumn> = mutableListOf()
            currentTypeResult.valuesDiagram.values.forEachIndexed { index, value ->
                val previousValue: Double = previousTypeResult?.valuesDiagram?.values?.getOrNull(index) ?: 0.0
                val chartColumn = ChartColumn(
                    values = listOf(value - previousValue),
                    label = labels.getOrNull(index) ?: ""
                )
                chartColumns.add(chartColumn)
            }

            val result = DiagramDto(
                chartColumns = chartColumns,
                dataLineTypeIds = listOf(currentTypeResult.typeId)
            )

            return result
        }

    }

}
