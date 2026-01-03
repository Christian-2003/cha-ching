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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOverviewDto
import java.time.LocalDate
import kotlin.math.abs


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


/**
 * Item within the overview card that displays the average per month OR per transfer.
 *
 * @param options       Data tab options.
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


/**
 * Trend icon shows whether a value increases, decreases or stays the same in between the current
 * time span and the previous time span.
 *
 * @param options       Data tab options.
 * @param currentStart  Start date of the current time span.
 * @param currentEnd    End date of the current time span.
 * @param currentValue  Value if the current time span.
 * @param previousStart Start date of the previous time span.
 * @param previousEnd   End date of the previous time span.
 * @param previousValue Value of the previous time span.
 * @param size          Size for the trend icon.
 * @param onFormatValue Callback invoked to format a value.
 * @param onFormatDate  Callback invoked to format a date.
 * @param modifier      Modifier.
 */
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


/**
 * Tooltip container for the trend icon.
 *
 * @param options       Data tab options.
 * @param currentStart  Start date of the current time span.
 * @param currentEnd    End date of the current time span.
 * @param currentValue  Value if the current time span.
 * @param previousStart Start date of the previous time span.
 * @param previousEnd   End date of the previous time span.
 * @param previousValue Value of the previous time span.
 * @param onFormatValue Callback invoked to format a value.
 * @param onFormatDate  Callback invoked to format a date.
 * @param content       Composable content for the tooltip container.
 */
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
