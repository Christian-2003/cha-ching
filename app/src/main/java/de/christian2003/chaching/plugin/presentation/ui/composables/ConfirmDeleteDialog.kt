package de.christian2003.chaching.plugin.presentation.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = ""
            )
        },
        title = {
            Text(
                text = when(options) {
                    ConfirmDeleteDialogOptions.Delete -> stringResource(R.string.confirm_delete)
                    ConfirmDeleteDialogOptions.MoveToTrash -> stringResource(R.string.confirm_moveToTrash)
                }
            )
        },
        text = {
            Text(text)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors().copy(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = when(options) {
                        ConfirmDeleteDialogOptions.Delete -> stringResource(R.string.button_delete)
                        ConfirmDeleteDialogOptions.MoveToTrash -> stringResource(R.string.button_confirm)
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    )
}


/**
 * Options for the dialog through which to confirm deletion.
 */
enum class ConfirmDeleteDialogOptions {
    Delete,
    MoveToTrash
}
