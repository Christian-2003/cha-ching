package de.christian2003.chaching.model.backup

import de.christian2003.chaching.plugin.db.entities.TransferEntity
import de.christian2003.chaching.plugin.db.entities.TypeEntity
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

    fun toTypes(): List<TypeEntity> {
        val typesList = mutableListOf<TypeEntity>()
        types.forEach { type ->
            typesList.add(type.toDatabaseEntity())
        }
        return typesList.toList()
    }


    fun toTransfers(): List<TransferEntity> {
        val transfersList = mutableListOf<TransferEntity>()
        transfers.forEach { transfer ->
            transfersList.add(transfer.toDatabaseEntity())
        }
        return transfersList
    }


    companion object {

        fun toSerializableAppData(typeEntities: List<TypeEntity>, transfers: List<TransferEntity>): SerializableAppData {
            val typesList = mutableListOf<SerializableTypeDto>()
            val transfersList = mutableListOf<SerializableTransferDto>()

            typeEntities.forEach { type ->
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
