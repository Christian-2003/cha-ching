package de.christian2003.chaching.plugin.presentation.widget

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.Text
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import de.christian2003.chaching.plugin.presentation.ui.theme.ChaChingThemeGlance
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.overview.OverviewCalcResult
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.ChaChingApplication
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingRepository
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate


/**
 * Implements the overview widget which displays the total amount of money earned in the last 31
 * days.
 */
class OverviewWidget : GlanceAppWidget() {

    /**
     * Number format for the value displayed.
     */
    private val numberFormat: NumberFormat = DecimalFormat("#,###.00")


    /**
     * Returns the size modes that are supported by the widget implementation.
     */
    override val sizeMode: SizeMode = SizeMode.Responsive(setOf(SMALL_SQUARE, LARGE_SQUARE))


    /**
     * Provides the Android Glance code for the widget.
     *
     * @param context   Context for the widget.
     * @param id        ID of the widget.
     */
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        var isError = false
        var data: OverviewCalcResult? = null
        val repository: ChaChingRepository = (context as ChaChingApplication).getRepository()
        withContext(Dispatchers.IO) {
            try {
                val now: LocalDate = LocalDate.now()
                val types: List<Type> = repository.getAllTypes().first()
                val transfers: List<Transfer> = repository.getAllTransfersInDateRange(now.minusDays(31), now).first()
                data = OverviewCalcResult(transfers, types)
            }
            catch (_: Exception) {
                isError = true
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
                if (isError) {
                    //Data cannot be loaded:
                    ErrorDisplay()
                }
                else if (data == null || data.totalValue == 0) {
                    //No incomes:
                    EmptyDisplay()
                }
                else {
                    //Data loaded:
                    OverviewDisplay(data)
                }
            }
        }
    }


    /**
     * Displays the normal state. This is displayed once the data is loaded successfully.
     */
    @Composable
    private fun OverviewDisplay(data: OverviewCalcResult) {
        val size = LocalSize.current
        if (size.width >= LARGE_SQUARE.width) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
                    .padding(horizontal = 6.dp)
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.widget_overview_text),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 16.sp
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                ValueDisplay(
                    value = data.totalValue,
                    modifier = GlanceModifier.padding(start = 6.dp)
                )
            }
        }
        else {
            Column(
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.widget_overview_text),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    modifier = GlanceModifier.padding(bottom = 6.dp)
                )
                ValueDisplay(
                    value = data.totalValue
                )
            }
        }
    }


    /**
     * Displays the value earned in the last 31 days.
     *
     * @param value     Value in cents to display.
     * @param modifier  Glance modifier.
     */
    @Composable
    private fun ValueDisplay(
        value: Int,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val formattedValue = numberFormat.format(value.toDouble() / 100.0)
        Text(
            text = LocalContext.current.getString(R.string.value_format, formattedValue),
            style = TextStyle(
                color = GlanceTheme.colors.onPrimaryContainer,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = modifier
                .padding(
                    vertical = 4.dp,
                    horizontal = 12.dp
                )
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(128.dp)
        )
    }


    /**
     * Displays the empty state. This is displayed if the user has not earned any money in the
     * last 31 days.
     */
    @Composable
    private fun EmptyDisplay() {
        val size = LocalSize.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
        ) {
            Image(
                provider = ImageProvider(R.drawable.el_overview),
                contentDescription = "",
                modifier = GlanceModifier.size(96.dp)
            )
            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.widget_overview_emptyTitle),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = if (size.width >= LARGE_SQUARE.width) { 16.sp } else { 12.sp },
                        fontWeight = FontWeight.Bold
                    )
                )
                if (size.width >= LARGE_SQUARE.width) {
                    Text(
                        text = LocalContext.current.getString(R.string.widget_overview_emptyText),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }


    /**
     * Displays the error state. This is displayed if some error occurs while loading data.
     */
    @Composable
    private fun ErrorDisplay() {
        val size = LocalSize.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
        ) {
            Image(
                provider = ImageProvider(R.drawable.err_overview),
                contentDescription = "",
                modifier = GlanceModifier.size(96.dp)
            )
            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.widget_overview_errorTitle),
                    style = TextStyle(
                        color = GlanceTheme.colors.error,
                        fontSize = if (size.width >= LARGE_SQUARE.width) { 16.sp } else { 12.sp },
                        fontWeight = FontWeight.Bold
                    )
                )
                if (size.width >= LARGE_SQUARE.width) {
                    Text(
                        text = LocalContext.current.getString(R.string.widget_overview_errorText),
                        style = TextStyle(
                            color = GlanceTheme.colors.error,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }


    companion object {

        /**
         * Sizes for the small widget (2 x 1 cells).
         */
        private val SMALL_SQUARE = DpSize(140.dp, 70.dp)

        /**
         * Sizes for the large widget (3 x 1 cells).
         */
        private val LARGE_SQUARE = DpSize(width = 210.dp, height = 70.dp)

    }

}
