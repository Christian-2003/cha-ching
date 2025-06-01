package de.christian2003.chaching

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christian2003.chaching.database.ChaChingDatabase
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.ui.theme.ChaChingTheme
import de.christian2003.chaching.view.licenses.LicensesScreen
import de.christian2003.chaching.view.licenses.LicensesViewModel
import de.christian2003.chaching.view.main.MainScreen
import de.christian2003.chaching.view.main.MainViewModel
import de.christian2003.chaching.view.settings.SettingsScreen
import de.christian2003.chaching.view.settings.SettingsViewModel
import de.christian2003.chaching.view.transfer.TransferScreen
import de.christian2003.chaching.view.transfer.TransferViewModel
import de.christian2003.chaching.view.transfers.TransfersScreen
import de.christian2003.chaching.view.transfers.TransfersViewModel
import de.christian2003.chaching.view.type.TypeScreen
import de.christian2003.chaching.view.type.TypeViewModel
import de.christian2003.chaching.view.types.TypesScreen
import de.christian2003.chaching.view.types.TypesViewModel
import java.util.UUID


/**
 * Main activity for the ChaChing app.
 */
class MainActivity : ComponentActivity() {

	/**
	 * Instantiates the app on state changes.
	 *
	 * @param savedInstanceState	State saved previously by the instance.
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ChaChing()
		}
	}

}


/**
 * Root composable for the ChaChing-app.
 */
@Composable
fun ChaChing() {
	val navController: NavHostController = rememberNavController()
	val database: ChaChingDatabase = ChaChingDatabase.getInstance(LocalContext.current)
	val repository = ChaChingRepository(database.transferDao, database.typeDao)

	val mainViewModel: MainViewModel = viewModel()
	val transfersViewModel: TransfersViewModel = viewModel()
	val transferViewModel: TransferViewModel = viewModel()
	val typesViewModel: TypesViewModel = viewModel()
	val typeViewModel: TypeViewModel = viewModel()
	val settingsViewModel: SettingsViewModel = viewModel()
	val licensesViewModel: LicensesViewModel = viewModel()

	ChaChingTheme {
		NavHost(
			navController = navController,
			startDestination = "main"
		) {
			composable("main") {
				mainViewModel.init(repository)
				MainScreen(
					viewModel = mainViewModel,
					onNavigateToTransfers = {
						navController.navigate("transfers")
					},
					onNavigateToTypes = {
						navController.navigate("types")
					},
					onCreateTransfer = { typeId ->
						navController.navigate("transfer/$typeId/")
					},
					onNavigateToSettings = {
						navController.navigate("settings")
					}
				)
			}


			composable("transfers") {
				transfersViewModel.init(repository)
				TransfersScreen(
					viewModel = transfersViewModel,
					onNavigateUp = {
						navController.navigateUp()
					},
					onEditTransfer = { typeId, transferId ->
						navController.navigate("transfer/$typeId/$transferId")
					}
				)
			}


			composable(
				route = "transfer/{typeId}/{transferId}",
				arguments = listOf(
					navArgument("typeId") { type = NavType.StringType },
					navArgument("transferId") { type = NavType.StringType }
				)
			) { backStackEntry ->
				val typeId: UUID? = try {
					UUID.fromString(backStackEntry.arguments!!.getString("typeId"))
				} catch (_: Exception) {
					return@composable
				}
				val transferId: UUID? = try {
					UUID.fromString(backStackEntry.arguments!!.getString("transferId"))
				} catch (_: Exception) {
					null
				}

				transferViewModel.init(repository, typeId!!, transferId)
				TransferScreen(
					viewModel = transferViewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}


			composable("types") {
				typesViewModel.init(repository)
				TypesScreen(
					viewModel = typesViewModel,
					onNavigateUp = {
						navController.navigateUp()
					},
					onCreateType = {
						navController.navigate("type/")
					},
					onEditType = { typeId ->
						navController.navigate("type/$typeId")
					}
				)
			}


			composable(
				route = "type/{typeId}",
				arguments = listOf(
					navArgument("typeId") { type = NavType.StringType }
				)
			) { backStackEntry ->
				val typeId: UUID? = try {
					UUID.fromString(backStackEntry.arguments!!.getString("typeId"))
				} catch (_: Exception) {
					null
				}

				typeViewModel.init(repository, typeId)
				TypeScreen(
					viewModel = typeViewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}


			composable("settings") {
				settingsViewModel.init(repository)
				SettingsScreen(
					viewModel = settingsViewModel,
					onNavigateUp = {
						navController.navigateUp()
					},
					onNavigateToTypes = {
						navController.navigate("types")
					},
					onNavigateToLicenses = {
						navController.navigate("licenses")
					}
				)
			}


			composable("licenses") {
				licensesViewModel.init()
				LicensesScreen(
					viewModel = licensesViewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}
		}
	}
}
