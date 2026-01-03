package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.plugin.presentation.model.ChartColorGenerator
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ColumnChart
import de.christian2003.chaching.plugin.presentation.ui.theme.isDarkTheme
import de.christian2003.chaching.plugin.presentation.view.analysis.AnalysisViewModel
import de.christian2003.chaching.plugin.presentation.view.analysis.model.AnalysisTab
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DiagramDto
import de.christian2003.chaching.plugin.presentation.view.analysis.model.OverviewTabDto
import java.time.LocalDate


@Composable
fun AnalysisOverviewTab(
    viewModel: AnalysisViewModel,
    bottomPadding: Dp
) {
    val analysisResult: LargeAnalysisResult? = viewModel.analysisResult
    if (analysisResult == null) {
        return
    }
    val overviewData: OverviewTabDto = viewModel.overviewTabData
    val precision: AnalysisPrecision = analysisResult.metadata.precision

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BudgetOverview(
            precision = precision,
            currentStart = analysisResult.currentSpan.start,
            currentEnd = analysisResult.currentSpan.end,
            currentBudget = overviewData.currentBudget,
            currentAvg = overviewData.currentAvgBudgetPerNormalizedDate,
            previousStart = analysisResult.previousSpan.start,
            previousEnd = analysisResult.previousSpan.end,
            previousBudget = overviewData.previousBudget,
            previousAvg = overviewData.previousAvgBudgetPerNormalizedDate,
            onFormatValue = {
                viewModel.formatValue(it)
            },
            onFormatDate = {
                viewModel.formatDate(it)
            },
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
        )

        Headline(
            title = when(precision) {
                AnalysisPrecision.Month -> stringResource(R.string.analysis_overview_budgetPerMonth)
                AnalysisPrecision.Quarter -> stringResource(R.string.analysis_overview_budgetPerQuarter)
                AnalysisPrecision.Year -> stringResource(R.string.analysis_overview_budgetPerYear)
            }
        )

        BudgetsDiagram(
            diagram = overviewData.budgetByNormalizedDateDiagram,
            onFormatValue = {
                viewModel.formatValue(it)
            }
        )

        Box(
            modifier = Modifier.height(bottomPadding)
        )
    }
}


@Composable
private fun BudgetOverview(
    precision: AnalysisPrecision,
    currentStart: LocalDate,
    currentEnd: LocalDate,
    currentBudget: Double,
    currentAvg: Double,
    previousStart: LocalDate,
    previousEnd: LocalDate,
    previousBudget: Double,
    previousAvg: Double,
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = onFormatValue(currentBudget),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (currentBudget >= 0.0) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.analysis_overview_total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TrendIcon(
                tab = AnalysisTab.Overview,
                currentStart = currentStart,
                currentEnd = currentEnd,
                currentValue = currentBudget,
                previousStart = previousStart,
                previousEnd = previousEnd,
                previousValue = previousBudget,
                size = dimensionResource(R.dimen.image_m),
                onFormatValue = onFormatValue,
                onFormatDate = onFormatDate,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            )
        }
        OverviewCardAvgItem(
            tab = AnalysisTab.Overview,
            label = when (precision) {
                AnalysisPrecision.Month -> stringResource(R.string.analysis_data_monthAvg)
                AnalysisPrecision.Quarter -> stringResource(R.string.analysis_data_quarterAvg)
                AnalysisPrecision.Year -> stringResource(R.string.analysis_data_yearAvg)
            },
            currentStart = currentStart,
            currentEnd = currentEnd,
            currentAvg = currentAvg,
            previousStart = previousStart,
            previousEnd = previousEnd,
            previousAvg = previousAvg,
            valueColor = if (currentAvg > 0.0) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.error
            },
            onFormatValue = onFormatValue,
            onFormatDate = onFormatDate,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_vertical))
                .height(IntrinsicSize.Min)
        )
    }
}


@Composable
private fun BudgetsDiagram(
    diagram: DiagramDto,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    if (diagram.chartColumns.isEmpty()) {
        return
    }

    val colorGenerator = ChartColorGenerator()
    val positiveColors: List<Color> = colorGenerator.generateChartColors(
        seed = MaterialTheme.colorScheme.secondary,
        darkTheme = MaterialTheme.isDarkTheme()
    )
    val negativeColors: List<Color> = listOf(MaterialTheme.colorScheme.error)

    ColumnChart(
        columns = diagram.chartColumns,
        positiveColors = positiveColors,
        negativeColors = negativeColors,
        onFormatValue = onFormatValue,
        modifier = modifier.fillMaxWidth()
    )
}
