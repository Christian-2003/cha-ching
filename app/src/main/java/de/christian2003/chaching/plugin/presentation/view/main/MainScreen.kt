package de.christian2003.chaching.plugin.presentation.view.main

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.analysis.overview.OverviewCalcResult
import de.christian2003.chaching.domain.analysis.overview.OverviewCalcResultItem
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.TransferListItem
import de.christian2003.chaching.plugin.presentation.ui.composables.Value
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.util.UUID


/**
 * Screen displays the home screen once the user enters the app.
 *
 * @param viewModel				View model.
 * @param onNavigateToTransfers	Callback invoked to navigate to the transfers list.
 * @param onEditTransfer		Callback invoked to navigate to the screen showing a transfer.
 * @param onNavigateToTypes		Callback invoked to navigate to the types list.
 * @param onCreateTransfer		Callback invoked to create a new transfer.
 * @param onNavigateToSettings	Callback invoked to navigate to the settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
	viewModel: MainViewModel,
	onNavigateToTransfers: () -> Unit,
	onEditTransfer: (UUID, UUID) -> Unit,
	onNavigateToTypes: () -> Unit,
	onCreateTransfer: (UUID) -> Unit,
	onCreateNewType: () -> Unit,
	onNavigateToSettings: () -> Unit,
	onNavigateToAnalysis: () -> Unit
) {
	val allTypes by viewModel.allTypes.collectAsState(emptyList())
	val recentTransfers by viewModel.recentTransfers.collectAsState(emptyList())

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
					onCreateTransfer(type.id)
				},
				onCreateNewType = onCreateNewType
			)
		}
	) { innerPadding ->
		if (recentTransfers.isEmpty()) {
			EmptyPlaceholder(
				title = stringResource(R.string.main_emptyPlaceholder_title),
				subtitle = stringResource(R.string.main_emptyPlaceholder_subtitle),
				painter = painterResource(R.drawable.el_transfers),
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
			)
		}
		else {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.verticalScroll(rememberScrollState())
			) {
				AnimatedVisibility(viewModel.isUpdateAvailable && !viewModel.isUpdateMessageDismissed) {
					DownloadCard(
						onCancelClicked = {
							viewModel.isUpdateMessageDismissed = true
						},
						onConfirmClicked = {
							viewModel.isUpdateMessageDismissed = true
							viewModel.requestDownload()
						}
					)
				}
				Headline(stringResource(R.string.main_quickActions_title))
				QuickActions(
					onAnalysisClicked = onNavigateToAnalysis,
					onTransfersClicked = onNavigateToTransfers,
					onTypesClicked = onNavigateToTypes,
					modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
				)
				AnimatedVisibility(viewModel.overviewCalcResult != null) {
					Column {
						Headline(stringResource(R.string.main_overview_title))
						Overview(
							overviewCalcResult = viewModel.overviewCalcResult!!,
							modifier = Modifier.padding(
								start = dimensionResource(R.dimen.margin_horizontal),
								end = dimensionResource(R.dimen.margin_horizontal),
								bottom = dimensionResource(R.dimen.padding_vertical)
							)
						)
					}
				}
				Headline(stringResource(R.string.main_recentTransfers))
				TransferList(
					transfers = recentTransfers,
					onEditTransfer = { transfer ->
						onEditTransfer(transfer.type, transfer.id)
					},
					onDeleteTransfer = {
						viewModel.transferToDelete = it
					},
					onShowAllTransfers = onNavigateToTransfers,
					onQueryTransferType = { transfer ->
						viewModel.getTypeForTransfer(transfer, allTypes)
					},
					modifier = Modifier.padding(
						start = dimensionResource(R.dimen.margin_horizontal),
						end = dimensionResource(R.dimen.margin_horizontal),
						bottom = dimensionResource(R.dimen.padding_vertical)
					)
				)
			}
		}


		if (viewModel.transferToDelete != null) {
			ConfirmDeleteDialog(
				text = stringResource(
					R.string.transfers_confirmDelete,
					viewModel.getTypeForTransfer(viewModel.transferToDelete!!, allTypes)?.name ?: ""
				),
				onDismiss = {
					viewModel.transferToDelete = null
				},
				onConfirm = {
					viewModel.deleteTransfer()
				}
			)
		}
	}
}


/**
 * Displays a row with quick actions.
 *
 * @param onAnalysisClicked		Callback invoked once the button for analysis is clicked.
 * @param onTransfersClicked	Callback invoked once the button to show all transfers is clicked.
 * @param onTypesClicked		Callback invoked once the button to show all types is clicked.
 * @param modifier				Modifier.
 */
