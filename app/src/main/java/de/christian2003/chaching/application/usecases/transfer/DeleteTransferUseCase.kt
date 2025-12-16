package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to delete a transfer.
 *
 * @param repository    Repository through which to access transfers.
 */
class DeleteTransferUseCase @Inject constructor(
    private val repository: TransferRepository
) {

    /**
     * Deletes the transfer with the specified ID. If no transfer exists, nothing happens.
     *
     * @param transferId    ID of the transfer to delete.
     */
    suspend fun deleteTransfer(transferId: UUID) {
        val transfer: Transfer? = repository.getTransferById(transferId)
        if (transfer != null) {
            repository.deleteTransfer(transfer)
        }
    }

}
