package de.christian2003.chaching.domain.type

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

    NOTE(R.drawable.type_note),

    EDUCATION(R.drawable.type_education),

    FAMILY(R.drawable.type_family),

    HOME(R.drawable.type_home),

    SHARES(R.drawable.type_shares),

    VACATION(R.drawable.type_vacation)

}
