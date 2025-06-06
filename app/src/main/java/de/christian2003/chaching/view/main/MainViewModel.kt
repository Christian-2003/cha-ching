package de.christian2003.chaching.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.TransferWithType
import de.christian2003.chaching.database.entities.Type
import de.christian2003.chaching.model.transfers.OverviewCalcResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate


/**
 * View model for the MainScreen.
 */
class MainViewModel: ViewModel() {

	/**
	 * Repository from which to source data.
	 */
	private lateinit var repository: ChaChingRepository


	/**
	 * List of all types.
	 */
	lateinit var allTypes: Flow<List<Type>>

	/**
	 * List of recent transfers.
	 */
	lateinit var recentTransfers: Flow<List<TransferWithType>>

	/**
	 * Transfer to delete. If no transfer shall be deleted, this is null.
	 */
	var transferToDelete: TransferWithType? by mutableStateOf(null)

	var overviewCalcResult: OverviewCalcResult? by mutableStateOf(null)


	/**
	 * Instantiates the view model.
	 *
	 * @param repository	Repository from which to source data.
	 */
	fun init(repository: ChaChingRepository) = viewModelScope.launch(Dispatchers.IO) {
		this@MainViewModel.repository = repository
		allTypes = repository.allTypes
		recentTransfers = repository.recentTransfers

		overviewCalcResult = OverviewCalcResult(repository.selectTransfersForMonth(LocalDate.now()))
	}


	fun deleteTransfer() = viewModelScope.launch(Dispatchers.IO) {
		val transfer: TransferWithType? = transferToDelete
		if (transfer != null) {
			transferToDelete = null
			repository.deleteTransfer(transfer.transfer)
		}
	}

}
