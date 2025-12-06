package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to get a list of the transfers with the most recent value dates.
 *
 * @param repository    Repository to access transfers.
 */
class GetRecentTransfersUseCase @Inject constructor(
    private val repository: TransferRepository
) {

    /**
     * Returns a list of the transfers with the most recent value dates.
     *
     * @return  Flow contains a list with the transfers with the most recent value dates.
     */
    fun getRecentTransfers(): Flow<List<Transfer>> {
        return repository.getRecentTransfers()
    }

}
