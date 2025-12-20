package de.christian2003.chaching.plugin.infrastructure.db

import de.christian2003.chaching.application.backup.BackupImportRepository
import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.plugin.infrastructure.db.entities.TransferEntity
import de.christian2003.chaching.plugin.infrastructure.db.entities.TypeEntity
import de.christian2003.chaching.application.backup.ImportStrategy
import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.domain.type.DeletedType
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.db.entities.DeletedTypeEntity
import de.christian2003.chaching.plugin.infrastructure.db.mapper.DeletedTypeDbMapper
import de.christian2003.chaching.plugin.infrastructure.db.mapper.TransferDbMapper
import de.christian2003.chaching.plugin.infrastructure.db.mapper.TypeDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject


/**
 * Repository through which to access data.
 *
 * @param transferDao	DAO through which to access transfers.
 * @param typeDao		DAO through which to access types.
 */
class ChaChingRepository @Inject constructor(
	private val transferDao: TransferDao,
	private val typeDao: TypeDao,
	private val deletedTypeDao: DeletedTypeDao
): TransferRepository, TypeRepository, BackupImportRepository, AnalysisRepository {

	/**
	 * Imports the data passed as arguments based on the specified import strategy.
	 *
	 * @param transfers         List of transfers to import
	 * @param types             List of types to import
	 * @param importStrategy    Indicates what should happen to existing data during import.
	 */
	override suspend fun importFromBackup(transfers: List<Transfer>, types: List<Type>, importStrategy: ImportStrategy) {
		val transferEntities: MutableList<TransferEntity> = mutableListOf()
		val typeEntities: MutableList<TypeEntity> = mutableListOf()

		transfers.forEach { transfer ->
			transferEntities.add(transferMapper.toEntity(transfer))
		}
		types.forEach { type ->
			typeEntities.add(typeMapper.toEntity(type))
		}

		when (importStrategy) {
			ImportStrategy.DELETE_EXISTING_DATA -> {
				transferDao.deleteAll()
				typeDao.deleteAll()
				typeDao.insertAndIgnore(typeEntities)
				transferDao.insertAndIgnore(transferEntities)
			}
			ImportStrategy.REPLACE_EXISTING_DATA -> {
				typeDao.upsert(typeEntities)
				transferDao.upsert(transferEntities)
			}
			ImportStrategy.IGNORE_EXISTING_DATA -> {
				typeDao.insertAndIgnore(typeEntities)
				transferDao.insertAndIgnore(transferEntities)
			}
		}
	}


	/**
	 * Mapper used to map transfers from domain model to database entities.
	 */
	private val transferMapper: TransferDbMapper = TransferDbMapper()

	/**
	 * Mapper used to map types from domain model to database entities.
	 */
	private val typeMapper: TypeDbMapper = TypeDbMapper()

	private val deletedTypeMapper: DeletedTypeDbMapper = DeletedTypeDbMapper()


	/**
	 * List of all types.
	 */
	private var types: Flow<List<Type>>? = null

	/**
	 * List of all types in the trash bin.
	 */
	private var typesInTrash: Flow<List<DeletedType>>? = null

	/**
	 * List of all types not in the trash bin.
	 */
	private var typesNotInTrash: Flow<List<Type>>? = null

	/**
	 * List of all transfers.
	 */
	private var transfers: Flow<List<Transfer>>? = null


	/**
	 * Returns a flow containing list of all transfers.
	 *
	 * @return  List of all transfers.
	 */
	override fun getAllTransfers(): Flow<List<Transfer>> {
		if (transfers == null) {
			//WARNING: If this does not work, map type list every time the function is called!
			transfers = transferDao.selectAllTransfersSortedByDate().map { list ->
				list.map { transfer ->
					transferMapper.toDomain(transfer)
				}
			}
		}
		return transfers!!
	}


	/**
	 * Returns as list of all transfers with a value date within the range specified.
	 *
	 * @param start Start day of the range.
	 * @param end   End day of the range.
	 * @return      List of all transfers within the date range specified.
	 */
	override fun getAllTransfersInDateRange(start: LocalDate, end: LocalDate): Flow<List<Transfer>> {
		val transfers: Flow<List<TransferEntity>> = transferDao.selectTransfersWithValueDateRange(start.toEpochDay(), end.toEpochDay())
		return transfers.map { list ->
			list.map { transfer ->
				transferMapper.toDomain(transfer)
			}
		}
	}


	/**
	 * Returns a flow containing a list of the most recent transfers.
	 *
	 * @return  List of the most recent transfers.
	 */
	override fun getRecentTransfers(): Flow<List<Transfer>> {
		val transfers: Flow<List<TransferEntity>> = transferDao.selectRecentTransfers()
		return transfers.map { list ->
			list.map { transfer ->
				transferMapper.toDomain(transfer)
			}
		}
	}


	/**
	 * Returns the transfer of the ID passed as argument. If no transfer with the ID specified
	 * exists, null is returned.
	 *
	 * @param id    ID of the transfer to return.
	 * @return      Transfer with the ID specified.
	 */
	override suspend fun getTransferById(id: UUID): Transfer? {
		val transfer: TransferEntity? = transferDao.selectTransferById(id)
		return if (transfer != null) {
			transferMapper.toDomain(transfer)
		} else {
			null
		}
	}


	/**
	 * Creates a new transfer.
	 *
	 * @param transfer  New transfer to create.
	 */
	override suspend fun createNewTransfer(transfer: Transfer) {
		transferDao.insert(transferMapper.toEntity(transfer))
	}


	/**
	 * Updates an existing transfer with the data passed as argument. The transfer updated is
	 * determined based on the ID. This means that the ID of the transfer passed must be identical
	 * to the ID of the transfer to update.
	 *
	 * @param transfer  Data with which to update the existing transfer.
	 */
	override suspend fun updateExistingTransfer(transfer: Transfer) {
		transferDao.update(transferMapper.toEntity(transfer))
	}


	/**
	 * Deletes the transfer passed as argument.
	 *
	 * @param transfer  Transfer to delete.
	 */
	override suspend fun deleteTransfer(transfer: Transfer) {
		transferDao.delete(transferMapper.toEntity(transfer))
	}


	/**
	 * Returns a list of all types.
	 *
	 * @return  List of all types.
	 */
	override fun getAllTypes(): Flow<List<Type>> {
		if (types == null) {
			//WARNING: If this does not work, map type list every time the function is called!
			types = typeDao.selectAllTypesSortedByDate().map { list ->
				list.map { type ->
					typeMapper.toDomain(type)
				}
			}
		}
		return types!!
	}


	/**
	 * Returns a list of all types that are in the trash bin.
	 *
	 * @return  List of all types that are in the trash bin.
	 */
	override fun getAllTypesInTrash(): Flow<List<DeletedType>> {
		if (typesInTrash == null) {
			typesInTrash = deletedTypeDao.selectAll().map { list ->
				list.map { deletedTypeEntity ->
					val typeEntity: TypeEntity? = typeDao.selectTypeById(deletedTypeEntity.typeId)
					val typeDomain: Type = typeMapper.toDomain(typeEntity!!)
					val deletedTypeDomain: DeletedType = deletedTypeMapper.toDomain(deletedTypeEntity, typeDomain)
					return@map deletedTypeDomain
				}
			}
		}
		return typesInTrash!!
	}


	/**
	 * Returns a list of all types that are NOT in the trash bin.
	 *
	 * @return  List of all types that are not in the trash bin.
	 */
	override fun getAllTypesNotInTrash(): Flow<List<Type>> {
		if (typesNotInTrash == null) {
			typesNotInTrash = typeDao.selectAllTypesNotInTrashSortedByDate().map { list ->
				list.map { type ->
					typeMapper.toDomain(type)
				}
			}
		}
		return typesNotInTrash!!
	}


	/**
	 * Returns the type with the ID specified. If no type of the specified ID exists, null is
	 * returned.
	 *
	 * @param id    ID of the type to return.
	 * @return      Type of the ID specified.
	 */
	override suspend fun getTypeById(id: UUID): Type? {
		val type: TypeEntity? = typeDao.selectTypeById(id)
		return if (type != null) {
			typeMapper.toDomain(type)
		} else {
			null
		}
	}

	override suspend fun getDeletedTypeById(id: UUID): DeletedType? {
		val deletedTypeEntity: DeletedTypeEntity? = deletedTypeDao.selectById(id)
		if (deletedTypeEntity != null) {
			val typeEntity: TypeEntity? = typeDao.selectTypeById(id)
			if (typeEntity != null) {
				val type: Type = typeMapper.toDomain(typeEntity)
				val deletedType: DeletedType = deletedTypeMapper.toDomain(deletedTypeEntity, type)
				return deletedType
			}
		}
		return null
	}


	/**
	 * Creates a new type.
	 *
	 * @param type  Type to create.
	 */
	override suspend fun createNewType(type: Type) {
		typeDao.insert(typeMapper.toEntity(type))
	}


	/**
	 * Updates the existing type with the data passed as argument. The existing type which is
	 * updated with the new type is determined by the ID. This means that the ID of the type passed
	 * must be identical to the ID of the type to update.
	 *
	 * @param type  Data with which to update the existing type.
	 */
	override suspend fun updateExistingType(type: Type) {
		typeDao.update(typeMapper.toEntity(type))
	}


	/**
	 * Moves the specified type to the trash bin.
	 *
	 * @param type  Type to move to the trash bin.
	 */
	override suspend fun moveTypeToTrash(type: Type) {
		deletedTypeDao.insert(DeletedTypeEntity(type.id))
	}


	/**
	 * Restores the specified type from the trash bin.
	 *
	 * @param deletedType	Type to restore from the trash bin.
	 */
	override suspend fun restoreTypeFromTrash(deletedType: DeletedType) {
		deletedTypeDao.delete(deletedTypeMapper.toEntity(deletedType))
	}


	/**
	 * Deletes the type which is passed as argument.
	 *
	 * @param type  Type to delete.
	 */
	override suspend fun deleteType(type: Type) {
		typeDao.delete(typeMapper.toEntity(type))
	}

}
