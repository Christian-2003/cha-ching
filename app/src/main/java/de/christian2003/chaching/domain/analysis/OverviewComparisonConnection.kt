package de.christian2003.chaching.domain.analysis

import android.content.Context
import android.icu.text.DecimalFormat
import de.christian2003.chaching.R


/**
 * Enum lists some household or everyday items, each containing a price. The MainScreen displays
 * to the user, how many of a random item the user can afford with their current income.
 */
enum class OverviewComparisonConnection(

    /**
     * Price for an item of the type.
     */
    private val price: Int

) {

    COFFEE(400),

    ICE_CREAM(250),

    BIG_MAC(550),

    PIZZA(1200),

    CHOCOLATE(199),

    AVOCADO(150),

    TOILET_PAPIER(60),

    MILK(179),

    BREAD(350),

    EGGS(45),

    SPOTIFY(1199),

    MOVIE_TICKET(1500),

    GERMANY_TICKET(5800),

    RAMEN(1500),

    TOOTHPASTE(200),

    APPLE(60),

    LAUNDRY_DETERGENT(650),

    SODA(180),

    BATTERIES(600);


    /**
     * Returns a localized string that can be displayed to the user.
     *
     * @param context   Context from which to get the string.
     * @param value     Value used to format.
     * @return          Localized and formatted string.
     */
    fun getLocalizedString(context: Context, value: Int): String {
        val amount = value / price
        val valueFormat = DecimalFormat("#,###")
        return context.resources.getStringArray(R.array.main_comparisons)[this.ordinal].format(valueFormat.format(amount))
    }


    companion object {

        /**
         * Gets a random field of this enum.
         *
         * @return  Random field.
         */
        fun getRandomComparisonConnection(): OverviewComparisonConnection {
            val ordinal = (0..entries.size - 1).random()
            return entries[ordinal]
        }

    }

}
