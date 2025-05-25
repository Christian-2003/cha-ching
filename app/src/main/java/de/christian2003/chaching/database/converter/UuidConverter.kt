package de.christian2003.chaching.database.converter

import androidx.room.TypeConverter
import java.util.UUID


/**
 * Converter for room database to convert UUID instances into a string representation and vice
 * versa.
 */
class UuidConverter {

	/**
	 * Converts the UUID passed into a string representation.
	 *
	 * @param uuid	UUID to convert into a string.
	 * @return		String representation.
	 */
	@TypeConverter
	fun fromUuid(uuid: UUID): String {
		return uuid.toString()
	}


	/**
	 * Converts the string passed into a UUID.
	 *
	 * @param uuid	String to convert into a UUID.
	 * @return		UUID.
	 */
	@TypeConverter
	fun toUuid(uuid: String): UUID {
		return UUID.fromString(uuid)
	}

}
