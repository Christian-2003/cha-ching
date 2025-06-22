package de.christian2003.chaching.model.backup

import de.christian2003.chaching.plugin.db.entities.TypeEntity
import de.christian2003.chaching.model.backup.serializer.LocalDateTimeSerializer
import de.christian2003.chaching.model.backup.serializer.UuidSerializer
import de.christian2003.chaching.domain.type.TypeIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID


@Serializable
class SerializableTypeDto(

    @SerialName("name")
    val name: String = "Unnamed",

    @SerialName("icon")
    val icon: TypeIcon = TypeIcon.CURRENCY,

    @SerialName("isHoursWorkedEditable")
    val isHoursWorkedEditable: Boolean = true,

    @SerialName("id")
    @Serializable(with = UuidSerializer::class)
    val id: UUID,

    @SerialName("created")
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime = LocalDateTime.now(),

    @SerialName("edited")
    @Serializable(with = LocalDateTimeSerializer::class)
    val edited: LocalDateTime = LocalDateTime.now()

) {

    fun toDatabaseEntity(): TypeEntity {
        return TypeEntity(
            name = name,
            icon = icon,
            isHoursWorkedEditable = isHoursWorkedEditable,
            typeId = id,
            created = created,
            edited = edited
        )
    }


    companion object {

        fun fromDatabaseEntity(typeEntity: TypeEntity): SerializableTypeDto {
            return SerializableTypeDto(
                name = typeEntity.name,
                icon = typeEntity.icon,
                isHoursWorkedEditable = typeEntity.isHoursWorkedEditable,
                id = typeEntity.typeId,
                created = typeEntity.created,
                edited = typeEntity.edited
            )
        }

    }

}