@Composable
fun QuickActions(
	onAnalysisClicked: () -> Unit,
	onTransfersClicked: () -> Unit,
	onTypesClicked: () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState())
	) {
		QuickActionsButton(
			painter = painterResource(R.drawable.ic_analysis),
			text = stringResource(R.string.main_quickActions_analysis),
			onClick = onAnalysisClicked,
			modifier = Modifier
				.padding(start = dimensionResource(R.dimen.margin_horizontal))
				.width(96.dp)
		)
		QuickActionsButton(
			painter = painterResource(R.drawable.ic_data),
			text = stringResource(R.string.main_quickActions_transfers),
			onClick = onTransfersClicked,
			modifier = Modifier
				.padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
				.width(96.dp)
		)
		QuickActionsButton(
			painter = painterResource(R.drawable.ic_types),
			text = stringResource(R.string.main_quickActions_types),
			onClick = onTypesClicked,
			modifier = Modifier
				.padding(end = dimensionResource(R.dimen.margin_horizontal))
				.width(96.dp)
		)
	}
}


/**
 * Displays a quick action button.
 *
 * @param painter	Painter for the button icon.
 * @param text		Text for the button.
 * @param onClick	Callback invoked once the button is clicked.
 * @param modifier	Modifier.
 */
@Composable
fun QuickActionsButton(
	painter: Painter,
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
	) {
		FilledIconButton(
			onClick = onClick,
			colors = IconButtonDefaults.filledIconButtonColors().copy(
				containerColor = MaterialTheme.colorScheme.surfaceContainer,
				contentColor = MaterialTheme.colorScheme.onSurfaceVariant
			),
			modifier = Modifier.size(dimensionResource(R.dimen.image_l))
		) {
			Icon(
				painter = painter,
				contentDescription = "",
				modifier = Modifier.size(dimensionResource(R.dimen.image_s))
			)
		}
		Text(
			text = text,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelMedium.copy(hyphens = Hyphens.Auto),
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical) / 2)
		)
	}
}


/**
 * Displays the overview at the top of the screen.
 *
 * @param overviewCalcResult	Data for the overview.
 * @param modifier				Modifier.
 */
