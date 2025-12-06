package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to get a transfer by it's ID.
 *
 * @param repository    Repository to access transfers.
 */
class GetTransferByIdUseCase @Inject constructor(
    private val repository: TransferRepository
) {

    /**
     * Returns the transfer with the specified ID. If no transfer exists, null is returned.
     *
     * @param transferId    ID of the transfer to return.
     * @return              Transfer with the specified ID or null.
     */
    suspend fun getTransferById(transferId: UUID): Transfer? {
        return repository.getTransferById(transferId)
    }

}
