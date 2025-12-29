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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.R


/**
 * Displays the overview widget for 1x2 tiles.
 *
 * @param smallAnalysisResult   Analysis result to display.
 * @param onFormatValue         Callback invoked to format a value.
 */
@Composable
fun OverviewWidget1x2(
    smallAnalysisResult: SmallAnalysisResult,
    onFormatValue: (Double) -> String
) {
    OverviewWidgetBudgetSmall(
        smallAnalysisResult = smallAnalysisResult,
        onFormatValue = onFormatValue,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp)
    )
}


/**
 * Small display for the budget of the current month.
 *
 * @param smallAnalysisResult   Analysis result from which to display the budget.
 * @param onFormatValue         Callback invoked to format a value.
 * @param modifier              Modifier.
 */
@Composable
fun OverviewWidgetBudgetSmall(
    smallAnalysisResult: SmallAnalysisResult,
    onFormatValue: (Double) -> String,
    modifier: GlanceModifier = GlanceModifier
) {
    val differenceToLastMonth: Double = smallAnalysisResult.currentMonth.budget - smallAnalysisResult.previousMonth.budget
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column {
            Text(
                text = onFormatValue(smallAnalysisResult.currentMonth.budget),
                style = TextStyle(
                    color = when {
                        smallAnalysisResult.currentMonth.budget > 0.0 -> GlanceTheme.colors.primary
                        smallAnalysisResult.currentMonth.budget < 0.0 -> GlanceTheme.colors.error
                        else -> GlanceTheme.colors.onSurface
                    },
                    textAlign = TextAlign.Start,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = LocalContext.current.getString(R.string.widget_overview_budget),
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
                    .size(48.dp)
                    .background(when {
                        differenceToLastMonth > 0.0 -> GlanceTheme.colors.primaryContainer
                        differenceToLastMonth < 0.0 -> GlanceTheme.colors.errorContainer
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
                        differenceToLastMonth > 0.0 -> ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer)
                        differenceToLastMonth < 0.0 -> ColorFilter.tint(GlanceTheme.colors.onErrorContainer)
                        else -> ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant)
                    },
                    contentDescription = "",
                    modifier = GlanceModifier.size(36.dp)
                )
            }
        }
    }
}
