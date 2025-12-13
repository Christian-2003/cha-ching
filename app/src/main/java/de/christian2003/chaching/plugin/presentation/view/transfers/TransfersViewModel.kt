package de.christian2003.chaching.plugin.presentation.view.transfers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.services.GetTypeForTransferService
import de.christian2003.chaching.application.usecases.transfer.DeleteTransferUseCase
import de.christian2003.chaching.application.usecases.transfer.GetAllTransfersUseCase
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model for the screen which displays a list of transfers.
 *
 * @param getAllTransfersUseCase    Use case to get a list of all transfers.
 * @param deleteTransferUseCase     Use case to delete an existing transfer.
 * @param getTypeForTransferService Service to query the type of a transfer.
 */
@HiltViewModel
class TransfersViewModel @Inject constructor(
    getAllTransfersUseCase: GetAllTransfersUseCase,
    private val deleteTransferUseCase: DeleteTransferUseCase,
    private val getTypeForTransferService: GetTypeForTransferService
): ViewModel() {

    /**
     * List of all transfers.
     */
    val allTransfers: Flow<List<Transfer>> = getAllTransfersUseCase.getAllTransfers()


    /**
     * Transfer to delete. If no transfer shall be deleted, this is null.
     */
    var transferToDelete: Transfer? by mutableStateOf(null)


    suspend fun getTypeForTransfer(transfer: Transfer): Type? {
        return getTypeForTransferService.getType(transfer)
    }


    /**
     * Deletes the transfer indicated by "transferToDelete".
     */
    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        val transfer = transferToDelete
        transferToDelete = null
        if (transfer != null) {
            deleteTransferUseCase.deleteTransfer(transfer.id)
        }
    }

}
