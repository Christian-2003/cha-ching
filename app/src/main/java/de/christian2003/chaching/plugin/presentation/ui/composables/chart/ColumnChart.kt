package de.christian2003.chaching.plugin.presentation.ui.composables.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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


@Composable
fun ColumnChart(
    dataLines: List<List<Double>>,
    labels: List<String>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onFormatValue: ((Double) -> String) = { it.toString() },
    backgroundColor: Color = Color.Transparent,
    columnHeight: Dp = 256.dp
) {
    val maxColumns: Int = dataLines.maxOfOrNull { it.size } ?: 0
    val columns: List<List<Double>> = rememberSaveable{
        (0 until maxColumns).map { columnIndex ->
            dataLines.mapNotNull { row ->
                row.getOrNull(columnIndex)
            }
        }
    }

    val maxValue: Double = rememberSaveable {
        columns.maxOfOrNull { row ->
            var sum = 0.0
            row.forEach { value -> sum += value }
            return@maxOfOrNull sum
        } ?: 0.0
    }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        ChartYAxis(
            maxValue = maxValue,
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
            columns.forEachIndexed { index, dataLineValues ->
                ChartColumnWithLabel(
                    values = dataLineValues,
                    colors = colors,
                    backgroundColor = backgroundColor,
                    label = labels.getOrNull(index) ?: "",
                    columnHeight = columnHeight,
                    maxValue = maxValue
                )
            }
        }
    }
}


@Composable
private fun ChartYAxis(
    maxValue: Double,
    columnHeight: Dp,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier,
    steps: Int = 5
) {
    val labelValues: MutableList<Double> = mutableListOf()
    val stepSize: Double = maxValue / steps
    for (i in 0..(steps - 1)) {
        labelValues.add(stepSize * i)
    }

    Box(
        modifier = modifier
            .height(columnHeight)
    ) {
        labelValues.reversed().forEachIndexed { index, value ->
            Text(
                text = onFormatValue(value),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.offset(
                    y = (columnHeight / (steps - 1)) * index
                )
            )
        }
    }
}


@Composable
private fun ChartColumnWithLabel(
    values: List<Double>,
    colors: List<Color>,
    backgroundColor: Color,
    label: String,
    columnHeight: Dp,
    maxValue: Double,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 1.dp)
            .width(28.dp)
    ) {
        ChartColumn(
            values = values,
            colors = colors,
            backgroundColor = backgroundColor,
            maxValue = maxValue,
            modifier = modifier
                .height(columnHeight)
                .fillMaxWidth()
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
private fun ChartColumn(
    values: List<Double>,
    colors: List<Color>,
    backgroundColor: Color,
    maxValue: Double,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLargeIncreased)
            .background(backgroundColor)
    ) {
        val cumulatedValues: MutableList<Double> = mutableListOf()
        var totalCumulatedValue = 0.0
        values.take(colors.size).forEach { value ->
            totalCumulatedValue += value
            cumulatedValues.add(totalCumulatedValue)
        }

        cumulatedValues.reversed().forEachIndexed { index, value ->
            val height: Float = size.height * (value / maxValue).toFloat()
            drawRoundRect(
                color = colors[cumulatedValues.size - (index % colors.size) - 1],
                size = Size(
                    width = size.width,
                    height = height
                ),
                topLeft = Offset(
                    x = 0f,
                    y = size.height - height
                ),
                cornerRadius = CornerRadius(100f, 100f)
            )
        }
    }
}
