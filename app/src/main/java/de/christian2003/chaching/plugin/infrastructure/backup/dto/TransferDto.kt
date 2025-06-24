package de.christian2003.chaching.plugin.infrastructure.backup.dto

import de.christian2003.chaching.plugin.infrastructure.backup.serializer.LocalDateSerializer
import de.christian2003.chaching.plugin.infrastructure.backup.serializer.LocalDateTimeSerializer
import de.christian2003.chaching.plugin.infrastructure.backup.serializer.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


@Serializable
data class TransferDto (

    @SerialName("value")
    val value: Int = 0,

    @SerialName("hoursWorked")
    val hoursWorked: Int = 0,

    @SerialName("isSalary")
    val isSalary: Boolean = true,

    @SerialName("valueDate")
    @Serializable(with = LocalDateSerializer::class)
    val valueDate: LocalDate = LocalDate.now(),

    @SerialName("type")
    @Serializable(with = UuidSerializer::class)
    val type: UUID,

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