@Composable
private fun Overview(
	overviewCalcResult: OverviewCalcResult,
	modifier: Modifier = Modifier
) {
	val valueFormat = DecimalFormat("#,###.00")
	if (overviewCalcResult.results.isNotEmpty()) {
		val colors = listOf<Color>(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
		Column(
			modifier = modifier
				.fillMaxWidth()
				.border(
					width = 1.dp,
					color = MaterialTheme.colorScheme.outline,
					shape = MaterialTheme.shapes.extraLarge
				)
				.clip(MaterialTheme.shapes.extraLarge)
				.padding(
					horizontal = dimensionResource(R.dimen.margin_horizontal),
					vertical = dimensionResource(R.dimen.padding_vertical)
				)
		) {
			//Total:
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(
					text = stringResource(R.string.main_overview_total),
					color = MaterialTheme.colorScheme.onSurface,
					style = MaterialTheme.typography.bodyLarge,
					modifier = Modifier
						.weight(1f)
						.padding(end = dimensionResource(R.dimen.padding_horizontal))
				)
				Value(valueFormat.format(overviewCalcResult.totalValue.toDouble() / 100))
			}
			Text(
				text = overviewCalcResult.overviewComparisonConnection.getLocalizedString(LocalContext.current, overviewCalcResult.totalValue),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				style = MaterialTheme.typography.bodyMedium,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = dimensionResource(R.dimen.padding_vertical))
			)
			HorizontalDivider(
				color = MaterialTheme.colorScheme.outline,
				modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
			)

			//Types:
			Row(
				modifier = Modifier.fillMaxWidth()
			) {
				//List:
				Column(
					modifier = Modifier.weight(1f)
				) {
					for (i in 0..overviewCalcResult.results.size - 1) {
						if (i >= 3) {
							break
						}
						OverviewItem(
							overviewCalcResultItem = overviewCalcResult.results[i],
							color = colors[i],
							valueFormat = valueFormat
						)
					}
				}

				//Diagram:
				OverviewChart(
					overviewCalcResultItems = overviewCalcResult.results,
					colors = colors,
					modifier = Modifier
						.align(Alignment.CenterVertically)
						.padding(
							start = dimensionResource(R.dimen.padding_horizontal),
							top = dimensionResource(R.dimen.padding_vertical)
						)
				)
			}
		}
	}
	else {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = modifier
				.fillMaxWidth()
				.border(
					width = 1.dp,
					color = MaterialTheme.colorScheme.outline,
					shape = MaterialTheme.shapes.extraLarge
				)
				.clip(MaterialTheme.shapes.extraLarge)
				.padding(
					horizontal = dimensionResource(R.dimen.margin_horizontal),
					vertical = dimensionResource(R.dimen.padding_vertical)
				)
		) {
			Text(
				text = stringResource(R.string.main_overview_noData),
				color = MaterialTheme.colorScheme.onSurface,
				style = MaterialTheme.typography.bodyLarge,
				modifier = Modifier
					.weight(1f)
					.padding(end = dimensionResource(R.dimen.padding_horizontal))
			)
			Image(
				painter = painterResource(R.drawable.el_overview),
				contentDescription = "",
				modifier = Modifier.size(dimensionResource(R.dimen.image_emptyPlaceholderSmall))
			)
		}
	}
}


/**
 * Displays an item of the overview.
 *
 * @param overviewCalcResultItem	Item to display.
 * @param color						Color with which to display the item.
 * @param valueFormat				Number format to format the item value.
 */
@Composable
private fun OverviewItem(
	overviewCalcResultItem: OverviewCalcResultItem,
	color: Color,
	valueFormat: NumberFormat
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.padding(
			top = dimensionResource(R.dimen.padding_vertical),
			end = dimensionResource(R.dimen.padding_horizontal)
		)
	) {
		Text(
			text = if (overviewCalcResultItem.type != null) { overviewCalcResultItem.type.name } else { stringResource(R.string.main_overview_otherTypes) },
			color = MaterialTheme.colorScheme.onSurface,
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.weight(1f)
				.padding(end = dimensionResource(R.dimen.padding_horizontal))
		)
		Text(
			text = stringResource(R.string.value_format, valueFormat.format(overviewCalcResultItem.value.toDouble() / 100)),
			color = color,
			style = MaterialTheme.typography.bodyMedium
		)
	}
}


/**
 * Displays a pie chart for the types of the overview.
 *
 * @param overviewCalcResultItems	Result items for which to display the chart.
 * @param colors					Colors with which to display the items.
 * @param modifier					Modifier.
 */
@Composable
private fun OverviewChart(
	overviewCalcResultItems: List<OverviewCalcResultItem>,
	colors: List<Color>,
	modifier: Modifier = Modifier
) {
	val data: MutableList<Pie> = mutableListOf()
	for (i in 0..overviewCalcResultItems.size - 1) {
		if (i >= 3) {
			break
		}
		data.add(Pie(
			label = if (overviewCalcResultItems[i].type != null) { overviewCalcResultItems[i].type!!.name } else { stringResource(R.string.main_overview_otherTypes) },
			data = overviewCalcResultItems[i].value.toDouble(),
			color = colors[i]
		))
	}

	PieChart(
		data = data.toList(),
		style = Pie.Style.Stroke(width = 24.dp),
		scaleAnimEnterSpec = spring(),
		modifier = modifier.size(96.dp)
	)
}


