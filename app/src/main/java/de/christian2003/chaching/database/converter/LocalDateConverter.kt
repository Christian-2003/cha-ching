package de.christian2003.chaching.database.converter

import androidx.room.TypeConverter
import java.time.LocalDate


/**
 * Converter for room database to convert LocalDate instances into an epoch day and vice versa.
 */
class LocalDateConverter {

	/**
	 * Converts the local date passed into an epoch day.
	 *
	 * @param localDate	Local date to convert into an epoch day.
	 * @return			Epoch day.
	 */
	@TypeConverter
	fun fromLocalDate(localDate: LocalDate): Long {
		return localDate.toEpochDay()
	}


	/**
	 * Converts the epoch day passed into a local date instance.
	 *
	 * @param epochDay	Epoch day to convert into a local date.
	 * @return			Local date.
	 */
	@TypeConverter
	fun toLocalDate(epochDay: Long): LocalDate {
		return LocalDate.ofEpochDay(epochDay)
	}

}
