package de.christian2003.chaching.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.model.transfers.OverviewCalcResult
import de.christian2003.chaching.model.update.UpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate


/**
 * View model for the MainScreen.
 */
class MainViewModel: ViewModel() {

	/**
	 * Repository from which to source data.
	 */
	private lateinit var transferRepository: TransferRepository

	private lateinit var typeRepository: TypeRepository

	/**
	 * Indicates whether the view model is initialized.
	 */
	private var isInitialized: Boolean = false

	/**
	 * Stores all transfers from the last month.
	 */
	private lateinit var transfersLastMonth: Flow<List<Transfer>>

	/**
	 * Stores the update manager.
	 */
	private lateinit var updateManager: UpdateManager


	/**
	 * List of all types.
	 */
	lateinit var allTypes: Flow<List<Type>>

	/**
	 * List of recent transfers.
	 */
	lateinit var recentTransfers: Flow<List<Transfer>>

	/**
	 * Transfer to delete. If no transfer shall be deleted, this is null.
	 */
	var transferToDelete: Transfer? by mutableStateOf(null)

	/**
	 * Result for the overview.
	 */
	var overviewCalcResult: OverviewCalcResult? by mutableStateOf(null)

	/**
	 * Indicates whether an update is available for the app.
	 */
	var isUpdateAvailable: Boolean by mutableStateOf(false)

	/**
	 * Indicates whether the update message has been dismissed by the user.
	 */
	var isUpdateMessageDismissed: Boolean by mutableStateOf(false)


	/**
	 * Instantiates the view model.
	 *
	 * @param transferRepository	Repository to access and manipulate transfers.
	 * @param typeRepository		Repository to access types.
	 * @param updateManager			Update manager.
	 */
	fun init(transferRepository: TransferRepository, typeRepository: TypeRepository, updateManager: UpdateManager) {
		isUpdateAvailable = updateManager.isUpdateAvailable
		if (!isInitialized) {
			this@MainViewModel.transferRepository = transferRepository
			this@MainViewModel.typeRepository = typeRepository
			this@MainViewModel.updateManager = updateManager
			allTypes = typeRepository.getAllTypes()
			recentTransfers = transferRepository.getAllTransfers()
			transfersLastMonth = transferRepository.getAllTransfersInDateRange(LocalDate.now().minusDays(30), LocalDate.now())
			isInitialized = true
			//All code after 'collect' is not called, therefore, this must be the last method call of the init-function!
			viewModelScope.launch(Dispatchers.IO) {
				transfersLastMonth.collect { transfersList ->
					overviewCalcResult = OverviewCalcResult(transfersList, allTypes.first())
				}
			}
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
	 * Deletes the transfer currently selected for deletion (i.e. stored in "transferToDelete").
	 */
	fun deleteTransfer() = viewModelScope.launch(Dispatchers.IO) {
		val transfer: Transfer? = transferToDelete
		if (transfer != null) {
			transferToDelete = null
			transferRepository.deleteTransfer(transfer)
		}
	}


	/**
	 * Requests the download of the new version of the app (if available).
	 */
	fun requestDownload() {
		updateManager.requestDownload()
	}

}
