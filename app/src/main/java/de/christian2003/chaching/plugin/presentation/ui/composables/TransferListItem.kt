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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * Displays a single transfer as list item.
 *
 * @param transfer              Transfer to display.
 * @param onEdit                Callback invoked to edit the transfer.
 * @param onDelete              Callback invoked to delete the transfer.
 * @param onQueryTransferType   Callback invoked to query the type for a transfer.
 */
@Composable
fun TransferListItem(
    transfer: Transfer,
    onEdit: (Transfer) -> Unit,
    onDelete: (Transfer) -> Unit,
    onQueryTransferType: (Transfer) -> Type?
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
                    text = onQueryTransferType(transfer)?.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transfer.transferValue.date.format(formatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Value(
                formattedValue = transfer.getFormattedValue()
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


/**
 * Displays a value.
 *
 * @param formattedValue    Formatted value to display.
 */
@Composable
fun Value(
    formattedValue: String
) {
    Text(
        text = stringResource(R.string.value_format, formattedValue),
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
