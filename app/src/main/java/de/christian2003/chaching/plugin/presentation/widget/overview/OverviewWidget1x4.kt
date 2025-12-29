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
 * Displays the overview widget for 1x4 tiles.
 *
 * @param smallAnalysisResult   Analysis result to display.
 * @param onFormatValue         Callback invoked to format a value.
 */
@Composable
fun OverviewWidget1x4(
    smallAnalysisResult: SmallAnalysisResult,
    onFormatValue: (Double) -> String
) {
    val differenceToLastMonth: Double = smallAnalysisResult.currentMonth.budget - smallAnalysisResult.previousMonth.budget
    Row(
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp)
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
                .cornerRadius(24.dp)
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

        Column(
            modifier = GlanceModifier.padding(start = 12.dp)
        ) {
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
            modifier = GlanceModifier
                .padding(start = 12.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.widget_overview_incomes),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        modifier = GlanceModifier.padding(end = 12.dp)
                    )
                    Text(
                        text = onFormatValue(smallAnalysisResult.currentMonth.incomes.totalSum),
                        style = TextStyle(
                            color = GlanceTheme.colors.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocalContext.current.getString(R.string.widget_overview_expenses),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        modifier = GlanceModifier.padding(end = 12.dp)
                    )
                    Text(
                        text = onFormatValue(smallAnalysisResult.currentMonth.expenses.totalSum),
                        style = TextStyle(
                            color = GlanceTheme.colors.tertiary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}