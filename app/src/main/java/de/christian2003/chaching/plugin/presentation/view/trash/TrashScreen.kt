package de.christian2003.chaching.plugin.presentation.view.trash

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.type.DeletedType
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.TypeShapes
import de.christian2003.chaching.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.chaching.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import java.time.LocalDateTime


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
    val types: List<DeletedType> by viewModel.typesInTrash.collectAsState(emptyList())
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
                        deletedType = type,
                        isFirst = index == 0,
                        isLast = index == types.size - 1,
                        onRestoreFromTrash = {
                            viewModel.restoreType(type)
                        },
                        onDeletePermanently = {
                            viewModel.typeToDelete = type.type
                        },
                        onFormatDateTime = {
                            viewModel.formatDateTime(it)
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




/**
 * Displays a type as list item.
 *
 * @param deletedType           Deleted type to display as list item.
 * @param isFirst               Whether this is the first list item.
 * @param isLast                Whether this is the last list item.
 * @param onRestoreFromTrash    Callback invoked to restore the item from the trash bin.
 * @param onDeletePermanently   Callback invoked to delete the type permanently.
 * @param onFormatDateTime      Callback invoked to format a LocalDateTime instance.
 */
@Composable
private fun TypeListItem(
    deletedType: DeletedType,
    isFirst: Boolean,
    isLast: Boolean,
    onRestoreFromTrash: () -> Unit,
    onDeletePermanently: () -> Unit,
    onFormatDateTime: (LocalDateTime) -> String
) {
    var isDropdownVisible by remember { mutableStateOf(false) }
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_horizontal),
                    top = dimensionResource(R.dimen.padding_vertical),
                    end = dimensionResource(R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
                    .size(dimensionResource(R.dimen.image_m))
            ) {
                Shape(
                    shape = TypeShapes.getShapeForTypeIcon(deletedType.type.icon).shape,
                    color = TypeShapes.getShapeColor(deletedType.type.icon)
                )
                Icon(
                    painter = painterResource(deletedType.type.icon.drawableResourceId),
                    contentDescription = "",
                    tint = TypeShapes.getOnShapeColor(deletedType.type.icon),
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = deletedType.type.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.trash_deletedLabel, onFormatDateTime(deletedType.deletedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Box {
                IconButton(
                    onClick = {
                        isDropdownVisible = !isDropdownVisible
                    },
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        tint = MaterialTheme.colorScheme.onSurface,
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
                                Text(stringResource(R.string.button_restore))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_restore),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                isDropdownVisible = false
                                onRestoreFromTrash()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.button_deletePermanently))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete_permanently),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                isDropdownVisible = false
                                onDeletePermanently()
                            }
                        )
                    }
                }
            }
        }
    }
}
