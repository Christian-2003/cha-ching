package de.christian2003.chaching

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.HoverInteraction
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
import de.christian2003.chaching.view.main.MainScreen
import de.christian2003.chaching.view.main.MainViewModel
import de.christian2003.chaching.view.transfers.TransfersScreen
import de.christian2003.chaching.view.transfers.TransfersViewModel
import de.christian2003.chaching.view.type.TypeScreen
import de.christian2003.chaching.view.type.TypeViewModel
import de.christian2003.chaching.view.types.TypesScreen
import de.christian2003.chaching.view.types.TypesViewModel
import java.util.UUID


class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			ChaChing()
		}
	}

}


@Composable
fun ChaChing() {
	val navController: NavHostController = rememberNavController()
	val database: ChaChingDatabase = ChaChingDatabase.getInstance(LocalContext.current)
	val repository = ChaChingRepository(database.transferDao, database.typeDao)

	val mainViewModel: MainViewModel = viewModel()
	val transfersViewModel: TransfersViewModel = viewModel()
	val typesViewModel: TypesViewModel = viewModel()

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
					onCreateTransfer = {

					},
					onEditTransfer = { transferId ->

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
				Log.d("MainActivity", "composable type/id called")
				val typeId: UUID? = try {
					UUID.fromString(backStackEntry.arguments!!.getString("typeId"))
				} catch (e: Exception) {
					null
				}

				val typeViewModel: TypeViewModel = viewModel()
				typeViewModel.init(repository, typeId)
				TypeScreen(
					viewModel = typeViewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}
		}
	}
}
