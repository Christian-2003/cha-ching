package de.christian2003.chaching.plugin.presentation.view.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.analysis.small.SmallAnalysisUseCase
import de.christian2003.chaching.application.services.DateTimeFormatterService
import de.christian2003.chaching.application.services.GetTypeForTransferService
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.application.usecases.transfer.DeleteTransferUseCase
import de.christian2003.chaching.application.usecases.transfer.GetAllTransfersUseCase
import de.christian2003.chaching.application.usecases.transfer.GetRecentTransfersUseCase
import de.christian2003.chaching.application.usecases.transfer.GetTransfersInDateRangeUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesNotInTrashUseCase
import de.christian2003.chaching.application.usecases.type.GetAllTypesUseCase
import de.christian2003.chaching.application.usecases.type.GetTypeByIdUseCase
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.analysis.overview.OverviewCalcResult
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.plugin.infrastructure.update.UpdateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject


/**
 * View model for the MainScreen.
 *
 * @param getAllTypesUseCase				Use case to get a list of all types.
 * @param getAllTypesNotInTrashUseCase		Use case to get a list of all types that are not in the
 * 											trash bin.
 * @param getRecentTransfersUseCase			Use case to get a list of recent transfers.
 * @param getAllTransfersUseCase			Use case to get a list of all transfers.
 * @param getTransfersInDateRangeUseCase	Use case to get transfers in a date range.
 * @param deleteTransferUseCase				Use case to delete a transfer.
 * @param smallAnalysisUseCase				Use case to perform the small analysis.
 * @param getTypeByIdUseCase				Use case to get a type by it's ID.
 * @param getTypeForTransferService			Service through which to query the type of a transfer.
 * @param valueFormatterService				Service used to format currency values.
 * @param dateTimeFormatterService			Service used to format dates.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
	getAllTypesUseCase: GetAllTypesUseCase,
	getAllTypesNotInTrashUseCase: GetAllTypesNotInTrashUseCase,
	getRecentTransfersUseCase: GetRecentTransfersUseCase,
	getAllTransfersUseCase: GetAllTransfersUseCase,
	getTransfersInDateRangeUseCase: GetTransfersInDateRangeUseCase,
	private val deleteTransferUseCase: DeleteTransferUseCase,
	private val smallAnalysisUseCase: SmallAnalysisUseCase,
	private val getTypeByIdUseCase: GetTypeByIdUseCase,
	private val getTypeForTransferService: GetTypeForTransferService,
	private val valueFormatterService: ValueFormatterService,
	private val dateTimeFormatterService: DateTimeFormatterService
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
	 * List of all types that are not in trash.
	 */
	val allTypesNotInTrash: Flow<List<Type>> = getAllTypesNotInTrashUseCase.getAllTypesNotInTrash()

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

	var analysisResult: SmallAnalysisResult? by mutableStateOf(null)
		private set

	/**
	 * Indicates whether an update is available for the app.
	 */
	var isUpdateAvailable: Boolean by mutableStateOf(false)

	/**
	 * Indicates whether the update message has been dismissed by the user.
	 */
	var isUpdateMessageDismissed: Boolean by mutableStateOf(false)


	init {
		getAllTransfersUseCase.getAllTransfers().onEach {
			startAnalysis()
		}.launchIn(viewModelScope)
	}


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


	suspend fun queryType(transfer: Transfer): Type? {
		return getTypeForTransferService.getType(transfer)
	}

	suspend fun queryType(id: UUID): Type? {
		return getTypeByIdUseCase.getTypeById(id)
	}


	fun formatValue(value: TransferValue): String {
		return valueFormatterService.format(value)
	}


	fun formatValue(value: Double): String {
		return valueFormatterService.format(value)
	}

	fun formatDate(date: LocalDate): String {
		return dateTimeFormatterService.format(date)
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


	private fun startAnalysis() = viewModelScope.launch(Dispatchers.Default) {
		analysisResult = null
		val result: SmallAnalysisResult = smallAnalysisUseCase.analyzeData(LocalDate.now().minusMonths(1)) //TODO: Remove minusMonths
		analysisResult = result
	}

}
