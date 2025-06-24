package de.christian2003.chaching.plugin.infrastructure.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.christian2003.chaching.domain.type.TypeIcon
import java.time.LocalDateTime
import java.util.UUID


/**
 * Database entity for storing types.
 */
@Entity(tableName = "types")
class TypeEntity(

    /**
     * Display name of the type.
     */
    var name: String,

    /**
     * Icon of the type.
     */
    var icon: TypeIcon,

    /**
     * UUID of the type.
     */
    @PrimaryKey
    val typeId: UUID = UUID.randomUUID(),

    /**
     * Whether the hoursWorked-field of the transfers for this type can be edited.
     */
    var isHoursWorkedEditable: Boolean = true,


    /**
     * Date time on which the type was created. This is for statistical purposes.
     */
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date time on which the type was last edited. This is for statistical purposes.
     */
    var edited: LocalDateTime = LocalDateTime.now()

) {

    override fun hashCode(): Int {
        return typeId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is TypeEntity) {
            return other.typeId == typeId
        }
        return false
    }

}
