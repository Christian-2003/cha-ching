package de.christian2003.chaching.view.transfers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.DisableContentCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.TransferWithType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class TransfersViewModel: ViewModel() {

    private lateinit var repository: ChaChingRepository

    lateinit var allTransfers: Flow<List<TransferWithType>>


    var transferToDelete: TransferWithType? by mutableStateOf(null)


    fun init(repository: ChaChingRepository) {
        this.repository = repository
        allTransfers = repository.allTransfers
    }


    fun delete() = viewModelScope.launch(Dispatchers.IO) {
        val transferWithType = transferToDelete
        transferToDelete = null
        if (transferWithType != null) {
            repository.deleteTransfer(transferWithType.transfer)
        }
    }

}
