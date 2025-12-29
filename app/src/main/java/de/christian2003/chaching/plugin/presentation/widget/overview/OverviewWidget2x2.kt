package de.christian2003.chaching.plugin.presentation.widget.overview

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisData
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult


/**
 * Displays the overview widget for 2x2 tiles.
 *
 * @param smallAnalysisResult   Analysis result to display.
 * @param onFormatValue         Callback invoked to format a value.
 */
@Composable
fun OverviewWidget2x2(
    smallAnalysisResult: SmallAnalysisResult,
    onFormatValue: (Double) -> String
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp)
    ) {
        //Budget:
        OverviewWidgetBudgetSmall(
            smallAnalysisResult = smallAnalysisResult,
            onFormatValue = onFormatValue
        )

        //Incomes and expenses:
        Column(
            verticalAlignment = Alignment.Bottom,
            modifier = GlanceModifier.fillMaxHeight()
        ) {
            OverviewWidgetSmallAnalysisData(
                smallAnalysisDataCurrent = smallAnalysisResult.currentMonth.incomes,
                smallAnalysisDataPrevious = smallAnalysisResult.previousMonth.incomes,
                isIncome = true,
                onFormatValue = onFormatValue,
                modifier = GlanceModifier.padding(top = 12.dp)
            )
            OverviewWidgetSmallAnalysisData(
                smallAnalysisDataCurrent = smallAnalysisResult.currentMonth.expenses,
                smallAnalysisDataPrevious = smallAnalysisResult.previousMonth.expenses,
                isIncome = false,
                onFormatValue = onFormatValue,
                modifier = GlanceModifier.padding(top = 12.dp)
            )
        }
    }
}


/**
 * Displays the incomes OR expenses for the current month.
 *
 * @param smallAnalysisDataCurrent  Analysis data for the current month.
 * @param smallAnalysisDataPrevious Analysis data for the previous month.
 * @param isIncome                  Whether this is used to display income (or expenses).
 * @param onFormatValue             Callback invoked to format a value.
 * @param modifier                  Modifier.
 */
@Composable
private fun OverviewWidgetSmallAnalysisData(
    smallAnalysisDataCurrent: SmallAnalysisData,
    smallAnalysisDataPrevious: SmallAnalysisData,
    isIncome: Boolean,
    onFormatValue: (Double) -> String,
    modifier: GlanceModifier = GlanceModifier
) {
    val differenceToLastMonth: Double = smallAnalysisDataCurrent.totalSum - smallAnalysisDataPrevious.totalSum
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = GlanceModifier.padding(end = 12.dp)
        ) {
            Text(
                text = onFormatValue(smallAnalysisDataCurrent.totalSum),
                style = TextStyle(
                    color = when {
                        isIncome -> GlanceTheme.colors.primary
                        else -> GlanceTheme.colors.tertiary
                    },
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = when {
                    isIncome -> LocalContext.current.getString(R.string.widget_overview_incomes)
                    else -> LocalContext.current.getString(R.string.widget_overview_expenses)
                },
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp
                )
            )
        }

        Row(
            horizontalAlignment = Alignment.End,
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier
                    .size(32.dp)
                    .background(when {
                        differenceToLastMonth > 0.0 && isIncome -> GlanceTheme.colors.primaryContainer
                        differenceToLastMonth < 0.0 && isIncome -> GlanceTheme.colors.errorContainer
                        differenceToLastMonth > 0.0 -> GlanceTheme.colors.errorContainer
                        differenceToLastMonth < 0.0 -> GlanceTheme.colors.primaryContainer
                        else -> GlanceTheme.colors.surfaceVariant
                    })
                    .cornerRadius(100.dp)
            ) {
                Image(
                    provider = when {
                        differenceToLastMonth > 0.0 -> ImageProvider(R.drawable.ic_increase)
                        differenceToLastMonth < 0.0 -> ImageProvider(R.drawable.ic_decrease)
                        else -> ImageProvider(R.drawable.ic_identical)
                    },
                    colorFilter = when {
                        differenceToLastMonth > 0.0 && isIncome -> ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer)
                        differenceToLastMonth < 0.0 && isIncome -> ColorFilter.tint(GlanceTheme.colors.onErrorContainer)
                        differenceToLastMonth > 0.0 -> ColorFilter.tint(GlanceTheme.colors.onErrorContainer)
                        differenceToLastMonth < 0.0 -> ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer)
                        else -> ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant)
                    },
                    contentDescription = "",
                    modifier = GlanceModifier.size(24.dp)
                )
            }
        }
    }
}
