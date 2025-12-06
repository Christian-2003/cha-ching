package de.christian2003.chaching.plugin.presentation.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.application.usecases.transfer.DeleteTransferUseCase
import de.christian2003.chaching.application.usecases.transfer.GetRecentTransfersUseCase
import de.christian2003.chaching.application.usecases.transfer.GetTransfersInDateRangeUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesUseCase
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.analysis.overview.OverviewCalcResult
import de.christian2003.chaching.plugin.infrastructure.update.UpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate


/**
 * View model for the MainScreen.
 */
class MainViewModel: ViewModel() {

	private lateinit var deleteTransferUseCase: DeleteTransferUseCase

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
	 * @param deleteTransferUseCase				Use case to delete a transfer.
	 * @param getRecentTransfersUseCase			Use case to get recent transfers.
	 * @param getTransfersInDateRangeUseCase	Use case to get transfers in a date range.
	 * @param getAllTypesUseCase				Use case to get a list of all types.
	 * @param updateManager						Update manager.
	 */
	fun init(
		deleteTransferUseCase: DeleteTransferUseCase,
		getRecentTransfersUseCase: GetRecentTransfersUseCase,
		getTransfersInDateRangeUseCase: GetTransfersInDateRangeUseCase,
		getAllTypesUseCase: GetAllTypesUseCase, updateManager: UpdateManager
	) {
		isUpdateAvailable = updateManager.isUpdateAvailable
		if (!isInitialized) {
			this@MainViewModel.deleteTransferUseCase = deleteTransferUseCase
			this@MainViewModel.updateManager = updateManager
			allTypes = getAllTypesUseCase.getAllTypes()
			recentTransfers = getRecentTransfersUseCase.getRecentTransfers()
			transfersLastMonth = getTransfersInDateRangeUseCase.getTransfersInDateRange(LocalDate.now().minusDays(30), LocalDate.now())
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
			deleteTransferUseCase.deleteTransfer(transfer.id)
		}
	}


	/**
	 * Requests the download of the new version of the app (if available).
	 */
	fun requestDownload() {
		updateManager.requestDownload()
	}

}
