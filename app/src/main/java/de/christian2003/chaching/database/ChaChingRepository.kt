package de.christian2003.chaching.database

import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.Type
import java.util.UUID

class ChaChingRepository(

	private val transferDao: TransferDao,

	private val typeDao: TypeDao

) {

	val allTransfers = transferDao.selectAllTransfersWithTypeSortedByDate()

	val allTypes = typeDao.selectAllTypesSortedByDate()


	suspend fun insertTransfer(transfer: Transfer) {
		transferDao.insert(transfer)
	}

	suspend fun deleteTransfer(transfer: Transfer) {
		transferDao.delete(transfer)
	}

	suspend fun updateTransfer(transfer: Transfer) {
		transferDao.update(transfer)
	}

	suspend fun selectTypeById(typeId: UUID): Type {
		return typeDao.selectTypeById(typeId)
	}

	suspend fun insertType(type: Type) {
		typeDao.insert(type)
	}

	suspend fun deleteType(type: Type) {
		typeDao.delete(type)
	}

	suspend fun updateType(type: Type) {
		typeDao.update(type)
	}

}
