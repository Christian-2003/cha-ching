package de.christian2003.chaching.plugin.presentation.ui.composables.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * Displays a stacked column chart with positive and negative values.
 *
 * @param columns           Chart columns to display.
 * @param positiveColors    List of colors for positive data lines.
 * @param modifier          Modifier.
 * @param onFormatValue     Callback invoked to format a value.
 * @param negativeColors    List of colors for negative data lines.
 * @param columnHeight      Height for the columns.
 */
@Composable
fun ColumnChart(
    columns: List<ChartColumn>,
    positiveColors: List<Color>,
    modifier: Modifier = Modifier,
    onFormatValue: ((Double) -> String) = { it.toString() },
    negativeColors: List<Color> = positiveColors,
    columnHeight: Dp = 256.dp
) {
    val positiveMaxValue: Double = rememberSaveable {
        columns.maxOfOrNull { row ->
            var sum = 0.0
            row.values.forEach { value ->
                if (value > 0.0) {
                    sum += value
                }
            }
            return@maxOfOrNull sum
        } ?: 0.0
    }
    val negativeMaxValue: Double = rememberSaveable {
        columns.minOfOrNull { row ->
            var sum = 0.0
            row.values.forEach { value ->
                if (value < 0.0) {
                    sum += value
                }
            }
            return@minOfOrNull sum
        } ?: 0.0
    }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        ChartYAxis(
            positiveMaxValue = positiveMaxValue,
            negativeMaxValue = negativeMaxValue,
            columnHeight = columnHeight,
            onFormatValue = onFormatValue,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.padding_horizontal)
            )
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState(initial = Int.MAX_VALUE))
                .padding(end = dimensionResource(R.dimen.margin_horizontal))
        ) {
            columns.forEachIndexed { index, column ->
                ChartColumnWithLabel(
                    column = column,
                    positiveColors = positiveColors,
                    negativeColors = negativeColors,
                    columnHeight = columnHeight,
                    positiveMaxValue = positiveMaxValue,
                    negativeMaxValue = negativeMaxValue
                )
            }
        }
    }
}


/**
 * Displays the y-axis for the chart.
 *
 * @param positiveMaxValue  Max positive value.
 * @param negativeMaxValue  Max negative value.
 * @param columnHeight      Height for the chart columns.
 * @param onFormatValue     Callback invoked to format a value.
 * @param modifier          Modifier.
 * @param steps             Number of labels for the axis (+ label for "0.0").
 */
@Composable
private fun ChartYAxis(
    positiveMaxValue: Double,
    negativeMaxValue: Double,
    columnHeight: Dp,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier,
    steps: Int = 4
) {
    val hasPositiveValues: Boolean = positiveMaxValue > 0.0
    val hasNegativeValues: Boolean = negativeMaxValue < 0.0

    val totalValue: Double = positiveMaxValue + abs(negativeMaxValue)
    val positiveChartHeight: Dp = columnHeight * (positiveMaxValue / totalValue).toFloat()
    val negativeChartHeight: Dp = columnHeight * (abs(negativeMaxValue) / totalValue).toFloat()
    val baseline = positiveChartHeight

    val positiveSteps: Int = (steps.toDouble() * (positiveMaxValue / totalValue)).roundToInt()
    val negativeSteps: Int = (steps.toDouble() * (abs(negativeMaxValue) / totalValue)).roundToInt()

    val positiveStepSize: Double = if (hasPositiveValues) { positiveMaxValue / positiveSteps } else { 0.0 }
    val negativeStepSize: Double = if (hasNegativeValues) { negativeMaxValue / negativeSteps } else { 0.0 }

    Box(
        modifier = modifier
            .height(columnHeight)
    ) {
        if (positiveSteps > 0) {
            for (i in 0..positiveSteps) {
                val value: Double = positiveStepSize * i
                val yOffset = baseline - (positiveChartHeight / positiveSteps) * i

                Text(
                    text = onFormatValue(value),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.offset(
                        y = yOffset
                    )
                )
            }
        }

        if (negativeSteps > 0) {
            val start: Int = if (positiveSteps > 0) { 1 } else { 0 }
            for (i in start..negativeSteps) {
                val value: Double = negativeStepSize * i
                val yOffset = baseline + (negativeChartHeight / negativeSteps) * i

                Text(
                    text = onFormatValue(if (value == 0.0) { 0.0 } else { value }),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.offset(
                        y = yOffset
                    )
                )
            }
        }
    }
}


