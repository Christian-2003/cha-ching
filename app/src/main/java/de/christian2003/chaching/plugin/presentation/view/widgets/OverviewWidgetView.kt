package de.christian2003.chaching.plugin.presentation.view.widgets

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import de.christian2003.chaching.R


/**
 * Android Views implementation for the preview of the overview widget.
 *
 * Since the widget preview is designed as XML layout for Android Views, we can implement
 * custom code-behind logic. This allows us to display the preview within the Jetpack Compose
 * screens using the AndroidView-composable. The custom logic for this view is required so
 * that changes to the widget settings can be applied as a preview within the widget settings
 * screen.
 *
 * @param context   Context.
 */
class OverviewWidgetView(
    context: Context
): LinearLayout(context) {

    /**
     * Initializes the view.
     */
    init {
        inflate(context, R.layout.widget_overview_preview, this)
    }


    /**
     * Changes the background opacity.
     *
     * @param opacity   Background opacity.
     */
    fun setOpacity(opacity: Float) {
        val backgroundLayout: View? = findViewById(R.id.background_layout)
        if (backgroundLayout != null) {
            backgroundLayout.background.alpha = (255 * opacity).toInt()
        }

        val backgroundTrend: FrameLayout? = findViewById(R.id.background_trend)
        if (backgroundTrend != null) {
            var alpha: Int = (255 * opacity).toInt() + 24
            if (alpha > 255) {
                alpha = 255
            }
            backgroundTrend.background.alpha = alpha
        }

        invalidate()
    }


    /**
     * Changes whether values should be obfuscated.
     *
     * @param isObfuscated  Whether values should be obfuscated.
     */
    fun setObfuscated(isObfuscated: Boolean) {
        val valueTextView: TextView? = findViewById(R.id.value_textview)
        if (valueTextView != null) {
            if (isObfuscated) {
                valueTextView.text = context.getString(R.string.widget_overview_obfuscatedValue)
            }
            else {
                valueTextView.text = context.getString(R.string.widget_overview_previewValue)
            }
        }
    }

}
