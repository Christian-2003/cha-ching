package de.christian2003.chaching.plugin.presentation.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver


/**
 * Implements the widget provider for Cha Ching.
 */
class ChaChingWidgetReceiver : GlanceAppWidgetReceiver() {

    /**
     * Overview widget for the app displays the total amount of money earned in the last 31 days.
     */
    override val glanceAppWidget: GlanceAppWidget = OverviewWidget()

}
