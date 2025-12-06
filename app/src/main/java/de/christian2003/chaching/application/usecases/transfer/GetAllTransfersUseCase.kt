package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.Flow


/**
 * Use case to get a list of all transfers.
 *
 * @param repository    Repository to access transfers.
 */
class GetAllTransfersUseCase(
    private val repository: TransferRepository
) {

    /**
     * Returns a list of all transfers.
     *
     * @return  Flow contains a list of all transfers.
     */
    fun getAllTransfers(): Flow<List<Transfer>> {
        return repository.getAllTransfers()
    }

}
