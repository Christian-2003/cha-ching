package de.christian2003.chaching.plugin.presentation.widget.overview

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import dagger.hilt.android.EntryPointAccessors
import de.christian2003.chaching.application.analysis.small.SmallAnalysisUseCase
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.plugin.presentation.ui.theme.ChaChingThemeGlance
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate


/**
 * Implements the overview widget which displays the total amount of money earned in the last 31
 * days.
 */
class OverviewWidget : GlanceAppWidget() {

    /**
     * Returns the size modes that are supported by the widget implementation.
     */
    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(Tiles1x2, Tiles1x4, Tiles2x2, Tiles2x4))


    /**
     * Provides the Android Glance code for the widget.
     *
     * @param context   Context for the widget.
     * @param id        ID of the widget.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint: OverviewWidgetEntryPoint = EntryPointAccessors.fromApplication(
            context = context.applicationContext,
            entryPoint = OverviewWidgetEntryPoint::class.java
        )
        val smallAnalysisUseCase: SmallAnalysisUseCase = entryPoint.getSmallAnalysisUseCase()
        val valueFormatterService: ValueFormatterService = entryPoint.getValueFormatterService()


        var smallAnalysisResult: SmallAnalysisResult? = null
        withContext(Dispatchers.IO) {
            try {
                smallAnalysisResult = smallAnalysisUseCase.analyzeData(LocalDate.now())
            }
            finally {
                update(context, id)
            }
        }

        val preferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val useGlobalTheme: Boolean = preferences.getBoolean("global_theme", false)
        val themeContrast: ThemeContrast = ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]

        provideContent {
            ChaChingThemeGlance(
                context = context,
                dynamicColor = useGlobalTheme,
                contrast = themeContrast
            ) {
                if (smallAnalysisResult != null) {
                    OverviewDisplay(
                        smallAnalysisResult = smallAnalysisResult,
                        onFormatValue = {
                            valueFormatterService.format(it)
                        }
                    )
                }
            }
        }
    }


    /**
     * Composable widget content.
     *
     * @param smallAnalysisResult   Analysis result to display.
     * @param onFormatValue         Callback invoked to format a value.
     */
    @Composable
    private fun OverviewDisplay(
        smallAnalysisResult: SmallAnalysisResult,
        onFormatValue: (Double) -> String
    ) {
        val size = LocalSize.current
        when {
            (size.width >= Tiles2x4.width && size.height >= Tiles2x4.height) -> {
                OverviewWidget2x4(
                    smallAnalysisResult = smallAnalysisResult,
                    onFormatValue = onFormatValue
                )
            }
            (size.width >= Tiles2x2.width && size.height >= Tiles2x2.height) -> {
                OverviewWidget2x2(
                    smallAnalysisResult = smallAnalysisResult,
                    onFormatValue = onFormatValue
                )
            }
            (size.width >= Tiles1x4.width && size.height >= Tiles1x4.height) -> {
                OverviewWidget1x4(
                    smallAnalysisResult = smallAnalysisResult,
                    onFormatValue = onFormatValue
                )
            }
            else -> {
                OverviewWidget1x2(
                    smallAnalysisResult = smallAnalysisResult,
                    onFormatValue = onFormatValue
                )
            }
        }
    }


    companion object {

        private val Tiles1x2 = DpSize(width = 140.dp, height = 70.dp)
        private val Tiles1x4 = DpSize(width = 300.dp, height = 70.dp)
        private val Tiles2x2 = DpSize(width = 140.dp, height = 140.dp)
        private val Tiles2x4 = DpSize(width = 300.dp, height = 140.dp)
    }

}
