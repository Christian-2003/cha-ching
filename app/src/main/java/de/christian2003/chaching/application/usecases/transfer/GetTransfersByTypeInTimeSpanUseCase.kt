package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class GetTransfersByTypeInTimeSpanUseCase @Inject constructor(
    private val repository: TransferRepository
) {

    fun getTransfersByTypeInTimeSpan(
        typeId: UUID,
        start: LocalDate,
        end: LocalDate
    ): Flow<List<Transfer>> {
        return repository.getAllTransfersByTypeInTimeSpan(typeId, start, end)
    }

}
