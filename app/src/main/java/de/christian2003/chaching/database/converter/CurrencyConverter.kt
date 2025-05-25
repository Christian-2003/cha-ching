package de.christian2003.chaching.database.converter

import androidx.room.TypeConverter
import de.christian2003.chaching.model.transfers.Currency


/**
 * Converter for room database to convert a currency field into an ordinal and vice versa.
 */
class CurrencyConverter {

	/**
	 * Converts the currency passed to an ordinal.
	 *
	 * @param currency	Currency to convert into an ordinal.
	 * @return			Ordinal.
	 */
	@TypeConverter
	fun fromCurrency(currency: Currency): Int {
		return currency.ordinal
	}


	/**
	 * Converts the ordinal to a currency.
	 *
	 * @param ordinal	Ordinal to convert into a currency.
	 * @return			Currency.
	 */
	@TypeConverter
	fun toCurrency(ordinal: Int): Currency {
		return try {
			Currency.entries[ordinal]
		} catch (e: Exception) {
			Currency.USD
		}
	}

}
