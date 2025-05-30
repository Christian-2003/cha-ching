package de.christian2003.chaching.model.help

import android.content.Context
import androidx.core.content.edit


/**
 * Fields store whether a help card is visible.
 */
enum class HelpCards(

    /**
     * Key with which the enum field stores in shared preferences whether the help card is visible.
     */
    private val sharedPreferencesKey: String

) {

    CREATE_TYPE("help_type"),

    TYPES_LIST("help_typesList");


    /**
     * Returns whether the help card is visible.
     *
     * @param context   Context for shared preferences.
     * @return          Whether help card is visible.
     */
    fun getVisible(context: Context): Boolean {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(sharedPreferencesKey, true)
    }

    /**
     * Changes whether the help card is visible.
     *
     * @param context   Context for shared preferences.
     * @param isVisible Whether the help card should be visible.
     */
    fun setVisible(context: Context, isVisible: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
            putBoolean(sharedPreferencesKey, isVisible)
        }
    }

}
