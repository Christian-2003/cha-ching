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


/**
 * Data access object to access transfers.
 */
@Dao
interface TransferDao {

	/**
	 * Returns all transfers sorted by the value date.
	 *
	 * @return	List of all transfers.
	 */
	@Query("SELECT * FROM transfers ORDER BY valueDate DESC")
	fun selectAllTransfersSortedByDate(): Flow<List<Transfer>>


	/**
	 * Returns all transfers (with type) sorted by the value date.
	 *
	 * @return	List of all transfers with type.
	 */
	@Transaction
	@Query("SELECT * FROM transfers ORDER BY valueDate DESC")
	fun selectAllTransfersWithTypeSortedByDate(): Flow<List<TransferWithType>>


	/**
	 * Returns the transfer (with type) of the ID passed. If no transfer with the ID specified
	 * exists, null is returned.
	 *
	 * @param transferId	ID of the transfer to query.
	 * @return				Transfer with type or null.
	 */
	@Transaction
	@Query("SELECT * FROM transfers WHERE transferId = :transferId")
	suspend fun selectTransferWithTypeById(transferId: UUID): TransferWithType?


	/**
	 * Inserts a new transfer into the database.
	 *
	 * @param transfer	Transfer to insert.
	 */
	@Insert
	suspend fun insert(transfer: Transfer)


	/**
	 * Deletes the transfer from the database.
	 *
	 * @param transfer	Transfer to delete.
	 */
	@Delete
	suspend fun delete(transfer: Transfer)


	/**
	 * Updates the transfer in the database.
	 *
	 * @param transfer	Transfer to update.
	 */
	@Update
	suspend fun update(transfer: Transfer)

}
