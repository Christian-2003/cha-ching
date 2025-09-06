package de.christian2003.chaching.plugin.infrastructure.backup.dto

import de.christian2003.chaching.plugin.infrastructure.serializer.LocalDateTimeSerializer
import de.christian2003.chaching.plugin.infrastructure.serializer.UuidSerializer
import de.christian2003.chaching.domain.type.TypeIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID


@Serializable
data class TypeDto(

    @SerialName("name")
    val name: String = "Unnamed",

    @SerialName("icon")
    val icon: TypeIcon = TypeIcon.CURRENCY,

    @SerialName("isHoursWorkedEditable")
    val isHoursWorkedEditable: Boolean = true,

    @SerialName("isEnabledInQuickAccess")
    val isEnabledInQuickAccess: Boolean = true,

    @SerialName("id")
    @Serializable(with = UuidSerializer::class)
    val id: UUID,

    @SerialName("created")
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime = LocalDateTime.now(),

    @SerialName("edited")
    @Serializable(with = LocalDateTimeSerializer::class)
    val edited: LocalDateTime = LocalDateTime.now()

)
