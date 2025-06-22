package de.christian2003.chaching.plugin.db.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Converter for room database to convert LocalDateTime instances into epoch seconds and vice versa.
 */
class LocalDateTimeConverter {

	/**
	 * Converts the local date time passed to epoch seconds.
	 *
	 * @param localDateTime	Local date time to convert to epoch seconds.
	 * @return				Epoch seconds.
 	 */
	@TypeConverter
	fun fromLocalDateTime(localDateTime: LocalDateTime): Long {
		return localDateTime.toEpochSecond(ZoneOffset.UTC)
	}


	/**
	 * Converts the epoch seconds passed to a local date time instance.
	 *
	 * @param epochSecond	Epoch seconds to convert to a local date time.
	 * @return				Local date time.
	 */
	@TypeConverter
	fun toLocalDateTime(epochSecond: Long): LocalDateTime {
		return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
	}

}
