package de.christian2003.chaching.plugin.infrastructure.backup

import de.christian2003.chaching.application.backup.BackupImportRepository
import de.christian2003.chaching.application.backup.BackupService
import de.christian2003.chaching.application.backup.ImportStrategy
import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TransferDto
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TypeDto
import de.christian2003.chaching.plugin.infrastructure.backup.mapper.TransferBackupMapper
import de.christian2003.chaching.plugin.infrastructure.backup.mapper.TypeBackupMapper
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject


class JsonBackupService @Inject constructor(
    private val transferRepository: TransferRepository,
    private val typeRepository: TypeRepository,
    private val importRepository: BackupImportRepository
): BackupService {

    private val transferMapper: TransferBackupMapper = TransferBackupMapper()

    private val typeMapper: TypeBackupMapper = TypeBackupMapper()



    /**
     * Serializes the app data into a string. If the data cannot be serialized, null is returned.
     * The serialized data can then be (for example) written into a file.
     *
     * @return  Serialized app data.
     */
    override suspend fun serialize(): String? {
        val transfers: List<Transfer> = transferRepository.getAllTransfers().first()
        val types: List<Type> = typeRepository.getAllTypes().first()
        val transferDtos: MutableList<TransferDto> = mutableListOf()
        val typeDtos: MutableList<TypeDto> = mutableListOf()

        transfers.forEach { transfer ->
            transferDtos.add(transferMapper.toDto(transfer))
        }
        types.forEach { type ->
            typeDtos.add(typeMapper.toDto(type))
        }

        val backupData = BackupData(
            metadata = BackupMetadata(),
            transfers = transferDtos,
            types = typeDtos
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

        backupData.transfers.forEach { transfer ->
            transfers.add(transferMapper.toDomain(transfer))
        }
        backupData.types.forEach { type ->
            types.add(typeMapper.toDomain(type))
        }

        try {
            importRepository.importFromBackup(transfers, types, importStrategy)
            return true
        }
        catch (_: Exception) {
            return false
        }
    }

}
