package de.christian2003.chaching.model.backup

import de.christian2003.chaching.database.entities.Type
import de.christian2003.chaching.model.backup.serializer.LocalDateTimeSerializer
import de.christian2003.chaching.model.backup.serializer.UuidSerializer
import de.christian2003.chaching.model.transfers.TypeIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID


@Serializable
class SerializableTypeDto(

    @SerialName("name")
    val name: String,

    @SerialName("icon")
    val icon: TypeIcon,

    @SerialName("id")
    @Serializable(with = UuidSerializer::class)
    val id: UUID,

    @SerialName("created")
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime,

    @SerialName("edited")
    @Serializable(with = LocalDateTimeSerializer::class)
    val edited: LocalDateTime

) {

    fun toDatabaseEntity(): Type {
        return Type(
            name = name,
            icon = icon,
            typeId = id,
            created = created,
            edited = edited
        )
    }


    companion object {

        fun fromDatabaseEntity(type: Type): SerializableTypeDto {
            return SerializableTypeDto(
                name = type.name,
                icon = type.icon,
                id = type.typeId,
                created = type.created,
                edited = type.edited
            )
        }

    }

}
