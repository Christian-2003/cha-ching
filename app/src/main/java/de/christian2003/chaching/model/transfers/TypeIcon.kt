package de.christian2003.chaching.model.transfers

import androidx.annotation.DrawableRes
import de.christian2003.chaching.R


/**
 * Stores all icons that can be assigned to types.
 */
enum class TypeIcon(

    /**
     * Resource ID of the drawable.
     */
    @DrawableRes
    val drawableResourceId: Int

) {

    CURRENCY(R.drawable.type_currency),

    COIN(R.drawable.type_coin),

    BANK(R.drawable.type_bank),

    CREDIT(R.drawable.type_credit),

    NOTE(R.drawable.type_note)

}
