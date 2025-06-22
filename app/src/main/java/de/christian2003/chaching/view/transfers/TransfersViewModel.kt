package de.christian2003.chaching.view.transfers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.plugin.db.ChaChingRepository
import de.christian2003.chaching.plugin.db.entities.TransferWithTypeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class TransfersViewModel: ViewModel() {

    /**
     * Repository from which to source data.
     */
    private lateinit var repository: ChaChingRepository

    private var isInitialized: Boolean = false

    /**
     * List of all transfers.
     */
    lateinit var allTransfers: Flow<List<TransferWithTypeEntity>>


    /**
     * Transfer to delete. If no transfer shall be deleted, this is null.
     */
    var transferToDelete: TransferWithTypeEntity? by mutableStateOf(null)


    /**
     * Instantiates the view model.
     *
     * @param repository    Repository from which to source the data.
     */
    fun init(repository: ChaChingRepository) {
        if (!isInitialized) {
            this.repository = repository
            allTransfers = repository.allTransfersDeprecated
            isInitialized = true
        }
    }


    /**
     * Deletes the transfer indicated by "transferToDelete".
     */
    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        val transferWithType = transferToDelete
        transferToDelete = null
        if (transferWithType != null) {
            repository.deleteTransfer(transferWithType.transfer)
        }
    }

}
