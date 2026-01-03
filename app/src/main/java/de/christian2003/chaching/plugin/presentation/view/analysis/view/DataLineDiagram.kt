package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.ChartColorGenerator
import de.christian2003.chaching.plugin.presentation.model.TypeShapes
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ColumnChart
import de.christian2003.chaching.plugin.presentation.ui.theme.isDarkTheme
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DiagramDto
import java.util.UUID


/**
 * Displays a column chart diagram.
 *
 * @param options       Options for the data tab.
 * @param diagram       Diagram data to display.
 * @param onFormatValue Callback invoked to format a value.
 * @param onQueryType   Callback invoked to query a type by it's ID.
 * @param modifier      Modifier.
 * @param showLegend    Whether to show the diagram legend.
 */
@Composable
fun DataLineDiagram(
    options: DataTabOptions,
    diagram: DiagramDto,
    onFormatValue: (Double) -> String,
    onQueryType: suspend (UUID) -> Type?,
    modifier: Modifier = Modifier,
    showLegend: Boolean = true
) {
    if (diagram.chartColumns.isEmpty()) {
        return
    }

    val colorGenerator = ChartColorGenerator()
    val positiveColors: MutableList<Color> = colorGenerator.generateChartColors(
        seed = when (options) {
            DataTabOptions.Incomes -> MaterialTheme.colorScheme.primary
            DataTabOptions.Expenses -> MaterialTheme.colorScheme.tertiary
        },
        darkTheme = MaterialTheme.isDarkTheme()
    ).toMutableList()
    positiveColors.add(MaterialTheme.colorScheme.outlineVariant)
    val negativeColors: MutableList<Color> = colorGenerator.generateChartColors(
        seed = MaterialTheme.colorScheme.error,
        darkTheme = MaterialTheme.isDarkTheme()
    ).toMutableList()
    negativeColors.add(MaterialTheme.colorScheme.outlineVariant)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        //Diagram:
        ColumnChart(
            columns = diagram.chartColumns,
            positiveColors = positiveColors,
            negativeColors = negativeColors,
            onFormatValue = onFormatValue
        )

        //Legend:
        if (showLegend) {
            diagram.dataLineTypeIds.take(positiveColors.size).forEachIndexed { index, typeId ->
                val type: Type? by produceState(null) {
                    value = onQueryType(typeId)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                ) {
                    Shape(
                        shape = if (type != null && index < positiveColors.size - 1) {
                            TypeShapes.getShapeForTypeIcon(type!!.icon).shape
                        } else {
                            MaterialShapes.Circle
                        },
                        color = if (index < positiveColors.size) { positiveColors[index] } else { MaterialTheme.colorScheme.outlineVariant },
                        modifier = Modifier.size(dimensionResource(R.dimen.image_xxs))
                    )
                    Text(
                        text = if (type != null && index < positiveColors.size - 1) {
                            type!!.name
                        } else {
                            stringResource(R.string.main_analysis_otherTypeLabel)
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                    )
                }
            }
        }
    }
}
