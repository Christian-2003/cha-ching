package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.application.repository.TransferRepository
import javax.inject.Inject


/**
 * Use case to get the number of transfers that are hidden because their type is in the trash bin.
 *
 * @param repository    Repository to access the transfers.
 */
class CountTransfersWhoseTypeIsInTrashUseCase @Inject constructor(
    private val repository: TransferRepository
) {

    /**
     * Returns the number of transfers that whose type is in the trash bin.
     *
     * @return  Number of transfers whose type is in trash bin.
     */
    suspend fun countTransfersWhoseTypeIsInTrash(): Int {
        return repository.countTransfersWhoseTypeIsInTrash()
    }

}
