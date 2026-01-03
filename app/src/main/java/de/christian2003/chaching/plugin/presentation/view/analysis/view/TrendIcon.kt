package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.Dp
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.view.analysis.model.AnalysisTab
import java.time.LocalDate
import kotlin.math.abs


/**
 * Trend icon shows whether a value increases, decreases or stays the same in between the current
 * time span and the previous time span.
 *
 * @param tab           Tab on which the trend icon is shown.
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
fun TrendIcon(
    tab: AnalysisTab,
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
        tab = tab,
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
                    differenceToPrevious > 0.0 && tab == AnalysisTab.Incomes -> MaterialTheme.colorScheme.primaryContainer
                    differenceToPrevious < 0.0 && tab == AnalysisTab.Incomes -> MaterialTheme.colorScheme.errorContainer
                    differenceToPrevious > 0.0 && tab == AnalysisTab.Expenses -> MaterialTheme.colorScheme.errorContainer
                    differenceToPrevious < 0.0 && tab == AnalysisTab.Expenses -> MaterialTheme.colorScheme.primaryContainer
                    differenceToPrevious > 0.0 && tab == AnalysisTab.Overview -> MaterialTheme.colorScheme.primaryContainer
                    differenceToPrevious < 0.0 && tab == AnalysisTab.Overview -> MaterialTheme.colorScheme.errorContainer
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
                    differenceToPrevious > 0.0 && tab == AnalysisTab.Incomes -> MaterialTheme.colorScheme.onPrimaryContainer
                    differenceToPrevious < 0.0 && tab == AnalysisTab.Incomes -> MaterialTheme.colorScheme.onErrorContainer
                    differenceToPrevious > 0.0 && tab == AnalysisTab.Expenses -> MaterialTheme.colorScheme.onErrorContainer
                    differenceToPrevious < 0.0 && tab == AnalysisTab.Expenses -> MaterialTheme.colorScheme.onPrimaryContainer
                    differenceToPrevious > 0.0 && tab == AnalysisTab.Overview -> MaterialTheme.colorScheme.onPrimaryContainer
                    differenceToPrevious < 0.0 && tab == AnalysisTab.Overview -> MaterialTheme.colorScheme.onErrorContainer
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
 * @param tab           Tab on which the trend icon is shown.
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
    tab: AnalysisTab,
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
                            (tab == AnalysisTab.Incomes) && (difference > 0.0) -> stringResource(R.string.analysis_trend_upTextIncomes,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (tab == AnalysisTab.Incomes) && (difference < 0.0) -> stringResource(R.string.analysis_trend_downTextIncomes,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            tab == AnalysisTab.Incomes -> stringResource(R.string.analysis_trend_identicalTextIncomes,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (tab == AnalysisTab.Expenses) && (difference > 0.0) -> stringResource(R.string.analysis_trend_upTextExpenses,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (tab == AnalysisTab.Expenses) && (difference < 0.0) -> stringResource(R.string.analysis_trend_downTextExpenses,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            tab == AnalysisTab.Expenses -> stringResource(R.string.analysis_trend_identicalTextExpenses,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (tab == AnalysisTab.Overview) && (difference > 0.0) -> stringResource(R.string.analysis_trend_upTextOverview,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            (tab == AnalysisTab.Overview) && (difference < 0.0) -> stringResource(R.string.analysis_trend_downTextOverview,
                                onFormatDate(currentStart),
                                onFormatDate(currentEnd),
                                onFormatValue(previousValue),
                                onFormatValue(currentValue),
                                onFormatDate(previousStart),
                                onFormatDate(previousEnd)
                            )
                            else -> stringResource(R.string.analysis_trend_identicalTextOverview,
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
