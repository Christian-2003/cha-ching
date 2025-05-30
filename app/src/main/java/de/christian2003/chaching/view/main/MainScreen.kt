package de.christian2003.chaching.view.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.christian2003.chaching.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	viewModel: MainViewModel,
	onNavigateToTransfers: () -> Unit,
	onNavigateToTypes: () -> Unit
) {
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = stringResource(R.string.app_name)
					)
				}
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			Button(
				onClick = onNavigateToTransfers
			) {
				Text("Show Transfers List")
			}
			Button(
				onClick = onNavigateToTypes
			) {
				Text("Show Types List")
			}
		}
	}
}
