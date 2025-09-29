package de.christian2003.chaching.plugin.presentation.view.transfers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.ui.composables.TransferListItem


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
    val types: List<Type> by viewModel.allTypes.collectAsState(emptyList())
    val transfers: List<Transfer> by viewModel.allTransfers.collectAsState(emptyList())
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)

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
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            if (transfers.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.transfers_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.transfers_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_transfers),
                    modifier = Modifier.fillMaxSize()
                )
            }
            else {
                TransferList(
                    transfers = transfers,
                    onEditTransfer = { transfer ->
                        onEditTransfer(transfer.type, transfer.id)
                    },
                    onDeleteTransfer = { transfer ->
                        viewModel.transferToDelete = transfer
                    },
                    onQueryTransferType = { transfer ->
                        viewModel.getTypeForTransfer(transfer, types)
                    },
                    windowInsets = WindowInsets(
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
            }
        }

        NavigationBarProtection(
            color = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f),
            windowInsets = WindowInsets(
                bottom = innerPadding.calculateBottomPadding()
            )
        )

        if (viewModel.transferToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(
                    R.string.transfers_confirmDelete,
                    viewModel.getTypeForTransfer(viewModel.transferToDelete!!, types)?.name ?: ""
                ),
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
 * @param transfers             List of transfers to display.
 * @param onEditTransfer        Callback invoked to edit a transfer.
 * @param onDeleteTransfer      Callback invoked to delete a transfer.
 * @param onQueryTransferType   Callback invoked to query the type for a transfer.
 * @param windowInsets          Insets for the screen.
 */
@Composable
private fun TransferList(
    transfers: List<Transfer>,
    onEditTransfer: (Transfer) -> Unit,
    onDeleteTransfer: (Transfer) -> Unit,
    onQueryTransferType: (Transfer) -> Type?,
    windowInsets: WindowInsets
) {
    val groupedTransfers = transfers.groupBy { transfer ->
        transfer.valueDate.withDayOfMonth(1)
    }
    LazyColumn {
        groupedTransfers.forEach { (month, monthTransfer) ->
            item {
                Column {
                    HorizontalDivider()
                    Headline(
                        title = stringArrayResource(R.array.months)[month.month.ordinal]
                    )
                }
            }
            items(monthTransfer) { transfer ->
                TransferListItem(
                    transfer = transfer,
                    onEdit = onEditTransfer,
                    onDelete = onDeleteTransfer,
                    onQueryTransferType = onQueryTransferType
                )
            }
        }
        item {
            Box(
                modifier = Modifier.windowInsetsBottomHeight(windowInsets)
            )
        }
    }
}
