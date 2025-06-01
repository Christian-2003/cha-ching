package de.christian2003.chaching.model.backup

import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class SerializableAppData(

    @SerialName("metadata")
    val metadata: SerializableMetadata,

    @SerialName("types")
    val types: List<SerializableTypeDto>,

    @SerialName("transfers")
    val transfers: List<SerializableTransferDto>

) {

    fun toTypes(): List<Type> {
        val typesList = mutableListOf<Type>()
        types.forEach { type ->
            typesList.add(type.toDatabaseEntity())
        }
        return typesList.toList()
    }


    fun toTransfers(): List<Transfer> {
        val transfersList = mutableListOf<Transfer>()
        transfers.forEach { transfer ->
            transfersList.add(transfer.toDatabaseEntity())
        }
        return transfersList
    }


    companion object {

        fun toSerializableAppData(types: List<Type>, transfers: List<Transfer>): SerializableAppData {
            val typesList = mutableListOf<SerializableTypeDto>()
            val transfersList = mutableListOf<SerializableTransferDto>()

            types.forEach { type ->
                typesList.add(SerializableTypeDto.fromDatabaseEntity(type))
            }
            transfers.forEach { transfer ->
                transfersList.add(SerializableTransferDto.fromDatabaseEntity(transfer))
            }

            return SerializableAppData(
                metadata = SerializableMetadata(),
                types = typesList.toList(),
                transfers = transfersList.toList()
            )
        }

    }

}
