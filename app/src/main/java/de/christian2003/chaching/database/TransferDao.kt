package de.christian2003.chaching.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.christian2003.chaching.database.entities.Transfer
import kotlinx.coroutines.flow.Flow


@Dao
interface TransferDao {

	@Query("SELECT * FROM transfers ORDER BY valueDate DESC")
	fun selectAllTransfersSortedByDate(): Flow<List<Transfer>>


	@Insert
	suspend fun insert(transfer: Transfer)


	@Delete
	suspend fun delete(transfer: Transfer)


	@Update
	suspend fun update(transfer: Transfer)

}
