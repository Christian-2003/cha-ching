package de.christian2003.chaching.view.types

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.ui.composables.Headline
import de.christian2003.chaching.ui.composables.HelpCard
import java.util.UUID


/**
 * Screen displays a list of all types available.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 * @param onCreateType  Callback invoked to navigate to another screen to create a new type.
 * @param onEditType    Callback invoked to navigate to another screen to edit an existing type.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                                    horizontal = dimensionResource(R.dimen.margin_horizontal)
                                )
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                                    vertical = dimensionResource(R.dimen.padding_vertical)
                                )
                        ) {
                            Button(
                                onClick = onCreateType
                            ) {
                                CreateButtonContent()
                            }
                        }
                    }
                    item {
                        HorizontalDivider()
                        Headline(
                            title = stringResource(R.string.types_listTitle)
                        )
                    }
                    items(typeEntities) { type ->
                        TypeListItem(
                            type = type,
                            onEdit = {
                                onEditType(type.id)
                            },
                            onDelete = {
                                viewModel.typeToDelete = type
                            }
                        )
                    }
                }
            }

            if (viewModel.typeToDelete != null) {
                ConfirmDeleteDialog(
                    text = stringResource(R.string.types_confirmDelete, viewModel.typeToDelete!!.name),
                    onDismiss = {
                        viewModel.typeToDelete = null
                    },
                    onConfirm = {
                        viewModel.deleteType()
                    }
                )
            }
        }
    }
}


/**
 * Displays a type as list item.
 *
 * @param type      Type to display as list item.
 * @param onEdit    Callback invoked to edit the type.
 * @param onDelete  Callback invoked to delete the type.
 */
@Composable
private fun TypeListItem(
    type: Type,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isDropdownVisible by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEdit()
            }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Icon(
            painter = painterResource(type.icon.drawableResourceId),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_horizontal))
        )
        Text(
            text = type.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Box {
            IconButton(
                onClick = {
                    isDropdownVisible = !isDropdownVisible
                },
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = ""
                )
                DropdownMenu(
                    expanded = isDropdownVisible,
                    onDismissRequest = {
                        isDropdownVisible = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.button_edit))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.button_delete))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onDelete()
                        }
                    )
                }
            }
        }
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
