package de.christian2003.chaching.plugin.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.text.Text
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import de.christian2003.chaching.plugin.presentation.MainActivity
import de.christian2003.chaching.plugin.presentation.ui.theme.ChaChingThemeGlance
import de.christian2003.chaching.R


class OverviewWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            ChaChingThemeGlance {
                OverviewContent()
            }
        }
    }


    @Composable
    private fun OverviewContent() {
        Column(
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp)
        ) {
            Text(
                text = LocalContext.current.getString(R.string.widget_overview_text),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier.padding(bottom = 6.dp)
            )
            ValueDisplay()
        }
    }


    @Composable
    private fun ValueDisplay() {
        Text(
            text = "1.234,56 €",
            style = TextStyle(
                color = GlanceTheme.colors.onPrimaryContainer,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier
                .padding(
                    vertical = 4.dp,
                    horizontal = 12.dp
                )
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(128.dp)
        )
    }

}
