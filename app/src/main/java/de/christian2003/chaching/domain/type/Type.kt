package de.christian2003.chaching.domain.type

import java.time.LocalDateTime
import java.util.UUID


/**
 * Domain entity models a type. Users can create transfers for a type which describes the transfer
 * in greater detail.
 *
 * @param name      Name of the type which is shown to the user. The user can identify the type using
 *                  this name.
 * @param icon      Icon of the type which is shown to the user.
 * @param id        Unique ID of the type.
 * @param metadata  Metadata for the type.
 */
class Type(
    name: String,
    icon: TypeIcon,
    val id: UUID = UUID.randomUUID(),
    var metadata: TypeMetadata = TypeMetadata()
) {

    /**
     * Name of the type which is shown to the user. The user can identify the type using this name.
     */
    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "Name cannot be blank" }
            field = value
            metadata = metadata.copy(edited = LocalDateTime.now())
        }

    /**
     * Icon of the type which is shown to the user.
     */
    var icon: TypeIcon = icon
        set(value) {
            field = value
            metadata = metadata.copy(edited = LocalDateTime.now())
        }


    /**
     * Initializes the type.
     */
    init {
        this.name = name
        this.icon = icon
    }


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
        return (other is Type) && (other.id == this.id)
    }

}
