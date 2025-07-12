package de.christian2003.chaching.plugin.presentation.view.transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import de.christian2003.chaching.plugin.presentation.ui.composables.TextInput
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


/**
 * Displays the screen through which a transfer can be configured.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: TransferViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (viewModel.isCreating) { stringResource(R.string.transfer_titleCreate) } else { stringResource(R.string.transfer_titleEdit) })
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
                .fillMaxWidth()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.transfer_help),
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(
                        bottom = dimensionResource(R.dimen.padding_vertical)
                    )
                )
            }
            TextInput(
                value = viewModel.valueDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                onValueChange = { },
                label = stringResource(R.string.transfer_valueDateLabel),
                keyboardOptions = KeyboardOptions(showKeyboardOnFocus = false),
                prefixIcon = painterResource(R.drawable.ic_date),
                modifier = Modifier.pointerInput(null) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            viewModel.isDatePickerVisible = true
                        }
                    }
                }
            )
            TextInput(
                value = viewModel.value,
                onValueChange = {
                    viewModel.updateValue(it)
                },
                label = stringResource(R.string.transfer_valueLabel),
                errorMessage = viewModel.valueErrorMessage,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefixIcon = painterResource(R.drawable.ic_money),
                suffixLabel = stringResource(R.string.transfer_valueSuffix),
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
            if (viewModel.isHoursWorkedEditable) {
                TextInput(
                    value = viewModel.hoursWorked,
                    onValueChange = {
                        viewModel.updateHoursWorked(it)
                    },
                    label = stringResource(R.string.transfer_hoursWorkedLabel),
                    errorMessage = viewModel.hoursWorkedErrorMessage,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    prefixIcon = painterResource(R.drawable.ic_time),
                    suffixLabel = stringResource(R.string.transfer_hoursWorkedSuffix),
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
                )
            }
            Button(
                onClick = {
                    viewModel.save()
                    onNavigateUp()
                },
                enabled = viewModel.isSavable,
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.padding_vertical))
                    .align(Alignment.End)
            ) {
                Text(stringResource(R.string.button_save))
            }
        }
        if (viewModel.isDatePickerVisible) {
            DatePickerModal(
                selectedMillis = viewModel.valueDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                onDateSelected = { selectedMillis ->
                    if (selectedMillis != null) {
                        viewModel.valueDate = Instant.ofEpochMilli(selectedMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    viewModel.isDatePickerVisible = false
                },
                onDismiss = {
                    viewModel.isDatePickerVisible = false
                }
            )
        }
    }
}


/**
 * Displays a modal date picker.
 *
 * @param selectedMillis    Milliseconds of the date selected.
 * @param onDateSelected    Callback invoked once a date is selected.
 * @param onDismiss         Callback invoked to dismiss the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    selectedMillis: Long,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    datePickerState.selectedDateMillis = selectedMillis
    datePickerState.displayedMonthMillis = selectedMillis

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                }
            ) {
                Text(stringResource(R.string.button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}
