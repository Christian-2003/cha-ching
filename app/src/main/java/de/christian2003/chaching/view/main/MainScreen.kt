package de.christian2003.chaching.view.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.chaching.R
import de.christian2003.chaching.database.entities.Type
import java.util.UUID


/**
 * Screen displays the home screen once the user enters the app.
 *
 * @param viewModel				View model.
 * @param onNavigateToTransfers	Callback invoked to navigate to the transfers list.
 * @param onNavigateToTypes		Callback invoked to navigate to the types list.
 * @param onCreateTransfer		Callback invoked to create a new transfer.
 * @param onNavigateToSettings	Callback invoked to navigate to the settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
	viewModel: MainViewModel,
	onNavigateToTransfers: () -> Unit,
	onNavigateToTypes: () -> Unit,
	onCreateTransfer: (UUID) -> Unit,
	onNavigateToSettings: () -> Unit
) {
	val allTypes by viewModel.allTypes.collectAsState(emptyList())
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = stringResource(R.string.app_name)
					)
				},
				actions = {
					IconButton(
						onClick = onNavigateToSettings
					) {
						Icon(
							painter = painterResource(R.drawable.ic_settings),
							contentDescription = ""
						)
					}
				}
			)
		},
		floatingActionButton = {
			FabMenu(
				types = allTypes,
				onTypeClicked = { type ->
					onCreateTransfer(type.typeId)
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


/**
 * Displays the floating action button and it'S menu.
 *
 * @param types			List of types to display in the FAB menu.
 * @param onTypeClicked	Callback invoked once a type is clicked.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FabMenu(
	types: List<Type>,
	onTypeClicked: (Type) -> Unit
) {
	var isExpanded by remember { mutableStateOf(false) }
	val iconAnimator by animateFloatAsState(
		targetValue = if (isExpanded) { 0f } else { 45f },
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioMediumBouncy
		)
	)

	FloatingActionButtonMenu(
		expanded = isExpanded,
		button = {
			ToggleFloatingActionButton(
				checked = isExpanded,
				onCheckedChange = {
					isExpanded = !isExpanded
				}
			) {
				Icon(
					painter = painterResource(R.drawable.ic_cancel),
					tint = if (isExpanded) { MaterialTheme.colorScheme.onPrimary } else { MaterialTheme.colorScheme.onPrimaryContainer },
					contentDescription = "",
					modifier = Modifier.rotate(iconAnimator)
				)
			}
		}
	) {
		types.forEach { type ->
			FloatingActionButtonMenuItem(
				onClick = {
					onTypeClicked(type)
				},
				text = {
					Text(type.name)
				},
				icon = {
					Icon(
						painter = painterResource(type.icon.drawableResourceId),
						contentDescription = ""
					)
				}
			)
		}
	}

}
