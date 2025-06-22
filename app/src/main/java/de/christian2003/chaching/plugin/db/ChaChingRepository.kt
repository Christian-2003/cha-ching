package de.christian2003.chaching.plugin.db

import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.plugin.db.entities.TransferEntity
import de.christian2003.chaching.plugin.db.entities.TransferWithTypeEntity
import de.christian2003.chaching.plugin.db.entities.TypeEntity
import de.christian2003.chaching.model.backup.ImportStrategy
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

): TransferRepository, TypeRepository {

	/**
	 * List of all transfers (with type) sorted by value date.
	 */
	val allTransfersDeprecated = transferDao.selectAllTransfersWithTypeSortedByDate()

	val recentTransfers = transferDao.selectRecentTransfersWithType()

	/**
	 * List of all types sorted by date of creation.
	 */
	val allTypesDeprecated = typeDao.selectAllTypesSortedByDate()



	/**
	 * Returns the transfer (with type) of the ID specified. If no transfer with the ID specified
	 * exists, null is returned.
	 *
	 * @return	Transfer with type or null.
	 */
	suspend fun selectTransferWithTypeById(transferId: UUID): TransferWithTypeEntity? {
		return transferDao.selectTransferWithTypeById(transferId)
	}

	/**
	 * Returns all transfers for the last thirty days.
	 *
	 * @param date	Current date (i.e. today).
	 * @return		List of all transfers for the last 30 days.
	 */
    fun selectTransfersForMonth(date: LocalDate): Flow<List<TransferWithTypeEntity>> {
		val today: Long = date.toEpochDay()
		val thirtyDaysAgo: Long = date.minusDays(30).toEpochDay()
		return transferDao.selectTransfersWithValueDateRange(thirtyDaysAgo, today)
	}

	/**
	 * Inserts a new transfer into the database.
	 *
	 * @param transfer	New transfer to insert.
	 */
	suspend fun insertTransfer(transfer: TransferEntity) {
		transferDao.insert(transfer)
	}

	/**
	 * Deletes the transfer passed from the database.
	 *
	 * @param transfer	Transfer to delete.
	 */
	suspend fun deleteTransfer(transfer: TransferEntity) {
		transferDao.delete(transfer)
	}

	/**
	 * Updates an existing transfer within the database with the transfer passed.
	 *
	 * @param transfer	Transfer to update.
	 */
	suspend fun updateTransfer(transfer: TransferEntity) {
		transferDao.update(transfer)
	}


	/**
	 * Returns the type with the ID passed. If no type with the ID specified exists, null is
	 * returned.
	 *
	 * @return	Type of the ID specified or null.
	 */
	suspend fun selectTypeById(typeId: UUID): TypeEntity? {
		return typeDao.selectTypeById(typeId)
	}

	/**
	 * Inserts a new type into the database.
	 *
	 * @param typeEntity    New type to insert.
	 */
	suspend fun insertType(typeEntity: TypeEntity) {
		typeDao.insert(typeEntity)
	}

	/**
	 * Deletes the type passed from the database.
	 *
	 * @param typeEntity    Type to delete.
	 */
	suspend fun deleteType(typeEntity: TypeEntity) {
		typeDao.delete(typeEntity)
	}

	/**
	 * Updates an existing type from the database with the type passed.
	 *
	 * @param typeEntity    Type to update.
	 */
	suspend fun updateType(typeEntity: TypeEntity) {
		typeDao.update(typeEntity)
	}


	/**
	 * Imports the types and transfers from the backup according to the import strategy.
	 *
	 * @param typeEntities                Types to import.
	 * @param transfers			Transfers to import
	 * @param importStrategy	Import strategy indicates how to handle existing data with the import.
	 */
	suspend fun importDataFromBackup(typeEntities: List<TypeEntity>, transfers: List<TransferEntity>, importStrategy: ImportStrategy) {
		when (importStrategy) {
			ImportStrategy.DELETE_EXISTING_DATA -> {
				transferDao.deleteAll()
				typeDao.deleteAll()
				typeDao.insertAndIgnore(typeEntities)
				transferDao.insertAndIgnore(transfers)
			}
			ImportStrategy.REPLACE_EXISTING_DATA -> {
				typeDao.insertAndReplace(typeEntities)
				transferDao.insertAndReplace(transfers)
			}
			ImportStrategy.IGNORE_EXISTING_DATA -> {
				typeDao.insertAndIgnore(typeEntities)
				transferDao.insertAndIgnore(transfers)
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
		TODO("Not yet implemented")
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
