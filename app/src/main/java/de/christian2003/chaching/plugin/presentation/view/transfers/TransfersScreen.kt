package de.christian2003.chaching.plugin.presentation.view.transfers

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.runtime.produceState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.UUID
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.ui.composables.TransferListItem
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import java.time.LocalDate


/**
 * Screen displays a list of all transfers available.
 *
 * @param viewModel         View model.
 * @param onNavigateUp      Callback invoked to navigate up on the navigation stack.
 * @param onEditTransfer    Callback invoked to navigate to the page to edit a transfer.
 */
@Composable
fun TransfersScreen(
    viewModel: TransfersViewModel,
    onNavigateUp: () -> Unit,
    onEditTransfer: (UUID, UUID) -> Unit
) {
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
                        viewModel.getTypeForTransfer(transfer)
                    },
                    onFormatValue = {
                        viewModel.formatValue(it)
                    },
                    onFormatDate = {
                        viewModel.formatDate(it)
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

        val transferToDelete: Transfer? = viewModel.transferToDelete
        if (transferToDelete != null) {
            val typeName: String by produceState("") {
                value = viewModel.getTypeForTransfer(transferToDelete)?.name ?: ""
            }
            ConfirmDeleteDialog(
                text = stringResource(R.string.transfers_confirmDelete, typeName),
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
 * @param onFormatValue         Callback invoked to format a currency value.
 * @param onFormatDate          Callback invoked to format a date.
 * @param windowInsets          Insets for the screen.
 */
@Composable
private fun TransferList(
    transfers: List<Transfer>,
    onEditTransfer: (Transfer) -> Unit,
    onDeleteTransfer: (Transfer) -> Unit,
    onQueryTransferType: suspend (Transfer) -> Type?,
    onFormatValue: (TransferValue) -> String,
    onFormatDate: (LocalDate) -> String,
    windowInsets: WindowInsets
) {
    val groupedTransfers = transfers.groupBy { transfer ->
        transfer.transferValue.date.withDayOfMonth(1)
    }
    LazyColumn {
        groupedTransfers.forEach { (month, monthTransfer) ->
            item {
                Column {
                    Headline(
                        title = String.format(stringArrayResource(R.array.months)[month.month.ordinal], month.year)
                    )
                }
            }
            itemsIndexed(monthTransfer) { index, transfer ->
                TransferListItem(
                    transfer = transfer,
                    isFirst = index == 0,
                    isLast = index == monthTransfer.size - 1,
                    onEdit = onEditTransfer,
                    onDelete = onDeleteTransfer,
                    onQueryTransferType = onQueryTransferType,
                    onFormatValue = onFormatValue,
                    onFormatDate = onFormatDate
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
