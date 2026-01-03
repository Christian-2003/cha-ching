package de.christian2003.chaching.plugin.presentation.view.transfer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
@Composable
fun TransferScreen(
    viewModel: TransferViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.type != null) {
                            if (viewModel.isCreating) {
                                stringResource(R.string.transfer_titleCreate, viewModel.type!!.name)
                            } else {
                                stringResource(R.string.transfer_titleEdit, viewModel.type!!.name)
                            }
                        } else { "" },
                        maxLines = 1,
                        overflow = TextOverflow.MiddleEllipsis
                    )
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
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //Scrollable content:
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
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
                IsSalaryCard(
                    isCreating = viewModel.isCreating,
                    isSalary = viewModel.isSalary,
                    onIsSalaryChange = {
                        viewModel.isSalary = it
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )
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
                    onValueChange = { input ->
                        val cleanupInput = input.filter { it.isDigit() || it == ',' || it == '.' }
                        viewModel.updateValue(cleanupInput)
                    },
                    label = stringResource(R.string.transfer_valueLabel),
                    errorMessage = viewModel.valueErrorMessage,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefixIcon = painterResource(R.drawable.ic_money),
                    suffixLabel = stringResource(R.string.transfer_valueSuffix),
                    visualTransformation = NumberFormatTransformation(),
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
            }


            //Button:
            Column(
                horizontalAlignment = Alignment.End
            ) {
                HorizontalDivider()
                Button(
                    onClick = {
                        viewModel.save()
                        onNavigateUp()
                    },
                    enabled = viewModel.isSavable,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.margin_horizontal),
                            vertical = dimensionResource(R.dimen.padding_vertical)
                        )
                ) {
                    Text(stringResource(R.string.button_save))
                }
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


@Composable
private fun IsSalaryCard(
    isCreating: Boolean,
    isSalary: Boolean,
    onIsSalaryChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val arrowAnimator by animateFloatAsState(
        targetValue = if (isSalary) { 0F } else { 180F },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = dimensionResource(R.dimen.padding_vertical))
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
        ) {
            SegmentedButton(
                selected = isSalary,
                onClick = {
                    onIsSalaryChange(true)
                },
                shape = RoundedCornerShape(
                    topStart = 100.dp,
                    topEnd = 0.dp,
                    bottomStart = 100.dp,
                    bottomEnd = 0.dp
                )
            ) {
                Text(stringResource(R.string.transfer_isSalary_incomeTitle))
            }
            SegmentedButton(
                selected = !isSalary,
                onClick = {
                    onIsSalaryChange(false)
                },
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 100.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 100.dp
                )
            ) {
                Text(stringResource(R.string.transfer_isSalary_expenseTitle))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_horizontal),
                    top = dimensionResource(R.dimen.padding_vertical),
                    end = dimensionResource(R.dimen.padding_horizontal)
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = stringResource(R.string.transfer_isSalary_budgetForecast),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = when {
                        isCreating && isSalary -> stringResource(R.string.transfer_isSalary_incomeTextCreate)
                        !isCreating && isSalary -> stringResource(R.string.transfer_isSalary_incomeTextEdit)
                        isCreating && !isSalary -> stringResource(R.string.transfer_isSalary_expenseTextCreate)
                        else -> stringResource(R.string.transfer_isSalary_expenseTextEdit)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.largeIncreased)
                    .background(
                        color = if (isSalary) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                    .padding(dimensionResource(R.dimen.padding_horizontal))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_wallet),
                    contentDescription = "",
                    tint = if (isSalary) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    },
                    modifier = Modifier.size(dimensionResource(R.dimen.image_s))
                )
                Icon(
                    painter = painterResource(R.drawable.ic_increase),
                    contentDescription = "",
                    tint = if (isSalary) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    },
                    modifier = Modifier
                        .offset(
                            x = dimensionResource(R.dimen.image_xs) / 4,
                            y = dimensionResource(R.dimen.image_xs) / 4
                        )
                        .clip(CircleShape)
                        .background(
                            color = if (isSalary) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                        .size(dimensionResource(R.dimen.image_xs))
                        .rotate(arrowAnimator)
                )
            }
        }
    }
}
