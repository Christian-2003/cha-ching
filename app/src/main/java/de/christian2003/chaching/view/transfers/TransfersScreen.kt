package de.christian2003.chaching.view.transfers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.database.entities.TransferWithType
import de.christian2003.chaching.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.ui.composables.EmptyPlaceholder
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * Screen displays a list of all transfers available.
 *
 * @param viewModel         View model.
 * @param onNavigateUp      Callback invoked to navigate up on the navigation stack.
 * @param onEditTransfer    Callback invoked to navigate to the page to edit a transfer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersScreen(
    viewModel: TransfersViewModel,
    onNavigateUp: () -> Unit,
    onEditTransfer: (UUID, UUID) -> Unit
) {
    val transfers: List<TransferWithType> by viewModel.allTransfers.collectAsState(emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.transfers_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (transfers.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.transfers_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.transfers_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_transfers),
                    modifier = Modifier.fillMaxSize()
                ) { }
            }
            else {
                TransferList(
                    transfers = transfers,
                    onEditTransfer = { transfer ->
                        onEditTransfer(transfer.type.typeId, transfer.transfer.transferId)
                    },
                    onDeleteTransfer = { transfer ->
                        viewModel.transferToDelete = transfer
                    }
                )
            }
        }
        if (viewModel.transferToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.transfers_confirmDelete, viewModel.transferToDelete!!.type.name),
                onDismiss = {
                    viewModel.transferToDelete = null
                },
                onConfirm = {
                    viewModel.delete()
                }
            )
        }
    }
}


/**
 * Displays the list of transfers.
 *
 * @param transfers         List of transfers to display.
 * @param onEditTransfer    Callback invoked to edit a transfer.
 * @param onDeleteTransfer  Callback invoked to delete a transfer.
 */
@Composable
private fun TransferList(
    transfers: List<TransferWithType>,
    onEditTransfer: (TransferWithType) -> Unit,
    onDeleteTransfer: (TransferWithType) -> Unit
) {
    LazyColumn {
        items(transfers) { transfer ->
            TransferListItem(
                transfer = transfer,
                onEdit = onEditTransfer,
                onDelete = onDeleteTransfer
            )
        }
    }
}


/**
 * Displays a single transfer as list item.
 *
 * @param transfer  Transfer to display.
 * @param onEdit    Callback invoked to edit the transfer.
 * @param onDelete  Callback invoked to delete the transfer.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransferListItem(
    transfer: TransferWithType,
    onEdit: (TransferWithType) -> Unit,
    onDelete: (TransferWithType) -> Unit
) {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = transfer.type.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transfer.transfer.valueDate.format(formatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = stringResource(R.string.types_value, transfer.transfer.getFormattedValue()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraLargeIncreased)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(
                        vertical = 4.dp,
                        horizontal = 12.dp
                    )
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(spring(Spring.DampingRatioMediumBouncy)),
            exit = shrinkVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeOut(spring(Spring.DampingRatioMediumBouncy))
        ) {
            Row {
                FilledIconButton(
                    onClick = {
                        onEdit(transfer)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = ""
                    )
                }
                FilledIconButton(
                    onClick = {
                        onDelete(transfer)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}
