package de.christian2003.chaching.database

import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.TransferWithType
import de.christian2003.chaching.database.entities.Type
import de.christian2003.chaching.model.backup.ImportStrategy
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

) {

	/**
	 * List of all transfers (with type) sorted by value date.
	 */
	val allTransfers = transferDao.selectAllTransfersWithTypeSortedByDate()

	/**
	 * List of all types sorted by date of creation.
	 */
	val allTypes = typeDao.selectAllTypesSortedByDate()


	/**
	 * Returns the transfer (with type) of the ID specified. If no transfer with the ID specified
	 * exists, null is returned.
	 *
	 * @return	Transfer with type or null.
	 */
	suspend fun selectTransferWithTypeById(transferId: UUID): TransferWithType? {
		return transferDao.selectTransferWithTypeById(transferId)
	}

	/**
	 * Inserts a new transfer into the database.
	 *
	 * @param transfer	New transfer to insert.
	 */
	suspend fun insertTransfer(transfer: Transfer) {
		transferDao.insert(transfer)
	}

	/**
	 * Deletes the transfer passed from the database.
	 *
	 * @param transfer	Transfer to delete.
	 */
	suspend fun deleteTransfer(transfer: Transfer) {
		transferDao.delete(transfer)
	}

	/**
	 * Updates an existing transfer within the database with the transfer passed.
	 *
	 * @param transfer	Transfer to update.
	 */
	suspend fun updateTransfer(transfer: Transfer) {
		transferDao.update(transfer)
	}


	/**
	 * Returns the type with the ID passed. If no type with the ID specified exists, null is
	 * returned.
	 *
	 * @return	Type of the ID specified or null.
	 */
	suspend fun selectTypeById(typeId: UUID): Type? {
		return typeDao.selectTypeById(typeId)
	}

	/**
	 * Inserts a new type into the database.
	 *
	 * @param type	New type to insert.
	 */
	suspend fun insertType(type: Type) {
		typeDao.insert(type)
	}

	/**
	 * Deletes the type passed from the database.
	 *
	 * @param type	Type to delete.
	 */
	suspend fun deleteType(type: Type) {
		typeDao.delete(type)
	}

	/**
	 * Updates an existing type from the database with the type passed.
	 *
	 * @param type	Type to update.
	 */
	suspend fun updateType(type: Type) {
		typeDao.update(type)
	}


	/**
	 * Imports the types and transfers from the backup according to the import strategy.
	 *
	 * @param types				Types to import.
	 * @param transfers			Transfers to import
	 * @param importStrategy	Import strategy indicates how to handle existing data with the import.
	 */
	suspend fun importDataFromBackup(types: List<Type>, transfers: List<Transfer>, importStrategy: ImportStrategy) {
		when (importStrategy) {
			ImportStrategy.DELETE_EXISTING_DATA -> {
				transferDao.deleteAll()
				typeDao.deleteAll()
				typeDao.insertAndIgnore(types)
				transferDao.insertAndIgnore(transfers)
			}
			ImportStrategy.REPLACE_EXISTING_DATA -> {
				typeDao.insertAndReplace(types)
				transferDao.insertAndReplace(transfers)
			}
			ImportStrategy.IGNORE_EXISTING_DATA -> {
				typeDao.insertAndIgnore(types)
				transferDao.insertAndIgnore(transfers)
			}
		}
	}

}
