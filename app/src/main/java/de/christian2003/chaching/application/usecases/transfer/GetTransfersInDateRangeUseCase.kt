package de.christian2003.chaching.application.usecases.transfer

import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

//TODO: This is a temporary use case until the analysis feature is reworked and can be invoked through a use case.
@Deprecated("Use analysis use case")
class GetTransfersInDateRangeUseCase(
    private val repository: TransferRepository
) {

    @Deprecated("Use analysis use case")
    fun getTransfersInDateRange(start: LocalDate, end: LocalDate): Flow<List<Transfer>> {
        return repository.getAllTransfersInDateRange(start, end)
    }

}