/**
 * Displays a chart column with labels. The chart column will display both positive and negative
 * values.
 *
 * @param column            Chart column to display.
 * @param positiveColors    List of colors for the positive data lines.
 * @param negativeColors    List of colors for the negative data lines.
 * @param columnHeight      Height for the chart column.
 * @param positiveMaxValue  Max positive value.
 * @param negativeMaxValue  Max negative value.
 * @param modifier          Modifier.
 */
@Composable
private fun ChartColumnWithLabel(
    column: ChartColumn,
    positiveColors: List<Color>,
    negativeColors: List<Color>,
    columnHeight: Dp,
    positiveMaxValue: Double,
    negativeMaxValue: Double,
    modifier: Modifier = Modifier
) {
    val totalValue: Double = positiveMaxValue + abs(negativeMaxValue)
    val positiveChartHeight: Dp = columnHeight * (positiveMaxValue / totalValue).toFloat()
    val negativeChartHeight: Dp = columnHeight * (abs(negativeMaxValue) / totalValue).toFloat()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(30.dp)
    ) {
        ChartColumn(
            column = column,
            colors = positiveColors,
            maxValue = positiveMaxValue,
            isNegative = false,
            modifier = modifier
                .height(positiveChartHeight)
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
        )
        HorizontalDivider()
        ChartColumn(
            column = column,
            colors = negativeColors,
            maxValue = negativeMaxValue,
            isNegative = true,
            modifier = modifier
                .height(negativeChartHeight)
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
        )
        Text(
            text = column.label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
        )
    }
}


/**
 * Displays a chart column. A chart column displays either positive OR negative values for a single
 * column of the chart.
 *
 * @param column        Chart column to display.
 * @param colors        Colors with which to display each data line in the column.
 * @param maxValue      Maximum value (either positive or negative).
 * @param modifier      Modifier.
 * @param isNegative    Whether the column is used to display negative values.
 */
@Composable
private fun ChartColumn(
    column: ChartColumn,
    colors: List<Color>,
    maxValue: Double,
    modifier: Modifier = Modifier,
    isNegative: Boolean = false
) {
    val absMaxValue: Double = abs(maxValue)
    Canvas(
        modifier = modifier.clip(MaterialTheme.shapes.extraLargeIncreased)
    ) {
        val cumulatedValues: MutableList<Double> = mutableListOf()
        var totalCumulatedValue = 0.0
        column.values.take(colors.size).forEach { value ->
            if (isNegative && value < 0.0) {
                totalCumulatedValue -= value
            }
            else if (!isNegative && value > 0.0) {
                totalCumulatedValue += value
            }
            cumulatedValues.add(totalCumulatedValue)
        }

        cumulatedValues.reversed().forEachIndexed { index, value ->
            if ((isNegative && column.values[column.values.size - 1 - index] < 0.0) || (!isNegative && column.values[column.values.size - 1 - index] > 0.0)) {
                val height: Float = size.height * (value / absMaxValue).toFloat()
                drawRoundRect(
                    color = colors[cumulatedValues.size - (index % colors.size) - 1],
                    size = Size(
                        width = size.width,
                        height = height
                    ),
                    topLeft = Offset(
                        x = 0f,
                        y = if (isNegative) { 0f } else { size.height - height }
                    ),
                    cornerRadius = CornerRadius(100f, 100f)
                )
            }
        }
    }
}
