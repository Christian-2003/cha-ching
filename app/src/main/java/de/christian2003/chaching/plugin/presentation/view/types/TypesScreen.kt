package de.christian2003.chaching.plugin.presentation.view.types

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.chaching.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialogOptions
import de.christian2003.chaching.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.ui.composables.TypeListItem
import java.util.UUID


/**
 * Screen displays a list of all types available.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 * @param onCreateType  Callback invoked to navigate to another screen to create a new type.
 * @param onEditType    Callback invoked to navigate to another screen to edit an existing type.
 */
@Composable
fun TypesScreen(
    viewModel: TypesViewModel,
    onNavigateUp: () -> Unit,
    onCreateType: () -> Unit,
    onEditType: (UUID) -> Unit
) {
    val typeEntities: List<Type> by viewModel.allTypes.collectAsState(emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.types_title))
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
                actions = {
                    IconButton(
                        onCreateType
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
        ) {
            if (typeEntities.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.types_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.types_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_transfers),
                    modifier = Modifier.fillMaxSize(),
                    onButtonClick = {
                        onCreateType()
                    }
                ) {
                    CreateButtonContent()
                }
            }
            else {
                LazyColumn {
                    item {
                        AnimatedVisibility(viewModel.isHelpCardVisible) {
                            HelpCard(
                                text = stringResource(R.string.types_help),
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
                    itemsIndexed(typeEntities) { index, type ->
                        TypeListItem(
                            type = type,
                            isFirst = index == 0,
                            isLast = index == typeEntities.size - 1,
                            onEdit = {
                                onEditType(type.id)
                            },
                            onDelete = {
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

            if (viewModel.typeToDelete != null) {
                ConfirmDeleteDialog(
                    text = stringResource(
                        R.string.types_confirmDelete,
                        viewModel.typeToDelete!!.name
                    ),
                    onDismiss = {
                        viewModel.typeToDelete = null
                    },
                    onConfirm = {
                        viewModel.deleteType()
                    },
                    options = ConfirmDeleteDialogOptions.MoveToTrash
                )
            }
        }

        NavigationBarProtection(height = bottomPadding)
    }
}


/**
 * Displays content for the button to create a new type.
 */
@Composable
private fun CreateButtonContent() {
    Row {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = stringResource(R.string.button_createNewType)
        )
    }
}
