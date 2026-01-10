package de.christian2003.chaching.plugin.infrastructure.backup.dto

import de.christian2003.chaching.plugin.infrastructure.serializer.LocalDateTimeSerializer
import de.christian2003.chaching.plugin.infrastructure.serializer.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO for the JSON backup that models a deleted type.
 */
@Serializable
data class DeletedTypeDto(

    /**
     * ID of the deleted type.
     */
    @SerialName("typeId")
    @Serializable(with = UuidSerializer::class)
    val typeId: UUID,

    /**
     * Timestamp at which the type was deleted.
     */
    @SerialName("deletedAt")
    @Serializable(with = LocalDateTimeSerializer::class)
    val deletedAt: LocalDateTime

)
