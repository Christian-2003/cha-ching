package de.christian2003.chaching.plugin.presentation.view.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import de.christian2003.chaching.domain.analysis.ResultSummary
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.AnalysisDiagram
import de.christian2003.chaching.domain.analysis.TypeResult
import de.christian2003.chaching.domain.analysis.TypeResultSummary
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTypeDiagram
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties
import java.util.UUID


@Composable
fun AnalysisDataTab(
    viewModel: AnalysisViewModel,
    bottomPadding: Dp,
    options: DataTabOptions
) {
    //Data for the analysis (Depending on options)
    val resultSummary: ResultSummary? = when (options) {
        DataTabOptions.Incomes -> viewModel.analysisResult?.totalIncomes
        DataTabOptions.Expenses -> viewModel.analysisResult?.totalExpenses
    }
    val typeResults: List<TypeResult> = when (options) {
        DataTabOptions.Incomes -> viewModel.typeResultsIncomes
        DataTabOptions.Expenses -> viewModel.typeResultsExpenses
    }
    val precision: AnalysisPrecision = viewModel.analysisResult?.precision ?: AnalysisPrecision.Month

    //Colors for the analysis:
    val labelColor: Color = when (options) {
        DataTabOptions.Incomes -> MaterialTheme.colorScheme.onSurface
        DataTabOptions.Expenses -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val valueColor: Color = when (options) {
        DataTabOptions.Incomes -> MaterialTheme.colorScheme.primary
        DataTabOptions.Expenses -> MaterialTheme.colorScheme.onSurface
    }

    if (resultSummary != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                DataOverviewCard(
                    options = options,
                    transferSumFormatted = viewModel.formatValue(resultSummary.transferSum),
                    transferAvgFormatted = viewModel.formatValue(resultSummary.transferAvg),
                    normalizedDateAvgFormatted = viewModel.formatValue(resultSummary.normalizedDateAvg),
                    labelColor = labelColor,
                    valueColor = valueColor,
                    precision = precision,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
                )
            }
            item {
                DataByTypeDiagram(
                    options = options,
                    typeResults = typeResults,
                    labelColor = labelColor,
                    valueColor = valueColor,
                    precision = precision,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
                )
            }
            item {
                Headline(
                    title = when (options) {
                        DataTabOptions.Incomes -> when (precision) {
                            AnalysisPrecision.Month -> stringResource(R.string.analysis_incomes_monthDiagramValues)
                            AnalysisPrecision.Quarter -> stringResource(R.string.analysis_incomes_quarterDiagramValues)
                            AnalysisPrecision.Year -> stringResource(R.string.analysis_incomes_yearDiagramValues)
                        }
                        DataTabOptions.Expenses -> when (precision) {
                            AnalysisPrecision.Month -> stringResource(R.string.analysis_expenses_monthDiagramValues)
                            AnalysisPrecision.Quarter -> stringResource(R.string.analysis_expenses_quarterDiagramValues)
                            AnalysisPrecision.Year -> stringResource(R.string.analysis_expenses_yearDiagramValues)
                        }
                    }
                )
            }
            item {
                DataLineDiagram(
                    diagram = when (options) {
                        DataTabOptions.Incomes -> viewModel.valuesDiagramIncomes
                        DataTabOptions.Expenses -> viewModel.valuesDiagramExpenses
                    },
                    labels = viewModel.diagramLabels
                )
            }
            item {
                Headline(
                    title = when (options) {
                        DataTabOptions.Incomes -> when (precision) {
                            AnalysisPrecision.Month -> stringResource(R.string.analysis_incomes_monthDiagramCumulated)
                            AnalysisPrecision.Quarter -> stringResource(R.string.analysis_incomes_quarterDiagramCumulated)
                            AnalysisPrecision.Year -> stringResource(R.string.analysis_incomes_yearDiagramCumulated)
                        }
                        DataTabOptions.Expenses -> when (precision) {
                            AnalysisPrecision.Month -> stringResource(R.string.analysis_expenses_monthDiagramCumulated)
                            AnalysisPrecision.Quarter -> stringResource(R.string.analysis_expenses_quarterDiagramCumulated)
                            AnalysisPrecision.Year -> stringResource(R.string.analysis_expenses_yearDiagramCumulated)
                        }
                    }
                )
            }
            item {
                DataLineDiagram(
                    diagram = when (options) {
                        DataTabOptions.Incomes -> viewModel.cumulatedDiagramIncomes
                        DataTabOptions.Expenses -> viewModel.cumulatedDiagramExpenses
                    },
                    labels = viewModel.diagramLabels
                )
            }
            item {
                Headline(
                    title = stringResource(R.string.analysis_data_typesListTitle)
                )
            }
            itemsIndexed(items = typeResults) { index, typeResult ->
                TypeListItem(
                    options = options,
                    index = index,
                    typeResult = typeResult,
                    isFirst = index == 0,
                    isLast = index == typeResults.size - 1,
                    labelColor = labelColor,
                    valueColor = valueColor,
                    onQueryType = { typeId ->
                        viewModel.queryType(typeId)
                    },
                    onFormatValue = { value ->
                        viewModel.formatValue(value)
                    }
                )
            }
            item {
                Box(
                    modifier = Modifier.height(bottomPadding)
                )
            }
        }
    }
}


