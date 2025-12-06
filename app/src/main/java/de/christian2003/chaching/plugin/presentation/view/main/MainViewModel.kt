package de.christian2003.chaching.plugin.presentation.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject


/**
 * View model for the MainScreen.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
	getAllTypesUseCase: GetAllTypesUseCase,
	getRecentTransfersUseCase: GetRecentTransfersUseCase,
	getTransfersInDateRangeUseCase: GetTransfersInDateRangeUseCase,
	private val deleteTransferUseCase: DeleteTransferUseCase
): ViewModel() {

	/**
	 * Indicates whether the view model is initialized.
	 */
	private var isInitialized: Boolean = false

	/**
	 * Stores all transfers from the last month.
	 */
	private val transfersLastMonth: Flow<List<Transfer>> = getTransfersInDateRangeUseCase.getTransfersInDateRange(LocalDate.now().minusDays(30), LocalDate.now())

	/**
	 * Stores the update manager.
	 */
	private lateinit var updateManager: UpdateManager


	/**
	 * List of all types.
	 */
	val allTypes: Flow<List<Type>> = getAllTypesUseCase.getAllTypes()

	/**
	 * List of recent transfers.
	 */
	val recentTransfers: Flow<List<Transfer>> = getRecentTransfersUseCase.getRecentTransfers()

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
	 * @param updateManager	Update manager.
	 */
	fun init(updateManager: UpdateManager) {
		isUpdateAvailable = updateManager.isUpdateAvailable
		if (!isInitialized) {
			this@MainViewModel.updateManager = updateManager
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
