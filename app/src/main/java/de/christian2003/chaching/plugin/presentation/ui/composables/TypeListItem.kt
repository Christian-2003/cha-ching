package de.christian2003.chaching.plugin.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.TypeShapes


/**
 * Displays a type as list item.
 *
 * @param type                  Type to display as list item.
 * @param onEdit                Callback invoked to edit the type.
 * @param onDelete              Callback invoked to move the type to the trash bin.
 * @param onRestoreFromTrash    Callback invoked to restore the item from the trash bin.
 * @param onDeletePermanently   Callback invoked to delete the type permanently.
 */
@Composable
fun TypeListItem(
    type: Type,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRestoreFromTrash: (() -> Unit)? = null,
    onDeletePermanently: (() -> Unit)? = null
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
                .clickable(onEdit != null) {
                    if (onEdit != null) {
                        onEdit()
                    }
                }
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
                    shape = TypeShapes.getShapeForTypeIcon(type.icon).shape,
                    color = if (type.metadata.isEnabledInQuickAccess) {
                        TypeShapes.getShapeColor(type.icon)
                    } else {
                        TypeShapes.getShapeColor(type.icon).copy(alpha = 0.5f)
                    }
                )
                Icon(
                    painter = painterResource(type.icon.drawableResourceId),
                    contentDescription = "",
                    tint = if (type.metadata.isEnabledInQuickAccess) {
                        TypeShapes.getOnShapeColor(type.icon)
                    } else {
                        TypeShapes.getOnShapeColor(type.icon).copy(alpha = 0.5f)
                    },
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = type.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (type.metadata.isEnabledInQuickAccess) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    },
                    maxLines = 2,
                    textDecoration = if (type.metadata.isEnabledInQuickAccess) {
                        TextDecoration.None
                    } else {
                        TextDecoration.LineThrough
                    },
                    overflow = TextOverflow.Ellipsis
                )
                if (!type.metadata.isEnabledInQuickAccess) {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.ic_invisible),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier
                                .padding(end = dimensionResource(R.dimen.padding_horizontal) / 2)
                                .size(dimensionResource(R.dimen.image_xxs))
                        )
                        Text(
                            text = stringResource(R.string.types_invisible),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
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
                        tint = if (type.metadata.isEnabledInQuickAccess) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        },
                        contentDescription = ""
                    )
                    DropdownMenu(
                        expanded = isDropdownVisible,
                        onDismissRequest = {
                            isDropdownVisible = false
                        }
                    ) {
                        if (onEdit != null) {
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
                        }
                        if (onDelete != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.button_moveToTrash))
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
                        if (onRestoreFromTrash != null) {
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
                        }
                        if (onDeletePermanently != null) {
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
}
