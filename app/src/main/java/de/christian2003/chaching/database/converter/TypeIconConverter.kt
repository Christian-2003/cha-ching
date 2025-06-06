package de.christian2003.chaching.database.converter

import androidx.room.TypeConverter
import de.christian2003.chaching.model.transfers.TypeIcon


/**
 * Converter for room database to convert a type icon field into an ordinal and vice versa.
 */
class TypeIconConverter {

    /**
     * Converts the type icon passed to an ordinal.
     *
     * @param typeIcon	Type icon to convert into an ordinal.
     * @return			Ordinal.
     */
    @TypeConverter
    fun fromTypeIcon(typeIcon: TypeIcon): Int {
        return typeIcon.ordinal
    }


    /**
     * Converts the ordinal to a type icon.
     *
     * @param ordinal	Ordinal to convert into a type icon.
     * @return			Type icon.
     */
    @TypeConverter
    fun toTypeIcon(ordinal: Int): TypeIcon {
        return try {
            TypeIcon.entries[ordinal]
        } catch (e: Exception) {
            TypeIcon.CURRENCY
        }
    }

}
