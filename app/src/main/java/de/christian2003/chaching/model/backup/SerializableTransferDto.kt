package de.christian2003.chaching.model.backup

import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.model.backup.serializer.LocalDateSerializer
import de.christian2003.chaching.model.backup.serializer.LocalDateTimeSerializer
import de.christian2003.chaching.model.backup.serializer.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


@Serializable
class SerializableTransferDto (

    @SerialName("value")
    val value: Int,

    @SerialName("hoursWorked")
    val hoursWorked: Int,

    @SerialName("isSalary")
    val isSalary: Boolean,

    @SerialName("valueDate")
    @Serializable(with = LocalDateSerializer::class)
    val valueDate: LocalDate,

    @SerialName("type")
    @Serializable(with = UuidSerializer::class)
    val type: UUID,

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

    fun toDatabaseEntity(): Transfer {
        return Transfer(
            value = value,
            hoursWorked = hoursWorked,
            isSalary = isSalary,
            valueDate = valueDate,
            type = type,
            transferId = id,
            created = created,
            edited = edited
        )
    }


    companion object {

        fun fromDatabaseEntity(transfer: Transfer): SerializableTransferDto {
            return SerializableTransferDto(
                value = transfer.value,
                hoursWorked = transfer.hoursWorked,
                isSalary = transfer.isSalary,
                valueDate = transfer.valueDate,
                type = transfer.type,
                id = transfer.transferId,
                created = transfer.created,
                edited = transfer.edited
            )
        }

    }

}
