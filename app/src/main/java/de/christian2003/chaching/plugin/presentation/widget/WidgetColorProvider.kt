package de.christian2003.chaching.plugin.presentation.widget

import android.content.Context
import androidx.glance.unit.ColorProvider


/**
 * Color provider for the widgets.
 *
 * @param context   Context.
 * @param opacity   Opacity for the widget background.
 */
class WidgetColorProvider(
    private val context: Context,
    private val opacity: Float
) {

    /**
     * Provide the specified color.
     *
     * @param color Color to provide.
     * @param mode  Provider mode.
     * @return      Provided color.
     */
    fun provide(color: ColorProvider, mode: ProviderMode): ColorProvider {
        return when (mode) {
            ProviderMode.Surface -> provideSurface(color)
            ProviderMode.TrendContainer -> provideTrendContainer(color)
        }
    }


    /**
     * Provides the surface color.
     *
     * @param surface   Surface color to provide.
     * @return          Provided surface color.
     */
    private fun provideSurface(surface: ColorProvider): ColorProvider {
        return androidx.glance.color.ColorProvider(
            day = surface.getColor(context).copy(alpha = opacity),
            night = surface.getColor(context).copy(alpha = opacity),
        )
    }


    /**
     * Provides the trend container color.
     *
     * @param trendContainer    Trend container color to provide.
     * @return                  Provided trend container color.
     */
    private fun provideTrendContainer(trendContainer: ColorProvider): ColorProvider {
        var alpha: Float = opacity + (24f / 255f)
        if (alpha > 1f) {
            alpha = 1f
        }
        return androidx.glance.color.ColorProvider(
            day = trendContainer.getColor(context).copy(alpha = alpha),
            night = trendContainer.getColor(context).copy(alpha = alpha),
        )
    }

}


/**
 * Provider modes.
 */
enum class ProviderMode {
    Surface,
    TrendContainer
}
