package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.large.LargeTimeSpanResult
import de.christian2003.chaching.domain.analysis.large.LargeTypeDiagram
import de.christian2003.chaching.domain.analysis.large.LargeTypeResult
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ChartColumn


/**
 * DTO for the column charts displayed on the analysis screen.
 *
 * @param chartColumns  List of columns for the chart.
 */
data class DiagramDto(
    val chartColumns: List<ChartColumn>
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
            val sortedTypeResults: List<LargeTypeResult> = timeSpanResult.typeResults.sortedByDescending { it.valueResult.sum }

            //Determine number of columns:
            val numberOfColumns: Int = when (diagramType) {
                DiagramType.Values -> sortedTypeResults.firstOrNull()?.valuesDiagram?.values?.size ?: 0
                DiagramType.Cumulated -> sortedTypeResults.firstOrNull()?.cumulatedDiagram?.values?.size ?: 0
            }

            //Generate column values:
            val columns: List<MutableList<Double>> = (0 until numberOfColumns).map { mutableListOf() }
            sortedTypeResults.forEach { typeResult ->
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

            //Generate result:
            val result = DiagramDto(
                chartColumns = chartColumns
            )

            return result
        }

    }

}
