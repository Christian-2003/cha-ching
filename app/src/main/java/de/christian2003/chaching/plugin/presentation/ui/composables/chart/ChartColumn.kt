package de.christian2003.chaching.plugin.presentation.ui.composables.chart


/**
 * Models a single column for the column chart.
 *
 * @param values    Values for the stacked column.
 * @param label     Label for the stacked column.
 */
data class ChartColumn(
    val values: List<Double>,
    val label: String
)
