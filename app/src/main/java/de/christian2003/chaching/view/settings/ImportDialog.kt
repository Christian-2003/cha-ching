package de.christian2003.chaching.view.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import de.christian2003.chaching.R
import de.christian2003.chaching.model.backup.ImportStrategy


/**
 * Displays a dialog through which the user can configure what happens with the data currently stored
 * in the app, once the user restores a backup.
 *
 * @param onDismiss Callback invoked to dismiss the dialog without any action.
 * @param onConfirm Callback invoked once the user selects an option.
 */
@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onConfirm: (ImportStrategy) -> Unit
) {
    var selectedImportStrategy: ImportStrategy by remember { mutableStateOf(ImportStrategy.DELETE_EXISTING_DATA) }

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_import),
                contentDescription = ""
            )
        },
        title = {
            Text(stringResource(R.string.settings_data_importDialog_title))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.settings_data_importDialog_text))
                ImportStrategy.entries.forEach { importStrategy ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = dimensionResource(R.dimen.padding_vertical))
                            .clickable {
                                selectedImportStrategy = importStrategy
                            }
                    ) {
                        RadioButton(
                            selected = importStrategy == selectedImportStrategy,
                            onClick = {
                                selectedImportStrategy = importStrategy
                            }
                        )
                        Column {
                            Text(
                                text = stringResource(importStrategy.titleStringRes),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = stringResource(importStrategy.infoStringRes),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedImportStrategy)
                }
            ) {
                Text(stringResource(R.string.button_import))
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
