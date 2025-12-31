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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.ChartColorGenerator
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ColumnChart
import de.christian2003.chaching.plugin.presentation.ui.theme.isDarkTheme
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DiagramDto
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.util.UUID


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

    val precision: AnalysisPrecision = analysisResult.metadata.precision

    //Colors for the analysis:
    val labelColor: Color = when (options) {
        DataTabOptions.Incomes -> MaterialTheme.colorScheme.onSurface
        DataTabOptions.Expenses -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val valueColor: Color = when (options) {
        DataTabOptions.Incomes -> MaterialTheme.colorScheme.primary
        DataTabOptions.Expenses -> MaterialTheme.colorScheme.onSurface
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
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
                options = options,
                diagram = when (options) {
                    DataTabOptions.Incomes -> viewModel.incomesTabData.valuesDiagram
                    DataTabOptions.Expenses -> viewModel.expensesTabData.cumulatedDiagram
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
                options = options,
                diagram = when (options) {
                    DataTabOptions.Incomes -> viewModel.incomesTabData.cumulatedDiagram
                    DataTabOptions.Expenses -> viewModel.expensesTabData.cumulatedDiagram
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
            Headline(
                title = stringResource(R.string.analysis_data_typesListTitle)
            )
        }
        item {
            Box(
                modifier = Modifier.height(bottomPadding)
            )
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
private fun DataLineDiagram(
    options: DataTabOptions,
    diagram: DiagramDto,
    onFormatValue: (Double) -> String,
    onQueryType: suspend (UUID) -> Type?,
    modifier: Modifier = Modifier
) {
    if (diagram.chartColumns.isEmpty()) {
        return
    }

    val colorGenerator = ChartColorGenerator()
    val colors: MutableList<Color> = colorGenerator.generateChartColors(
        primary = when (options) {
            DataTabOptions.Incomes -> MaterialTheme.colorScheme.primary
            DataTabOptions.Expenses -> MaterialTheme.colorScheme.tertiary
        },
        darkTheme = MaterialTheme.isDarkTheme()
    ).toMutableList()
    colors.add(MaterialTheme.colorScheme.outlineVariant)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ColumnChart(
            columns = diagram.chartColumns,
            colors = colors,
            onFormatValue = onFormatValue,
            modifier = modifier
        )
    }
}
