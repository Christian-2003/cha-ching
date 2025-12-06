package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.domain.type.Type
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to create a new transfer.
 *
 * @param transferRepository    Repository to access transfers.
 * @param typeRepository        Repository to access types.
 */
class CreateTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository,
    private val typeRepository: TypeRepository
) {

    /**
     * Creates a new transfer with the specified arguments.
     *
     * @param value         Value for the transfer.
     * @param date          Date on which the transfer takes effect.
     * @param isSalary      Indicates whether the transfer is a salary.
     * @param hoursWorked   Hours worked for the salary.
     * @param typeId        ID of the type for which to create this transfer.
     */
    suspend fun createTransfer(
        value: Int,
        date: LocalDate,
        isSalary: Boolean,
        hoursWorked: Int,
        typeId: UUID
    ) {
        val type: Type? = typeRepository.getTypeById(typeId)
        if (type == null) {
            throw IllegalStateException("Type does not exist. Cannot create transfer for unexisting type")
        }

        val transfer = Transfer(
            transferValue = TransferValue(
                value = value,
                date = date,
                isSalary = isSalary
            ),
            hoursWorked = hoursWorked,
            type = typeId
        )

        transferRepository.createNewTransfer(transfer)
    }

}
