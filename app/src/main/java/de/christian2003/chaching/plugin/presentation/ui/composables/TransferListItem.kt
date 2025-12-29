package de.christian2003.chaching.plugin.presentation.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.TypeShapes
import java.time.LocalDate


/**
 * Displays a single transfer as list item.
 *
 * @param transfer              Transfer to display.
 * @param isFirst               Whether this is the first list item.
 * @param isLast                Whether this is the last list item.
 * @param onEdit                Callback invoked to edit the transfer.
 * @param onDelete              Callback invoked to delete the transfer.
 * @param onQueryTransferType   Callback invoked to query the type for a transfer.
 * @param onFormatValue         Callback invoked to format a value.
 * @param onFormatDate          Callback invoked to format a date.
 * @param isClickable           Whether the list item is clickable.
 */
@Composable
fun TransferListItem(
    transfer: Transfer,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: (Transfer) -> Unit,
    onDelete: (Transfer) -> Unit,
    onQueryTransferType: suspend (Transfer) -> Type?,
    onFormatValue: (TransferValue) -> String,
    onFormatDate: (LocalDate) -> String,
    isClickable: Boolean = true
) {
    var isExpanded: Boolean by remember { mutableStateOf(false) }
    val type: Type? by produceState(null) {
        value = onQueryTransferType(transfer)
    }
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(isClickable) {
                    isExpanded = !isExpanded
                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (type != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.padding_horizontal))
                            .size(dimensionResource(R.dimen.image_m))
                    ) {
                        Shape(
                            shape = TypeShapes.getShapeForTypeIcon(type!!.icon).shape,
                            color = TypeShapes.getShapeColor(type!!.icon)
                        )
                        Icon(
                            painter = painterResource(type!!.icon.drawableResourceId),
                            contentDescription = "",
                            tint = TypeShapes.getOnShapeColor(type!!.icon),
                            modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                ) {
                    Text(
                        text = type?.name ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (!isExpanded) { 1 } else { Int.MAX_VALUE },
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = onFormatDate(transfer.transferValue.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = onFormatValue(transfer.transferValue),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (transfer.transferValue.isSalary) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLargeIncreased)
                        .background(if (transfer.transferValue.isSalary) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        })
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
}


/**
 * Displays a value.
 *
 * @param formattedValue    Formatted value to display.
 * @param isSalary          Whether the value is a salary or not.
 */
@Composable
@Deprecated("Do not use anymore")
fun Value(
    formattedValue: String,
    isSalary: Boolean = true
) {
    Text(
        text = stringResource(R.string.value_format, formattedValue),
        style = MaterialTheme.typography.bodyMedium,
        color = if (isSalary) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLargeIncreased)
            .background(if (isSalary) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            })
            .padding(
                vertical = 4.dp,
                horizontal = 12.dp
            )
    )
}
