package de.christian2003.chaching.plugin.presentation.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.chaching.R


/**
 * Displays a dialog asking the user for confirmation to delete an item.
 *
 * @param text      Information text to display to the user.
 * @param onDismiss Dismiss the dialog without deleting the item.
 * @param onConfirm Delete the item and dismiss the dialog.
 * @param options   Options for the dialog.
 */
@Composable
fun ConfirmDeleteDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    options: ConfirmDeleteDialogOptions = ConfirmDeleteDialogOptions.Delete
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                //Title:
                Text(
                    text = when(options) {
                        ConfirmDeleteDialogOptions.Delete -> stringResource(R.string.confirm_delete)
                        ConfirmDeleteDialogOptions.MoveToTrash -> stringResource(R.string.confirm_moveToTrash)
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )

                //Text:
                Text(
                    text = text,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 24.dp
                    )
                )

                //Buttons:
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(
                        onClick = onConfirm,
                        colors = ButtonDefaults.textButtonColors().copy(
                            contentColor = when(options) {
                                ConfirmDeleteDialogOptions.Delete -> MaterialTheme.colorScheme.error
                                ConfirmDeleteDialogOptions.MoveToTrash -> ButtonDefaults.textButtonColors().contentColor
                            }
                        )
                    ) {
                        Text(
                            text = when(options) {
                                ConfirmDeleteDialogOptions.Delete -> stringResource(R.string.button_delete)
                                ConfirmDeleteDialogOptions.MoveToTrash -> stringResource(R.string.button_confirm)
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 * Options for the dialog through which to confirm deletion.
 */
enum class ConfirmDeleteDialogOptions {
    Delete,
    MoveToTrash
}
