package de.christian2003.chaching

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import de.christian2003.chaching.model.update.UpdateManager
import de.christian2003.chaching.ui.theme.ChaChingTheme
import de.christian2003.chaching.view.help.HelpScreen
import de.christian2003.chaching.view.help.HelpViewModel
import de.christian2003.chaching.view.licenses.LicensesScreen
import de.christian2003.chaching.view.licenses.LicensesViewModel
import de.christian2003.chaching.view.main.MainScreen
import de.christian2003.chaching.view.main.MainViewModel
import de.christian2003.chaching.view.onboarding.OnboardingScreen
import de.christian2003.chaching.view.onboarding.OnboardingViewModel
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
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Main activity for the ChaChing app.
 */
class MainActivity : ComponentActivity() {

	private var updateManager: UpdateManager? = null


	/**
	 * Instantiates the app on state changes.
	 *
	 * @param savedInstanceState	State saved previously by the instance.
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		//Update manager:
		if (updateManager == null) {
			updateManager = UpdateManager.getInstance(this)
		}

		//Splash screen:
		val splashScreen = installSplashScreen()
		var keepSplashScreen = true
		splashScreen.setKeepOnScreenCondition { keepSplashScreen }
		lifecycleScope.launch {
			delay(resources.getInteger(R.integer.splash_duration).toLong())
			keepSplashScreen = false
		}

		//App content:
		enableEdgeToEdge()
		setContent {
			ChaChing(
				updateManager = updateManager!!
			)
		}
	}

}


/**
 * Root composable for the ChaChing-app.
 *
 * @param updateManager	Update manager which detects app updates.
 */
@Composable
fun ChaChing(updateManager: UpdateManager) {
	val navController: NavHostController = rememberNavController()
	val database: ChaChingDatabase = ChaChingDatabase.getInstance(LocalContext.current)
	val repository = ChaChingRepository(database.transferDao, database.typeDao)
	val context: Context = LocalContext.current
	var isOnboardingFinished: Boolean by rememberSaveable { mutableStateOf(context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("onboardingFinished", false)) }

	ChaChingTheme {
		NavHost(
			navController = navController,
			startDestination = if (isOnboardingFinished) { "main" } else { "onboarding" }
		) {
			composable("main") {
				val viewModel: MainViewModel = viewModel()
				viewModel.init(repository, updateManager)
				MainScreen(
					viewModel = viewModel,
					onNavigateToTransfers = {
						navController.navigate("transfers")
					},
					onEditTransfer = { typeId, transferId ->
						navController.navigate("transfer/$typeId/$transferId")
					},
					onNavigateToTypes = {
						navController.navigate("types")
					},
					onCreateTransfer = { typeId ->
						navController.navigate("transfer/$typeId/")
					},
					onCreateNewType = {
						navController.navigate("type/")
					},
					onNavigateToSettings = {
						navController.navigate("settings")
					}
				)
			}


			composable("transfers") {
				val viewModel: TransfersViewModel = viewModel()
				viewModel.init(repository)
				TransfersScreen(
					viewModel = viewModel,
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

				val viewModel: TransferViewModel = viewModel()
				viewModel.init(repository, typeId!!, transferId)
				TransferScreen(
					viewModel = viewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}


			composable("types") {
				val viewModel: TypesViewModel = viewModel()
				viewModel.init(repository)
				TypesScreen(
					viewModel = viewModel,
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

				val viewModel: TypeViewModel = viewModel()
				viewModel.init(repository, typeId)
				TypeScreen(
					viewModel = viewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}


			composable("settings") {
				val viewModel: SettingsViewModel = viewModel()
				viewModel.init(repository)
				SettingsScreen(
					viewModel = viewModel,
					onNavigateUp = {
						navController.navigateUp()
					},
					onNavigateToTypes = {
						navController.navigate("types")
					},
					onNavigateToLicenses = {
						navController.navigate("licenses")
					},
					onNavigateToHelpMessages = {
						navController.navigate("help")
					},
					onNavigateToOnboarding = {
						navController.navigate("onboarding")
					}
				)
			}


			composable("licenses") {
				val viewModel: LicensesViewModel = viewModel()
				viewModel.init()
				LicensesScreen(
					viewModel = viewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}


			composable("help") {
				val viewModel: HelpViewModel = viewModel()
				viewModel.init()
				HelpScreen(
					viewModel = viewModel,
					onNavigateUp = {
						navController.navigateUp()
					}
				)
			}


			composable("onboarding") {
				val viewModel: OnboardingViewModel = viewModel()
				viewModel.init(repository)
				OnboardingScreen(
					viewModel = viewModel,
					onNavigateUp = {
						if (!isOnboardingFinished) {
							//Onboarding shown for first time:
							isOnboardingFinished = true
							context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
								putBoolean("onboardingFinished", true)
							}
							navController.navigate("main") {
								popUpTo("onboarding") {
									inclusive = true
								}
							}
						}
						else {
							//Onboarding shown through settings:
							navController.navigateUp()
						}
					}
				)
			}
		}
	}
}
