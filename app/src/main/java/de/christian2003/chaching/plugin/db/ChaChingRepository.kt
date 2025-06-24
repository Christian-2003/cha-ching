package de.christian2003.chaching.plugin.db

import de.christian2003.chaching.application.backup.BackupImportRepository
import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.plugin.db.entities.TransferEntity
import de.christian2003.chaching.plugin.db.entities.TransferWithTypeEntity
import de.christian2003.chaching.plugin.db.entities.TypeEntity
import de.christian2003.chaching.application.backup.ImportStrategy
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.db.mapper.TransferDbMapper
import de.christian2003.chaching.plugin.db.mapper.TypeDbMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID


/**
 * Repository through which to access data.
 */
class ChaChingRepository(

	/**
	 * DAO through which to access transfers.
	 */
	private val transferDao: TransferDao,

	/**
	 * DAO through which to access types.
	 */
	private val typeDao: TypeDao

): TransferRepository, TypeRepository, BackupImportRepository {

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
				typeDao.insertAndReplace(typeEntities)
				transferDao.insertAndReplace(transferEntities)
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


	/**
	 * List of all types.
	 */
	private var types: Flow<List<Type>>? = null

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
	 * Returns the transfer of the ID passed as argument. If no transfer with the ID specified
	 * exists, null is returned.
	 *
	 * @param id    ID of the transfer to return.
	 * @return      Transfer with the ID specified.
	 */
	override suspend fun getTransferById(id: UUID): Transfer? {
		val transfer: TransferWithTypeEntity? = transferDao.selectTransferWithTypeById(id)
		return if (transfer != null) {
			transferMapper.toDomain(transfer.transfer)
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
	 * Deletes the type which is passed as argument.
	 *
	 * @param type  Type to delete.
	 */
	override suspend fun deleteType(type: Type) {
		typeDao.delete(typeMapper.toEntity(type))
	}

}
