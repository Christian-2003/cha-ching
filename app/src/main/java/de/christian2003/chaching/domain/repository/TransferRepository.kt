package de.christian2003.chaching.domain.repository

import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.Flow
import java.util.UUID


/**
 * Repository allows to access and manipulate transfers.
 */
interface TransferRepository {

    /**
     * Returns a flow containing list of all transfers.
     *
     * @return  List of all transfers.
     */
    fun getAllTransfers(): Flow<List<Transfer>>


    /**
     * Returns the transfer of the ID passed as argument. If no transfer with the ID specified
     * exists, null is returned.
     *
     * @param id    ID of the transfer to return.
     * @return      Transfer with the ID specified.
     */
    suspend fun getTransferById(id: UUID): Transfer?


    /**
     * Creates a new transfer.
     *
     * @param transfer  New transfer to create.
     */
    suspend fun createNewTransfer(transfer: Transfer)


    /**
     * Updates an existing transfer with the data passed as argument. The transfer updated is
     * determined based on the ID. This means that the ID of the transfer passed must be identical
     * to the ID of the transfer to update.
     *
     * @param transfer  Data with which to update the existing transfer.
     */
    suspend fun updateExistingTransfer(transfer: Transfer)


    /**
     * Deletes the transfer passed as argument.
     *
     * @param transfer  Transfer to delete.
     */
    suspend fun deleteTransfer(transfer: Transfer)

}
