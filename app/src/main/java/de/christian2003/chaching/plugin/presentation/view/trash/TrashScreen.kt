package de.christian2003.chaching.plugin.presentation.view.trash

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.ui.composables.TypeListItem


/**
 * Screen displaying the trash bin.
 *
 * @param viewModel     View model for the screen.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    onNavigateUp: () -> Unit
) {
    val context: Context = LocalContext.current
    val types: List<Type> by viewModel.typesInTrash.collectAsState(emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.trash_title))
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
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()

        if (types.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
                    .fillMaxSize()
            ) {
                AnimatedVisibility(viewModel.isHelpCardVisible) {
                    HelpCard(
                        text = stringResource(R.string.trash_help),
                        onDismiss = {
                            viewModel.dismissHelpCard()
                        },
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                    )
                }
                val modifier: Modifier = if (viewModel.isHelpCardVisible) { Modifier } else { Modifier.fillMaxSize() }
                EmptyPlaceholder(
                    title = stringResource(R.string.trash_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.trash_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_trash),
                    modifier = modifier
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
                    .fillMaxSize()
            ) {
                item {
                    AnimatedVisibility(viewModel.isHelpCardVisible) {
                        HelpCard(
                            text = stringResource(R.string.trash_help),
                            onDismiss = {
                                viewModel.dismissHelpCard()
                            },
                            modifier = Modifier.padding(
                                start = dimensionResource(R.dimen.margin_horizontal),
                                end = dimensionResource(R.dimen.margin_horizontal),
                                bottom = dimensionResource(R.dimen.padding_vertical) * 2
                            )
                        )
                    }
                }
                itemsIndexed(types) { index, type ->
                    TypeListItem(
                        type = type,
                        isFirst = index == 0,
                        isLast = index == types.size - 1,
                        onRestoreFromTrash = {
                            viewModel.restoreType(type)
                        },
                        onDeletePermanently = {
                            viewModel.typeToDelete = type
                        }
                    )
                }
                item {
                    Box(
                        modifier = Modifier.height(bottomPadding)
                    )
                }
            }
        }

        NavigationBarProtection(height = bottomPadding)
    }

    val typeToDelete: Type? = viewModel.typeToDelete
    if (typeToDelete != null) {
        ConfirmDeleteDialog(
            text = stringResource(R.string.trash_confirmDelete, typeToDelete.name),
            onConfirm = {
                viewModel.dismissDeleteDialog(typeToDelete)
            },
            onDismiss = {
                viewModel.dismissDeleteDialog()
            }
        )
    }
}
