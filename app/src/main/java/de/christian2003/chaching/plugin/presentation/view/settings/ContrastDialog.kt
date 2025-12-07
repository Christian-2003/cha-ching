package de.christian2003.chaching.plugin.presentation.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.Value
import de.christian2003.chaching.plugin.presentation.ui.theme.ChaChingTheme
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * Dialog through which the user can select a theme contrast.
 *
 * @param contrast  Contrast that is currently selected.
 * @param onDismiss Callback invoked to dismiss the dialog without saving.
 * @param onSave    Callback invoked to dismiss the dialog and save a theme.
 */
@Composable
fun ContrastDialog(
    contrast: ThemeContrast,
    onDismiss: () -> Unit,
    onSave: (ThemeContrast) -> Unit
) {
    var mutableContrast: ThemeContrast by rememberSaveable { mutableStateOf(contrast) }

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
            ) {
                val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                val numberFormat: NumberFormat = DecimalFormat("#,###.00")
                //This material theme applies the selected contrast to the preview:
                ChaChingTheme(
                    contrast = mutableContrast
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(
                                start = 24.dp,
                                top = 24.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.settings_customization_contrastDialog_previewTitle),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                            )
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings_customization_contrastDialog_previewText),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = LocalDate.now().format(formatter),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Value(
                                    numberFormat.format(1234.56)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()

                Text(
                    text = stringResource(R.string.settings_customization_contrastDialog_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    ThemeContrast.entries.forEach { contrast ->
                        SegmentedButton(
                            selected = contrast == mutableContrast,
                            onClick = {
                                mutableContrast = contrast
                            },
                            shape = RoundedCornerShape(
                                topStart = if (contrast.ordinal == 0) { 100.dp } else { 0.dp },
                                topEnd = if (contrast.ordinal == ThemeContrast.entries.size - 1) { 100.dp } else { 0.dp },
                                bottomStart = if (contrast.ordinal == 0) { 100.dp } else { 0.dp },
                                bottomEnd = if (contrast.ordinal == ThemeContrast.entries.size - 1) { 100.dp } else { 0.dp }
                            )
                        ) {
                            Text(
                                text = when(contrast) {
                                    ThemeContrast.Normal -> stringResource(R.string.settings_customization_contrastDialog_normal)
                                    ThemeContrast.Medium -> stringResource(R.string.settings_customization_contrastDialog_medium)
                                    ThemeContrast.High -> stringResource(R.string.settings_customization_contrastDialog_high)
                                }
                            )
                        }
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            horizontal = 24.dp,
                            vertical = 16.dp,
                        )
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            onSave(mutableContrast)
                        },
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                }
            }
        }
    }
}