@Composable
private fun DataOverviewCard(
    options: DataTabOptions,
    transferSumFormatted: String,
    transferAvgFormatted: String,
    normalizedDateAvgFormatted: String,
    labelColor: Color,
    valueColor: Color,
    precision: AnalysisPrecision,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = when (options) {
                        DataTabOptions.Incomes -> stringResource(R.string.analysis_incomes_total)
                        DataTabOptions.Expenses -> stringResource(R.string.analysis_expenses_total)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor
                )
                Text(
                    text = transferSumFormatted,
                    style = MaterialTheme.typography.headlineMedium,
                    color = valueColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_wallet),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_m))
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensionResource(R.dimen.padding_vertical))
                .height(IntrinsicSize.Min)
        ) {
            DataOverviewCardAverageItem(
                label = stringResource(R.string.analysis_data_transferAvg),
                valueFormatted = transferAvgFormatted,
                labelColor = labelColor,
                valueColor = valueColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Box(modifier = Modifier.width(dimensionResource(R.dimen.padding_horizontal)))
            DataOverviewCardAverageItem(
                label = when (precision) {
                    AnalysisPrecision.Month -> stringResource(R.string.analysis_data_monthAvg)
                    AnalysisPrecision.Quarter -> stringResource(R.string.analysis_data_quarterAvg)
                    AnalysisPrecision.Year -> stringResource(R.string.analysis_data_yearAvg)
                },
                valueFormatted = normalizedDateAvgFormatted,
                labelColor = labelColor,
                valueColor = valueColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}


@Composable
private fun DataOverviewCardAverageItem(
    label: String,
    valueFormatted: String,
    labelColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraExtraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = valueFormatted,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



@Composable
private fun DataByTypeDiagram(
    options: DataTabOptions,
    typeResults: List<TypeResult>,
    labelColor: Color,
    valueColor: Color,
    precision: AnalysisPrecision,
    modifier: Modifier = Modifier
) {
    val pieSegments: MutableList<Pie> = mutableListOf()
    typeResults.take(4).forEachIndexed { index, typeResult ->
        val pieSegment = Pie(
            data = when (options) {
                DataTabOptions.Incomes -> typeResult.incomes.transferSum
                DataTabOptions.Expenses -> typeResult.expenses.transferSum
            },
            color = when (index) {
                0 -> MaterialTheme.colorScheme.primary
                1 -> MaterialTheme.colorScheme.secondary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        pieSegments.add(pieSegment)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        PieChart(
            data = pieSegments,
            modifier = modifier
                .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                .size(192.dp)
        )
    }
}


@Composable
private fun DataLineDiagram(
    diagram: DataTypeDiagram,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val diagramLines: MutableList<Line> = mutableListOf()
    diagram.lines.forEachIndexed { index, line ->
        val color: Brush = SolidColor(when (index) {
            0 -> MaterialTheme.colorScheme.primary
            1 -> MaterialTheme.colorScheme.secondary
            2 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.outline
        })

        val diagramLine = Line(
            label = "",
            values = line.values,
            color = color,
            curvedEdges = !diagram.isCumulated,
            dotProperties = DotProperties(
                enabled = true,
                color = SolidColor(MaterialTheme.colorScheme.surface),
                strokeWidth = 2.dp,
                radius = 2.dp,
                strokeColor = color
            )
        )
        diagramLines.add(diagramLine)
    }

    LineChart(
        data = diagramLines,
        gridProperties = GridProperties(
            xAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                color = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                thickness = 1.dp
            ),
            yAxisProperties = GridProperties.AxisProperties(
                enabled = true,
                color = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                thickness = 1.dp,
                lineCount = diagram.lines[0].values.size
            ),
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            labels = labels,
            rotation = LabelProperties.Rotation(mode = LabelProperties.Rotation.Mode.Force, degree = -45f)
        ),
        /*labelHelperProperties = LabelHelperProperties(
            enabled = false
        ),
        popupProperties = PopupProperties(
            enabled = true,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            contentBuilder = indicatorBuilder
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            contentBuilder = indicatorBuilder
        ),*/
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
            .padding(bottom = 48.dp)
            .height(240.dp)
    )
}


@Composable
private fun TypeListItem(
    options: DataTabOptions,
    index: Int,
    typeResult: TypeResult,
    isFirst: Boolean,
    isLast: Boolean,
    labelColor: Color,
    valueColor: Color,
    onQueryType: suspend (UUID) -> Type?,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val type: Type? by produceState(null) {
        value = onQueryType(typeResult.typeId)
    }
    val typeResultSummary: TypeResultSummary = when (options) {
        DataTabOptions.Incomes -> typeResult.incomes
        DataTabOptions.Expenses -> typeResult.expenses
    }

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {

                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            if (type != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(dimensionResource(R.dimen.image_m))
                ) {
                    Shape(
                        shape = TypeListShapes.entries[index % TypeListShapes.entries.size].shape,
                        color = when (index) {
                            0 -> MaterialTheme.colorScheme.primaryContainer
                            1 -> MaterialTheme.colorScheme.secondaryContainer
                            2 -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    )
                    Icon(
                        painter = painterResource(type!!.icon.drawableResourceId),
                        contentDescription = "",
                        tint = when (index) {
                            0 -> MaterialTheme.colorScheme.onPrimaryContainer
                            1 -> MaterialTheme.colorScheme.onSecondaryContainer
                            2 -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> labelColor
                        },
                        modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                        .weight(1f)
                ) {
                    Text(
                        text = type!!.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = pluralStringResource(R.plurals.analysis_data_typeListTransferCount, typeResultSummary.transferCount, typeResultSummary.transferCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            else {
                Box(modifier = Modifier.weight(1f))
            }
            Text(
                text = onFormatValue(typeResultSummary.transferSum),
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor
            )
        }
    }
}


private enum class TypeListShapes(
    val shape: RoundedPolygon
) {
    Sunny(MaterialShapes.Sunny),
    Cookie(MaterialShapes.Cookie6Sided),
    Leaf(MaterialShapes.Clover8Leaf),
    Burst(MaterialShapes.SoftBurst)
}
