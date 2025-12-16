package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to update an existing transfer.
 *
 * @param repository    Repository to access transfers.
 */
class UpdateTransferUseCase @Inject constructor(
    private val repository: TransferRepository
) {

    /**
     * Updates the existing transfer with the specified ID. If no transfer with the ID exists,
     * nothing happens.
     *
     * @param transferId    ID of the transfer to update.
     * @param value         New value for the transfer.
     * @param date          New value date for the transfer.
     * @param isSalary      Indicates whether the transfer is salary.
     * @param hoursWorked   New hours worked for the transfer.
     */
    suspend fun updateTransfer(
        transferId: UUID,
        value: Int,
        date: LocalDate,
        isSalary: Boolean,
        hoursWorked: Int
    ) {
        val transfer: Transfer? = repository.getTransferById(transferId)
        if (transfer != null) {
            transfer.transferValue = TransferValue(
                value = value,
                date = date,
                isSalary = isSalary
            )
            transfer.hoursWorked = hoursWorked
            repository.updateExistingTransfer(transfer)
        }
    }

}
