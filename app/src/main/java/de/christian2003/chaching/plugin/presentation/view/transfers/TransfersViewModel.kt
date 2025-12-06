package de.christian2003.chaching.plugin.presentation.view.transfers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.application.usecases.transfer.DeleteTransferUseCase
import de.christian2003.chaching.application.usecases.transfer.GetAllTransfersUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesUseCase
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class TransfersViewModel: ViewModel() {

    private lateinit var deleteTransferUseCase: DeleteTransferUseCase

    /**
     * Indicates whether the view model has been initialized.
     */
    private var isInitialized: Boolean = false

    /**
     * List of all transfers.
     */
    lateinit var allTransfers: Flow<List<Transfer>>

    /**
     * List of all types.
     */
    lateinit var allTypes: Flow<List<Type>>


    /**
     * Transfer to delete. If no transfer shall be deleted, this is null.
     */
    var transferToDelete: Transfer? by mutableStateOf(null)


    /**
     * Instantiates the view model.
     *
     * @param getAllTransfersUseCase    Use case to get a list of all transfers.
     * @param deleteTransferUseCase     Use case to delete a transfer.
     * @param getAllTypesUseCase        Use case to get a list of all types.
     */
    fun init(getAllTransfersUseCase: GetAllTransfersUseCase, deleteTransferUseCase: DeleteTransferUseCase, getAllTypesUseCase: GetAllTypesUseCase) {
        if (!isInitialized) {
            this.deleteTransferUseCase = deleteTransferUseCase
            allTransfers = getAllTransfersUseCase.getAllTransfers()
            allTypes = getAllTypesUseCase.getAllTypes()
            isInitialized = true
        }
    }


    fun getTypeForTransfer(transfer: Transfer, types: List<Type>): Type? {
        var transferType: Type? = null
        types.forEach { type ->
            if (type.id == transfer.type) {
                transferType = type
                return@forEach
            }
        }
        return transferType
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
