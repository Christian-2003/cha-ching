package de.christian2003.chaching.plugin.infrastructure.backup

import android.util.Log
import de.christian2003.chaching.application.backup.BackupImportRepository
import de.christian2003.chaching.application.backup.BackupService
import de.christian2003.chaching.application.backup.ImportStrategy
import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.DeletedType
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.backup.dto.DeletedTypeDto
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TransferDto
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TypeDto
import de.christian2003.chaching.plugin.infrastructure.backup.mapper.DeletedTypeBackupMapper
import de.christian2003.chaching.plugin.infrastructure.backup.mapper.TransferBackupMapper
import de.christian2003.chaching.plugin.infrastructure.backup.mapper.TypeBackupMapper
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject


/**
 * Service handles JSON backups.
 *
 * @param transferRepository    Repository for transfers.
 * @param typeRepository        Repository for types.
 * @param importRepository      Repository for importing data.
 */
class JsonBackupService @Inject constructor(
    private val transferRepository: TransferRepository,
    private val typeRepository: TypeRepository,
    private val importRepository: BackupImportRepository
): BackupService {

    /**
     * Mapper for transfers.
     */
    private val transferMapper: TransferBackupMapper = TransferBackupMapper()

    /**
     * Mapper for types.
     */
    private val typeMapper: TypeBackupMapper = TypeBackupMapper()

    /**
     * Mapper for deleted types.
     */
    private val deletedTypeMapper: DeletedTypeBackupMapper = DeletedTypeBackupMapper()



    /**
     * Serializes the app data into a string. If the data cannot be serialized, null is returned.
     * The serialized data can then be (for example) written into a file.
     *
     * @return  Serialized app data.
     */
    override suspend fun serialize(): String? {
        val transfers: List<Transfer> = transferRepository.getAllTransfers().first()
        val types: List<Type> = typeRepository.getAllTypes().first()
        val deletedTypes: List<DeletedType> = typeRepository.getAllTypesInTrash().first()
        val transferDtos: MutableList<TransferDto> = mutableListOf()
        val typeDtos: MutableList<TypeDto> = mutableListOf()
        val deletedTypeDtos: MutableList<DeletedTypeDto> = mutableListOf()

        transfers.forEach { transfer ->
            transferDtos.add(transferMapper.toDto(transfer))
        }
        types.forEach { type ->
            typeDtos.add(typeMapper.toDto(type))
        }
        deletedTypes.forEach { deletedType ->
            deletedTypeDtos.add(deletedTypeMapper.toDto(deletedType))
        }

        val backupData = BackupData(
            metadata = BackupMetadata(),
            transfers = transferDtos,
            types = typeDtos,
            deletedTypes = deletedTypeDtos
        )

        return try {
            Json.encodeToString(backupData)
        } catch (_: Exception) {
            null
        }
    }


    /**
     * Deserializes the data passed and stores the data within the app repository according to the
     * import strategy passed as argument.
     *
     * @param serializedData    Data that should be deserialized and stored within the app.
     * @param importStrategy    Indicates what should happen with the data currently stored within
     *                          the app.
     * @return                  Whether deserialization and import are successful.
     */
    override suspend fun deserialize(serializedData: String, importStrategy: ImportStrategy): Boolean {
        val backupData: BackupData? = try {
            Json.decodeFromString<BackupData>(serializedData)
        } catch (_: Exception) {
            null
        }
        if (backupData == null) {
            return false
        }

        val transfers: MutableList<Transfer> = mutableListOf()
        val types: MutableList<Type> = mutableListOf()
        val deletedTypes: MutableList<DeletedType> = mutableListOf()

        backupData.transfers.forEach { transfer ->
            transfers.add(transferMapper.toDomain(transfer))
        }
        backupData.types.forEach { type ->
            types.add(typeMapper.toDomain(type))
        }
        backupData.deletedTypes.forEach { deletedType ->
            val type: Type? = types.find { it.id == deletedType.typeId }
            if (type != null) {
                deletedTypes.add(deletedTypeMapper.toDomain(deletedType, type))
            }
        }

        try {
            importRepository.importFromBackup(
                transfers = transfers,
                types = types,
                deletedTypes = deletedTypes,
                importStrategy = importStrategy
            )
            return true
        }
        catch (_: Exception) {
            return false
        }
    }

}
