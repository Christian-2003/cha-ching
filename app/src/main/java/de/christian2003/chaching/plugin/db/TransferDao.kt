package de.christian2003.chaching.plugin.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.christian2003.chaching.plugin.db.entities.TransferEntity
import de.christian2003.chaching.plugin.db.entities.TransferWithTypeEntity
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
	fun selectAllTransfersSortedByDate(): Flow<List<TransferEntity>>

	/**
	 * Returns all transfers (with type) sorted by the value date.
	 *
	 * @return	List of all transfers with type.
	 */
	@Transaction
	@Query("SELECT * FROM transfers ORDER BY valueDate DESC")
	fun selectAllTransfersWithTypeSortedByDate(): Flow<List<TransferWithTypeEntity>>

	/**
	 * Returns the last three transfers (with type).
	 *
	 * @return	Last three transfers with type.
	 */
	@Transaction
	@Query("SELECT * FROM transfers ORDER BY valueDate DESC LIMIT 3")
	fun selectRecentTransfersWithType(): Flow<List<TransferWithTypeEntity>>

	/**
	 * Returns the transfer (with type) of the ID passed. If no transfer with the ID specified
	 * exists, null is returned.
	 *
	 * @param transferId	ID of the transfer to query.
	 * @return				Transfer with type or null.
	 */
	@Transaction
	@Query("SELECT * FROM transfers WHERE transferId = :transferId")
	suspend fun selectTransferWithTypeById(transferId: UUID): TransferWithTypeEntity?

	/**
	 * Returns all transfers (with type) in the range between the epoch days passed.
	 *
	 * @param startEpochDay	First epoch day of the range.
	 * @param endEpochDay	Last epoch day of the range.
	 * @return				All transfers in the range specified.
	 */
	@Transaction
	@Query("SELECT * FROM transfers WHERE valueDate BETWEEN :startEpochDay AND :endEpochDay")
	fun selectTransfersWithValueDateRange(startEpochDay: Long, endEpochDay: Long): Flow<List<TransferWithTypeEntity>>


	/**
	 * Inserts a new transfer into the database.
	 *
	 * @param transfer	Transfer to insert.
	 */
	@Insert
	suspend fun insert(transfer: TransferEntity)


	/**
	 * Deletes the transfer from the database.
	 *
	 * @param transfer	Transfer to delete.
	 */
	@Delete
	suspend fun delete(transfer: TransferEntity)


	/**
	 * Updates the transfer in the database.
	 *
	 * @param transfer	Transfer to update.
	 */
	@Update
	suspend fun update(transfer: TransferEntity)


	@Query("DELETE FROM transfers")
	suspend fun deleteAll()

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAndIgnore(transfers: List<TransferEntity>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAndReplace(transfers: List<TransferEntity>)

}
