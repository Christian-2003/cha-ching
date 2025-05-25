package de.christian2003.chaching

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christian2003.chaching.ui.theme.ChaChingTheme
import de.christian2003.chaching.view.main.MainScreen
import de.christian2003.chaching.view.main.MainViewModel


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

	val mainViewModel: MainViewModel = viewModel()

	ChaChingTheme {
		NavHost(
			navController = navController,
			startDestination = "main"
		) {
			composable("main") {
				mainViewModel.init()
				MainScreen(
					viewModel = mainViewModel
				)
			}
		}
	}
}
