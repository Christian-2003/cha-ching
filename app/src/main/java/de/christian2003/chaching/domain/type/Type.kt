package de.christian2003.chaching.domain.type

import java.time.LocalDateTime
import java.util.UUID


/**
 * Domain entity models a type. Users can create transfers for a type which describes the transfer
 * in greater detail.
 */
class Type(

    /**
     * Name of the type which is shown to the user. The user can identify the type using this name.
     */
    var name: String,

    /**
     * Icon of the type which is shown to the user.
     */
    var icon: TypeIcon,

    /**
     * Unique ID of the type.
     */
    val id: UUID = UUID.randomUUID(),

    /**
     * Indicates whether the field "hours worked" should be tracked for transfers of this type.
     */
    var isHoursWorkedEditable: Boolean = true,

    /**
     * Indicates whether the type is available through the "+"-FAB on the main screen.
     */
    var isEnabledInQuickAccess: Boolean = true,

    /**
     * Stores the date time on which the type was created. This is for statistical purposes.
     */
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Stores the date time on which the type was edited. This is for statistical purposes.
     */
    var edited: LocalDateTime = LocalDateTime.now()

) {

    /**
     * Hash code for the type.
     *
     * @return  Hash code of the type ID.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Indicates whether the object passed is this type.
     *
     * @return  Whether the ID of the type passed is identical to the ID of this type.
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Type) {
            other.id == id
        } else {
            false
        }
    }

}
