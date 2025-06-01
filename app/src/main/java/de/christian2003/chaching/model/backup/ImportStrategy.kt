package de.christian2003.chaching.model.backup

import androidx.annotation.StringRes
import de.christian2003.chaching.R


enum class ImportStrategy(

    @StringRes
    val titleStringRes: Int,

    @StringRes
    val infoStringRes: Int

) {

    DELETE_EXISTING_DATA(R.string.settings_data_importDialog_deleteTitle, R.string.settings_data_importDialog_deleteInfo),

    REPLACE_EXISTING_DATA(R.string.settings_data_importDialog_replaceTitle, R.string.settings_data_importDialog_replaceInfo),

    IGNORE_EXISTING_DATA(R.string.settings_data_importDialog_ignoreTitle, R.string.settings_data_importDialog_ignoreInfo)

}
