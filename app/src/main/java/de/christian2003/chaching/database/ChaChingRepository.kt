package de.christian2003.chaching.database

import de.christian2003.chaching.database.entities.Transfer

class ChaChingRepository(

	private val transferDao: TransferDao

) {

	val allTransfers = transferDao.selectAllTransfersSortedByDate()


	suspend fun insertTransfer(transfer: Transfer) {
		transferDao.insert(transfer)
	}


	suspend fun deleteTransfer(transfer: Transfer) {
		transferDao.delete(transfer)
	}


	suspend fun updateTransfer(transfer: Transfer) {
		transferDao.update(transfer)
	}

}
