package de.christian2003.chaching.plugin.presentation.view.transfers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class TransfersViewModel: ViewModel() {

    /**
     * Repository through which to access and manipulate transfers.
     */
    private lateinit var transferRepository: TransferRepository

    /**
     * Repository through which to access types.
     */
    private lateinit var typeRepository: TypeRepository

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
     * @param transferRepository    Repository to access and manipulate transfers.
     * @param typeRepository        Repository to access types.
     */
    fun init(transferRepository: TransferRepository, typeRepository: TypeRepository) {
        if (!isInitialized) {
            this.transferRepository = transferRepository
            this.typeRepository = typeRepository
            allTransfers = transferRepository.getAllTransfers()
            allTypes = typeRepository.getAllTypes()
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
            transferRepository.deleteTransfer(transfer)
        }
    }

}
