package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.plugin.presentation.view.analysis.model.AnalysisTab
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOverviewDto
import java.time.LocalDate


/**
 * Overview card displays an overview of incomes or expenses.
 *
 * @param options       Data tab options.
 * @param overview      Overview data to display.
 * @param valueColor    Color with which to display the value.
 * @param precision     Analysis precision.
 * @param currentStart  Start date of the current time span.
 * @param currentEnd    End date of the current time span.
 * @param previousStart Start date of the previous time span.
 * @param previousEnd   End date of the previous time span.
 * @param onFormatValue Callback invoked to format a value.
 * @param onFormatDate  Callback invoked to format a date.
 * @param modifier      Modifier.
 */
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
                tab = when (options) {
                    DataTabOptions.Incomes -> AnalysisTab.Incomes
                    DataTabOptions.Expenses -> AnalysisTab.Expenses
                },
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
                tab = when (options) {
                    DataTabOptions.Incomes -> AnalysisTab.Incomes
                    DataTabOptions.Expenses -> AnalysisTab.Expenses
                },
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
                tab = when (options) {
                    DataTabOptions.Incomes -> AnalysisTab.Incomes
                    DataTabOptions.Expenses -> AnalysisTab.Expenses
                },
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


/**
 * Item within the overview card that displays the average per month OR per transfer.
 *
 * @param tab           Tab on which the item is shown.
 * @param label         Label for the item.
 * @param valueColor    Color with which to display the value.
 * @param currentStart  Start date of the current time span.
 * @param currentEnd    End date of the current time span.
 * @param currentAvg    Value if the current time span.
 * @param previousStart Start date of the previous time span.
 * @param previousEnd   End date of the previous time span.
 * @param previousAvg   Value of the previous time span.
 * @param onFormatValue Callback invoked to format a value.
 * @param onFormatDate  Callback invoked to format a date.
 * @param modifier      Modifier.
 */
@Composable
fun OverviewCardAvgItem(
    tab: AnalysisTab,
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
                    tab = tab,
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
