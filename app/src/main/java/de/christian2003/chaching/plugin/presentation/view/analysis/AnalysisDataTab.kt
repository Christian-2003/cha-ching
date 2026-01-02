package de.christian2003.chaching.plugin.presentation.view.analysis

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
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.ChartColorGenerator
import de.christian2003.chaching.plugin.presentation.model.TypeShapes
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ColumnChart
import de.christian2003.chaching.plugin.presentation.ui.theme.isDarkTheme
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabDto
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOverviewDto
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabTypeDto
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DiagramDto
import java.time.LocalDate
import java.util.UUID
import kotlin.math.abs


@Composable
fun AnalysisDataTab(
    viewModel: AnalysisViewModel,
    bottomPadding: Dp,
    options: DataTabOptions
) {
    val analysisResult: LargeAnalysisResult? = viewModel.analysisResult
    if (analysisResult == null) {
        return
    }

    val data: DataTabDto = when (options) {
        DataTabOptions.Incomes -> viewModel.incomesTabData
        DataTabOptions.Expenses -> viewModel.expensesTabData
    }
    val precision: AnalysisPrecision = analysisResult.metadata.precision

    //Colors for the analysis:
    val valueColor: Color = when (options) {
        DataTabOptions.Incomes -> MaterialTheme.colorScheme.primary
        DataTabOptions.Expenses -> MaterialTheme.colorScheme.tertiary
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        item {
            OverviewCard(
                options = options,
                overview = data.overview,
                valueColor = valueColor,
                precision = precision,
                currentStart = analysisResult.currentSpan.start,
                currentEnd = analysisResult.currentSpan.end,
                previousStart = analysisResult.previousSpan.start,
                previousEnd = analysisResult.previousSpan.end,
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onFormatDate = {
                    viewModel.formatDate(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        item {
            DataLineDiagram(
                options = options,
                diagram = data.valuesDiagram,
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onQueryType = {
                    viewModel.queryType(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        item {
            DataLineDiagram(
                options = options,
                diagram = data.cumulatedDiagram,
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onQueryType = {
                    viewModel.queryType(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
            )
        }
        item {
            Headline(
                title = stringResource(R.string.analysis_data_typesListTitle),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    ))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
        itemsIndexed(data.types) { index, type ->
            TypeResultItem(
                typeResult = type,
                valueColor = valueColor,
                isFirst = index == 0,
                isLast = index == data.types.size - 1,
                onClick = {
                    viewModel.displayType(type)
                },
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onQueryType = {
                    viewModel.queryType(it)
                }
            )
        }
        item {
            Box(
                modifier = Modifier.height(bottomPadding)
            )
        }
    }

    val displayedTypeInfo: DataTabTypeDto? = viewModel.displayedTypeInfo
    if (displayedTypeInfo != null) {
        val transfers: List<Transfer> by viewModel.transfersOfDisplayedType.collectAsState(emptyList())
        AnalysisTypeSheet(
            options = options,
            valueColor = valueColor,
            precision = precision,
            typeData = displayedTypeInfo,
            currentStart = analysisResult.currentSpan.start,
            currentEnd = analysisResult.currentSpan.end,
            previousStart = analysisResult.previousSpan.start,
            previousEnd = analysisResult.previousSpan.end,
            transfers = transfers,
            onDismiss = {
                viewModel.dismissDisplayedType()
            },
            onFormatValue = {
                viewModel.formatValue(it)
            },
            onFormatTransferValue = {
                viewModel.formatValue(it)
            },
            onFormatDate = {
                viewModel.formatDate(it)
            },
            onQueryType = {
                viewModel.queryType(it)
            }
        )
    }
}


@Composable
fun OverviewCard(
    options: DataTabOptions,
    overview: DataTabOverviewDto,
    valueColor: Color,
    precision: AnalysisPrecision,
    currentStart: LocalDate,
    currentEnd: LocalDate,
    previousStart: LocalDate,
    previousEnd: LocalDate,
    onFormatValue: (Double) -> String,
    onFormatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
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
                    text = onFormatValue(overview.sum),
                    style = MaterialTheme.typography.headlineMedium,
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (options) {
                        DataTabOptions.Incomes -> stringResource(R.string.analysis_incomes_total)
                        DataTabOptions.Expenses -> stringResource(R.string.analysis_expenses_total)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TrendIcon(
                options = options,
                currentStart = currentStart,
                currentEnd = currentEnd,
                currentValue = overview.sum,
                previousStart = previousStart,
                previousEnd = previousEnd,
                previousValue = overview.sum - overview.sumDifferenceToPreviousTimeSpan,
                size = dimensionResource(R.dimen.image_m),
                onFormatValue = onFormatValue,
                onFormatDate = onFormatDate,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensionResource(R.dimen.padding_vertical))
                .height(IntrinsicSize.Min)
        ) {
            OverviewCardAvgItem(
                options = options,
                label = stringResource(R.string.analysis_data_transferAvg),
                currentStart = currentStart,
                currentEnd = currentEnd,
                currentAvg = overview.avgPerTransfer,
                previousStart = previousStart,
                previousEnd = previousEnd,
                previousAvg = overview.avgPerTransfer - overview.avgPerTransferDifferenceToPreviousTimeSpan,
                valueColor = valueColor,
                onFormatValue = onFormatValue,
                onFormatDate = onFormatDate,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Box(modifier = Modifier.width(dimensionResource(R.dimen.padding_horizontal)))
            OverviewCardAvgItem(
                options = options,
                label = when (precision) {
                    AnalysisPrecision.Month -> stringResource(R.string.analysis_data_monthAvg)
                    AnalysisPrecision.Quarter -> stringResource(R.string.analysis_data_quarterAvg)
                    AnalysisPrecision.Year -> stringResource(R.string.analysis_data_yearAvg)
                },
                currentStart = currentStart,
                currentEnd = currentEnd,
                currentAvg = overview.avgPerNormalizedDate,
                previousStart = previousStart,
                previousEnd = previousEnd,
                previousAvg = overview.avgPerNormalizedDate - overview.avgPerNormalizedDateDifferenceToPreviousTimeSpan,
                valueColor = valueColor,
                onFormatValue = onFormatValue,
                onFormatDate = onFormatDate,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}


@Composable
private fun OverviewCardAvgItem(
    options: DataTabOptions,
    label: String,
    valueColor: Color,
    currentStart: LocalDate,
    currentEnd: LocalDate,
    currentAvg: Double,
    previousStart: LocalDate,
    previousEnd: LocalDate,
    previousAvg: Double,
    onFormatValue: (Double) -> String,
    onFormatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraExtraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = onFormatValue(currentAvg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = valueColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                TrendIcon(
                    options = options,
                    currentStart = currentStart,
                    currentEnd = currentEnd,
                    currentValue = currentAvg,
                    previousStart = previousStart,
                    previousEnd = previousEnd,
                    previousValue = previousAvg,
                    size = dimensionResource(R.dimen.image_s),
                    onFormatValue = onFormatValue,
                    onFormatDate = onFormatDate,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                )
            }
        }
    }
}


@Composable
private fun TrendIcon(
    options: DataTabOptions,
    currentStart: LocalDate,
    currentEnd: LocalDate,
    currentValue: Double,
    previousStart: LocalDate,
    previousEnd: LocalDate,
    previousValue: Double,
    size: Dp,
    onFormatValue: (Double) -> String,
    onFormatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    val differenceToPrevious: Double = currentValue - previousValue

    TrendIconTooltip(
        options = options,
        currentStart = currentStart,
        currentEnd = currentEnd,
        currentValue = currentValue,
        previousStart = previousStart,
        previousEnd = previousEnd,
        previousValue = previousValue,
        onFormatValue = onFormatValue,
        onFormatDate = onFormatDate
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size(size)
        ) {
            Shape(
                shape = MaterialShapes.Cookie7Sided,
                color = when {
                    differenceToPrevious > 0.0 && options == DataTabOptions.Incomes -> MaterialTheme.colorScheme.primaryContainer
                    differenceToPrevious < 0.0 && options == DataTabOptions.Incomes -> MaterialTheme.colorScheme.errorContainer
                    differenceToPrevious > 0.0 && options == DataTabOptions.Expenses -> MaterialTheme.colorScheme.errorContainer
                    differenceToPrevious < 0.0 && options == DataTabOptions.Expenses -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            Icon(
                painter = when {
                    differenceToPrevious > 0.0 -> painterResource(R.drawable.ic_increase)
                    differenceToPrevious < 0.0 -> painterResource(R.drawable.ic_decrease)
                    else -> painterResource(R.drawable.ic_identical)
                },
                tint = when {
                    differenceToPrevious > 0.0 && options == DataTabOptions.Incomes -> MaterialTheme.colorScheme.onPrimaryContainer
                    differenceToPrevious < 0.0 && options == DataTabOptions.Incomes -> MaterialTheme.colorScheme.onErrorContainer
                    differenceToPrevious > 0.0 && options == DataTabOptions.Expenses -> MaterialTheme.colorScheme.onErrorContainer
                    differenceToPrevious < 0.0 && options == DataTabOptions.Expenses -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size / 4)
            )
        }
    }
}


@Composable
private fun TrendIconTooltip(
    options: DataTabOptions,
    currentStart: LocalDate,
    currentEnd: LocalDate,
    currentValue: Double,
    previousStart: LocalDate,
    previousEnd: LocalDate,
    previousValue: Double,
    onFormatValue: (Double) -> String,
    onFormatDate: (LocalDate) -> String,
    content: @Composable () -> Unit
) {
    val difference: Double = currentValue - previousValue
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            RichTooltip(
                title = {
                    Text(
                        text = when {
                            difference > 0.0 -> stringResource(R.string.analysis_trend_upTitle, onFormatValue(abs(difference)))
                            difference < 0.0 -> stringResource(R.string.analysis_trend_downTitle, onFormatValue(abs(difference)))
                            else -> stringResource(R.string.analysis_trend_identicalTitle, onFormatValue(abs(difference)))
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = AnnotatedString.fromHtml(when {
                            (options == DataTabOptions.Incomes) && (difference > 0.0) -> stringResource(R.string.analysis_trend_upTextIncomes,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (options == DataTabOptions.Incomes) && (difference < 0.0) -> stringResource(R.string.analysis_trend_downTextIncomes,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            options == DataTabOptions.Incomes -> stringResource(R.string.analysis_trend_identicalTextIncomes,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (options == DataTabOptions.Expenses) && (difference > 0.0) -> stringResource(R.string.analysis_trend_upTextExpenses,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (options == DataTabOptions.Expenses) && (difference < 0.0) -> stringResource(R.string.analysis_trend_downTextExpenses,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            else -> stringResource(R.string.analysis_trend_identicalTextExpenses,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                        }),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}


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


@Composable
private fun TypeResultItem(
    typeResult: DataTabTypeDto,
    valueColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onFormatValue: (Double) -> String,
    onQueryType: suspend (UUID) -> Type?,
    modifier: Modifier = Modifier
) {
    val type: Type? by produceState(null) {
        value = onQueryType(typeResult.typeId)
    }
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            if (type != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                        .size(dimensionResource(R.dimen.image_m))
                ) {
                    Shape(
                        shape = TypeShapes.getShapeForTypeIcon(type!!.icon).shape,
                        color = TypeShapes.getShapeColor(type!!.icon)
                    )
                    Icon(
                        painter = painterResource(type!!.icon.drawableResourceId),
                        contentDescription = "",
                        tint = TypeShapes.getOnShapeColor(type!!.icon),
                        modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = type?.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pluralStringResource(R.plurals.analysis_data_typeListTransferCount, typeResult.overview.transferCount, typeResult.overview.transferCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = onFormatValue(typeResult.overview.sum),
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        }
    }
}

