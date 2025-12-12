package de.christian2003.chaching.plugin.presentation

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christian2003.chaching.plugin.infrastructure.update.UpdateManager
import de.christian2003.chaching.plugin.presentation.ui.theme.ChaChingTheme
import de.christian2003.chaching.plugin.presentation.view.help.HelpScreen
import de.christian2003.chaching.plugin.presentation.view.help.HelpViewModel
import de.christian2003.chaching.plugin.presentation.view.licenses.LicensesScreen
import de.christian2003.chaching.plugin.presentation.view.licenses.LicensesViewModel
import de.christian2003.chaching.plugin.presentation.view.main.MainScreen
import de.christian2003.chaching.plugin.presentation.view.main.MainViewModel
import de.christian2003.chaching.plugin.presentation.view.onboarding.OnboardingScreen
import de.christian2003.chaching.plugin.presentation.view.onboarding.OnboardingViewModel
import de.christian2003.chaching.plugin.presentation.view.settings.SettingsScreen
import de.christian2003.chaching.plugin.presentation.view.settings.SettingsViewModel
import de.christian2003.chaching.plugin.presentation.view.transfer.TransferScreen
import de.christian2003.chaching.plugin.presentation.view.transfer.TransferViewModel
import de.christian2003.chaching.plugin.presentation.view.transfers.TransfersScreen
import de.christian2003.chaching.plugin.presentation.view.transfers.TransfersViewModel
import de.christian2003.chaching.plugin.presentation.view.type.TypeScreen
import de.christian2003.chaching.plugin.presentation.view.type.TypeViewModel
import de.christian2003.chaching.plugin.presentation.view.types.TypesScreen
import de.christian2003.chaching.plugin.presentation.view.types.TypesViewModel
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import de.christian2003.chaching.plugin.presentation.view.analysis.AnalysisScreen
import de.christian2003.chaching.plugin.presentation.view.analysis.AnalysisViewModel
import de.christian2003.chaching.plugin.presentation.view.trash.TrashScreen
import de.christian2003.chaching.plugin.presentation.view.trash.TrashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Main activity for the ChaChing app.
 */
@AndroidEntryPoint
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
        enableEdgeToEdge(
            navigationBarStyle = if (isNightMode()) {
                SystemBarStyle.dark(
                    scrim = android.graphics.Color.TRANSPARENT
                )
            } else {
                SystemBarStyle.light(
                    scrim = android.graphics.Color.TRANSPARENT,
                    darkScrim = android.graphics.Color.TRANSPARENT
                )
            }
        )
		setContent {
			ChaChing(
				updateManager = updateManager!!
			)
		}
	}


    private fun isNightMode(): Boolean {
        val currentMode: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
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
    val context: Context = LocalContext.current
    val preferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
	var isOnboardingFinished: Boolean by rememberSaveable { mutableStateOf(preferences.getBoolean("onboardingFinished", false)) }
    var useGlobalTheme: Boolean by rememberSaveable { mutableStateOf(preferences.getBoolean("global_theme", false)) }
    var themeContrast: ThemeContrast by rememberSaveable { mutableStateOf(ThemeContrast.entries[preferences.getInt("theme_contrast", 0)]) }

    ChaChingTheme(
        dynamicColor = useGlobalTheme,
        contrast = themeContrast
    ) {
        NavHost(
            navController = navController,
            startDestination = if (isOnboardingFinished) {
                "main"
            } else {
                "onboarding"
            },
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)
                ) + fadeIn(spring(Spring.DampingRatioLowBouncy))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessHigh)
                ) + fadeOut(spring(Spring.DampingRatioLowBouncy))
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {

            composable("main") {
                val viewModel: MainViewModel = hiltViewModel()
                viewModel.init(
                    updateManager = updateManager
                )
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
                    },
                    onNavigateToAnalysis = {
                        navController.navigate("analysis")
                    }
                )
            }


            composable("transfers") {
                val viewModel: TransfersViewModel = hiltViewModel()
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
            ) {
                val viewModel: TransferViewModel = hiltViewModel()
                TransferScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("types") {
                val viewModel: TypesViewModel = hiltViewModel()
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
            ) {
                val viewModel: TypeViewModel = hiltViewModel()
                TypeScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("analysis") {
                val viewModel: AnalysisViewModel = hiltViewModel()
                AnalysisScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("settings") {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToTypes = {
                        navController.navigate("types")
                    },
                    onNavigateToTrash = {
                        navController.navigate("trash")
                    },
                    onNavigateToLicenses = {
                        navController.navigate("licenses")
                    },
                    onNavigateToHelpMessages = {
                        navController.navigate("help")
                    },
                    onNavigateToOnboarding = {
                        navController.navigate("onboarding")
                    },
                    onUseGlobalThemeChange = {
                        useGlobalTheme = it
                    },
                    onThemeContrastChange = {
                        themeContrast = it
                    }
                )
            }


            composable("trash") {
                val viewModel: TrashViewModel = hiltViewModel()
                TrashScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("licenses") {
                val viewModel: LicensesViewModel = hiltViewModel()
                LicensesScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("help") {
                val viewModel: HelpViewModel = hiltViewModel()
                HelpScreen(
                    viewModel = viewModel,
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }


            composable("onboarding") {
                val viewModel: OnboardingViewModel = hiltViewModel()
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
                        } else {
                            //Onboarding shown through settings:
                            navController.navigateUp()
                        }
                    }
                )
            }
        }
    }
}
