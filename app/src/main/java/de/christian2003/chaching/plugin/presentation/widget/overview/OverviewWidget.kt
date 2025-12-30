package de.christian2003.chaching.plugin.presentation.widget.overview

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import dagger.hilt.android.EntryPointAccessors
import de.christian2003.chaching.application.analysis.small.SmallAnalysisUseCase
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.plugin.presentation.ui.theme.ChaChingThemeGlance
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.Hilt_MainActivity
import de.christian2003.chaching.plugin.presentation.MainActivity
import de.christian2003.chaching.plugin.presentation.widget.WidgetColorProvider
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

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val destinationKey = ActionParameters.Key<String>(MainActivity.KEY_DESTINATION)


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


        provideContent {
            //val preferences = currentState<Preferences>()
            //val now = preferences[longPreferencesKey("now")]
            if (smallAnalysisResult != null) {
                OverviewWidgetContent(
                    smallAnalysisResult = smallAnalysisResult,
                    onFormatValue = {
                        valueFormatterService.format(it)
                    }
                )
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
    private fun OverviewWidgetContent(
        smallAnalysisResult: SmallAnalysisResult,
        onFormatValue: (Double) -> String
    ) {
        val context: Context = LocalContext.current
        val preferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        val useGlobalTheme: Boolean = preferences.getBoolean("global_theme", false)
        val themeContrast: ThemeContrast = ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]

        val opacity: Float = preferences.getFloat("widget_overview_opacity", 1f)
        val isObfuscated: Boolean = preferences.getBoolean("widget_overview_isObfuscated", false)
        val clickAction: Int = preferences.getInt("widget_overview_clickAction", 0)

        val size = LocalSize.current

        val widgetColorProvider = WidgetColorProvider(context, opacity)

        val invokeOnFormatValue: (Double) -> String = {
            if (isObfuscated) {
                context.getString(R.string.widget_overview_obfuscatedValue)
            } else {
                onFormatValue(it)
            }
        }

        val destination: String = when(clickAction) {
            1 -> "analysis"
            2 -> "widgets"
            else -> "main"
        }
        val onClick: Action = actionStartActivity<MainActivity>(
            parameters = actionParametersOf(destinationKey to destination)
        )

        ChaChingThemeGlance(
            context = context,
            dynamicColor = useGlobalTheme,
            contrast = themeContrast
        ) {
            when {
                (size.width >= Tiles2x4.width && size.height >= Tiles2x4.height) -> {
                    OverviewWidget2x4(
                        smallAnalysisResult = smallAnalysisResult,
                        onFormatValue = invokeOnFormatValue,
                        onProvideColor = { color, mode ->
                            widgetColorProvider.provide(color, mode)
                        },
                        onClick = onClick
                    )
                }
                (size.width >= Tiles2x2.width && size.height >= Tiles2x2.height) -> {
                    OverviewWidget2x2(
                        smallAnalysisResult = smallAnalysisResult,
                        onFormatValue = invokeOnFormatValue,
                        onProvideColor = { color, mode ->
                            widgetColorProvider.provide(color, mode)
                        },
                        onClick = onClick
                    )
                }
                (size.width >= Tiles1x4.width && size.height >= Tiles1x4.height) -> {
                    OverviewWidget1x4(
                        smallAnalysisResult = smallAnalysisResult,
                        onFormatValue = invokeOnFormatValue,
                        onProvideColor = { color, mode ->
                            widgetColorProvider.provide(color, mode)
                        },
                        onClick = onClick
                    )
                }
                else -> {
                    OverviewWidget1x2(
                        smallAnalysisResult = smallAnalysisResult,
                        onFormatValue = invokeOnFormatValue,
                        onProvideColor = { color, mode ->
                            widgetColorProvider.provide(color, mode)
                        },
                        onClick = onClick
                    )
                }
            }
        }
    }


    companion object {

        val Tiles1x2 = DpSize(width = 140.dp, height = 70.dp)
        val Tiles1x4 = DpSize(width = 300.dp, height = 70.dp)
        val Tiles2x2 = DpSize(width = 140.dp, height = 140.dp)
        val Tiles2x4 = DpSize(width = 300.dp, height = 140.dp)
    }

}
