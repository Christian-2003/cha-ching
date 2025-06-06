package de.christian2003.chaching.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.christian2003.chaching.model.transfers.TypeIcon
import java.time.LocalDateTime
import java.util.UUID


/**
 * Database entity for storing types.
 */
@Entity(tableName = "types")
class Type(

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

)
