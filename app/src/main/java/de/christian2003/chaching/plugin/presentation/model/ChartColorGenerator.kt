package de.christian2003.chaching.plugin.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils


/**
 * Generates colors for charts. This uses HSL color theory to determine suitable colors
 * that contrast well for light and dark modes.
 */
class ChartColorGenerator {

    /**
     * Generates the chart colors.
     *
     * @param primary   Seed color
     * @param darkTheme Whether the system is in dark theme.
     */
    fun generateChartColors(
        primary: Color,
        darkTheme: Boolean
    ): List<Color> {
        val baseHsl: FloatArray = colorToHsl(primary)
        val baseHue: Float = baseHsl[0]
        val baseLightness: Float = if (darkTheme) { 0.65f } else { 0.45f }

        //Hue
        val hueOffset: List<Float> = if (isGreenishColor(baseHue)) {
            listOf(0f, 70f, 140f) //More aggressive offsets to better distinguish if color is green-ish
        } else {
            listOf(0f, 40f, 80f) //Regular offset for regular colors
        }

        //Saturation:
        val saturation: Float = if (isGreenishColor(baseHue)) { 0.55f } else { 0.65f }

        //Lightness:
        val lightnessSteps: List<Float> = listOf(0f, -0.08f, 0.08f)

        return hueOffset.zip(lightnessSteps).map { (hOffset, lOffset) ->
            hslToColor(
                h = (baseHsl[0] + hOffset) % 360f,
                s = saturation,
                l = (baseLightness + lOffset).coerceIn(0.3f, 0.8f)
            )
        }
    }


    /**
     * Converts the specified color to HSL.
     *
     * @param color Color to convert to HSL.
     * @return      Color in HSL format.
     */
    private fun colorToHsl(color: Color): FloatArray {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color.toArgb(), hsl)
        return hsl
    }


    /**
     * Converts the specified HSL values to a color.
     *
     * @param h Hue.
     * @param s Saturation.
     * @param l Lightness.
     * @return  Color converted from HSL.
     */
    private fun hslToColor(h: Float, s: Float, l: Float): Color {
        return Color(ColorUtils.HSLToColor(floatArrayOf(h, s, l)))
    }


    /**
     * Determines whether the hue determines a greenish color. This is important
     * since green is near the maximum perceived lightness and therefore different
     * to differentiate.
     *
     * @param hue   Hue.
     * @return      Whether the hue indicates a greenish color.
     */
    private fun isGreenishColor(hue: Float): Boolean {
        return hue in 70f..160f
    }

}