/**
 * Displays a list of recent transfers.
 *
 * @param transfers				List of recent transfers to display.
 * @param onEditTransfer		Callback invoked to edit a transfer.
 * @param onDeleteTransfer		Callback invoked to delete a transfer.
 * @param onShowAllTransfers	Callback invoke to show all transfers.
 * @param modifier				Modifier.
 */
@Composable
private fun TransferList(
	transfers: List<Transfer>,
	onEditTransfer: (Transfer) -> Unit,
	onDeleteTransfer: (Transfer) -> Unit,
	onShowAllTransfers: () -> Unit,
	onQueryTransferType: (Transfer) -> Type?,
	modifier: Modifier = Modifier
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.border(
				width = 1.dp,
				color = MaterialTheme.colorScheme.outline,
				shape = MaterialTheme.shapes.extraLarge
			)
			.clip(MaterialTheme.shapes.extraLarge)
	) {
		transfers.forEach { transfer ->
			TransferListItem(
				transfer = transfer,
				onEdit = onEditTransfer,
				onDelete = onDeleteTransfer,
				onQueryTransferType = onQueryTransferType
			)
		}
		HorizontalDivider(
			color = MaterialTheme.colorScheme.outline
		)
		TextButton(
			onClick = onShowAllTransfers
		) {
			Text(stringResource(R.string.button_showAllTransfers))
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
	onTypeClicked: (Type) -> Unit,
	onCreateNewType: () -> Unit
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
		FloatingActionButtonMenuItem(
			onClick = {
				onCreateNewType()
			},
			text = {
				Text(stringResource(R.string.button_createNewType))
			},
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_add),
					contentDescription = ""
				)
			},
			containerColor = MaterialTheme.colorScheme.surfaceContainer,
			contentColor = MaterialTheme.colorScheme.onSurfaceVariant
		)
		types.forEach { type ->
			if (type.metadata.isEnabledInQuickAccess) {
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

}


/**
 * Composable displays a card at the top of the view if a new app version is available to download.
 *
 * @param onCancelClicked   Callback invoked once the cancel-button is clicked.
 * @param onConfirmClicked  Callback invoked once the confirm-button is clicked.
 */
@Composable
fun DownloadCard(
	onCancelClicked: () -> Unit,
	onConfirmClicked: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				start = dimensionResource(R.dimen.margin_horizontal),
				end = dimensionResource(R.dimen.margin_horizontal),
				bottom = dimensionResource(R.dimen.padding_vertical)
			),
		shape = MaterialTheme.shapes.extraLarge
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.surfaceContainer)
				.padding(
					horizontal = dimensionResource(R.dimen.margin_horizontal),
					vertical = dimensionResource(R.dimen.padding_vertical)
				)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth()
			) {
				Icon(
					painter = painterResource(R.drawable.ic_info),
					tint = MaterialTheme.colorScheme.onSurfaceVariant,
					contentDescription = "",
					modifier = Modifier
						.padding(end = dimensionResource(R.dimen.padding_horizontal))
				)
				Text(
					text = stringResource(R.string.main_downloadMessage),
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			FlowRow(
				modifier = Modifier.align(Alignment.End).padding(top = dimensionResource(R.dimen.padding_vertical)),
				horizontalArrangement = Arrangement.End
			) {
				TextButton(
					onClick = onCancelClicked,
					colors = ButtonDefaults.textButtonColors().copy(
						contentColor = MaterialTheme.colorScheme.primary
					)
				) {
					Text(stringResource(R.string.button_dismiss))
				}
				TextButton(
					onClick = onConfirmClicked,
					colors = ButtonDefaults.textButtonColors().copy(
						contentColor = MaterialTheme.colorScheme.primary
					)
				) {
					Text(stringResource(R.string.button_download))
				}
			}
		}
	}
}
