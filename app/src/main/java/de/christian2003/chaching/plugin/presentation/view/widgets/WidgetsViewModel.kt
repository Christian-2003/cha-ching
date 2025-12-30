package de.christian2003.chaching.plugin.presentation.view.widgets

import android.app.Application
import android.app.WallpaperManager
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.plugin.presentation.model.HelpCards
import de.christian2003.chaching.plugin.presentation.widget.ChaChingWidgetReceiver
import de.christian2003.chaching.plugin.presentation.widget.overview.OverviewWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the screen through which to edit widget settings.
 *
 * @param application   Application.
 */
@HiltViewModel
class WidgetsViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    /**
     * Preferences.
     */
    private val preferences: SharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)


    /**
     * Image bitmap of the launcher wallpaper. This is displayed behind the widget preview.
     * TODO: Due to permissions-issues, this is currently the default wallpaper. Change this
     * TODO: to be the actual wallpaper.
     */
    var wallpaperBitmap: ImageBitmap? by mutableStateOf(null)

    /**
     * Opacity for the widget background.
     */
    var widgetOpacity: Float by mutableFloatStateOf(preferences.getFloat("widget_overview_opacity", 1f))

    /**
     * Indicates whether to obfuscate values on the widget.
     */
    var widgetIsObfuscated: Boolean by mutableStateOf(preferences.getBoolean("widget_overview_isObfuscated", false))

    /**
     * Click action for the widget.
     */
    var widgetClickAction: Int by mutableIntStateOf(preferences.getInt("widget_overview_clickAction", 0))

    /**
     * Indicates whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCards.HELP_WIDGETS.getVisible(application))
        private set

    /**
     * Number of active widget instances in the launcher.
     */
    var numberOfWidgetInstances: Int? by mutableStateOf(null)
        private set


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            //Get wallpaper image:
            val wallpaperManager = WallpaperManager.getInstance(application)
            val wallpaperDrawable = wallpaperManager.builtInDrawable
            wallpaperBitmap = wallpaperDrawable.toBitmap().asImageBitmap()

            //Get number of widget instances:
            val manager = GlanceAppWidgetManager(application)
            numberOfWidgetInstances = manager.getGlanceIds(OverviewWidget::class.java).size
        }
    }


    /**
     * Saves the data and updates all widget instances.
     */
    fun save() = viewModelScope.launch(Dispatchers.IO) {
        preferences.edit {
            putFloat("widget_overview_opacity", widgetOpacity)
            putBoolean("widget_overview_isObfuscated", widgetIsObfuscated)
            putInt("widget_overview_clickAction", widgetClickAction)
        }

        //Update all widgets:
        val context: Context = (getApplication() as Application).applicationContext
        val overviewWidget = OverviewWidget()
        val manager = GlanceAppWidgetManager(context)

        manager.getGlanceIds(overviewWidget.javaClass).forEach { glanceId ->
            updateAppWidgetState(context, glanceId) {
                it[longPreferencesKey("now")] = System.currentTimeMillis()
            }
            overviewWidget.update(context, glanceId)
        }
    }


    /**
     * Requests the system to pin the widget instance in the launcher.
     * Once this is called, 'numberOfWidgetInstances' is set to 1 (regardless of the actual
     * number of instances), so that the box to pin the widget is removed from the UI.
     */
    fun requestPinWidget() = viewModelScope.launch {
        val manager = GlanceAppWidgetManager(application)

        val result: Boolean = manager.requestPinGlanceAppWidget(
            receiver = ChaChingWidgetReceiver::class.java,
            preview = OverviewWidget(),
            previewState = OverviewWidget.Tiles1x2
        )

        if (result) {
            numberOfWidgetInstances = 1 //Set this to 1 to remove the info message.
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCards.HELP_WIDGETS.setVisible(application, false)
        isHelpCardVisible = false
    }

}
