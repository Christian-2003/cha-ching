package de.christian2003.chaching.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.TransferWithType
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
interface TransferDao {

	@Query("SELECT * FROM transfers ORDER BY valueDate DESC")
	fun selectAllTransfersSortedByDate(): Flow<List<Transfer>>


	@Transaction
	@Query("SELECT * FROM transfers ORDER BY valueDate DESC")
	fun selectAllTransfersWithTypeSortedByDate(): Flow<List<TransferWithType>>


	@Transaction
	@Query("SELECT * FROM transfers WHERE transferId = :transferId")
	suspend fun selectTransferWithTypeById(transferId: UUID): TransferWithType?


	@Insert
	suspend fun insert(transfer: Transfer)


	@Delete
	suspend fun delete(transfer: Transfer)


	@Update
	suspend fun update(transfer: Transfer)

}
