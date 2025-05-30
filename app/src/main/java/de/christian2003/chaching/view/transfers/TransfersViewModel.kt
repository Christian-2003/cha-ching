package de.christian2003.chaching.view.transfers

import androidx.lifecycle.ViewModel
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.TransferWithType
import kotlinx.coroutines.flow.Flow


class TransfersViewModel: ViewModel() {

    private lateinit var repository: ChaChingRepository

    lateinit var allTransfers: Flow<List<TransferWithType>>


    fun init(repository: ChaChingRepository) {
        this.repository = repository
        allTransfers = repository.allTransfers
    }

}
