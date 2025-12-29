package de.christian2003.chaching.plugin.presentation.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.edit
import de.christian2003.chaching.R

/**
 * Fields store whether a help card is visible.
 */
enum class HelpCards(

    /**
     * Key with which the enum field stores in shared preferences whether the help card is visible.
     */
    private val sharedPreferencesKey: String,

    /**
     * ID of the string resource containing a short name for the help. This is NOT the help text
     * to display to the user. Instead, this string is shown to the user on the page which displays
     * all help texts, and which of those are enabled / disabled.
     */
    @StringRes
    val shortNameStringRes: Int

) {

    CREATE_TYPE("help_type", R.string.help_createType),

    TYPES_LIST("help_typesList", R.string.help_typesList),

    CREATE_TRANSFER("help_transfer", R.string.help_createTransfer),

    HELP_LIST("help_list", R.string.help_helpList),

    HELP_TRASH("help_trash", R.string.help_trash);


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