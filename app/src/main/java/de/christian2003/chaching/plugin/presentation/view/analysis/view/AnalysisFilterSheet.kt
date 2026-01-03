package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.view.analysis.model.AnalysisFilter
import de.christian2003.chaching.plugin.presentation.view.analysis.model.AnalysisPeriod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset


@Composable
fun AnalysisFilterSheet(
    filter: AnalysisFilter,
    onDismiss: () -> Unit,
    onApply: (AnalysisFilter) -> Unit,
    onFormatDate: (LocalDate) -> String
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var period: AnalysisPeriod by remember { mutableStateOf(filter.period) }
    var precision: AnalysisPrecision? by remember { mutableStateOf(filter.precision) }
    var isDateRangePickerVisible: Boolean by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.analysis_filter_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                val filter = AnalysisFilter(
                                    period = period,
                                    precision = precision
                                )
                                onApply(filter)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.button_apply))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Headline(
                    title = stringResource(R.string.analysis_filter_timeSpan)
                )
                AnalysisPeriodSelector(
                    period = period,
                    onPeriodSelected = {
                        if (it == null) {
                            isDateRangePickerVisible = true
                        }
                        else {
                            period = it
                        }
                    },
                    onFormatDate = onFormatDate
                )

                Headline(
                    title = stringResource(R.string.analysis_filter_precision)
                )
                AnalysisPrecisionSelector(
                    precision = precision,
                    onPrecisionSelected = {
                        precision = it
                    }
                )
            }
        }
    }

    if (isDateRangePickerVisible) {
        DateRangePickerModal(
            selectedAnalysisPeriod = period,
            onPeriodSelected = {
                period = it
                isDateRangePickerVisible = false
            },
            onDismiss = {
                isDateRangePickerVisible = false
            },
            onFormatDate = onFormatDate
        )
    }
}


@Composable
private fun AnalysisPeriodSelector(
    period: AnalysisPeriod,
    onPeriodSelected: (AnalysisPeriod?) -> Unit,
    onFormatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    val lastLabel: String = if (period == AnalysisPeriod.CURRENT_YEAR || period == AnalysisPeriod.LAST_YEAR) {
        stringResource(R.string.analysis_filter_timeSpan_custom)
    } else {
        stringResource(R.string.analysis_filter_timeSpan_customValue, onFormatDate(period.startDate), onFormatDate(period.endDate))
    }
    ChipRow(
        chipLabels = listOf(
            stringResource(R.string.analysis_filter_timeSpan_currentYear),
            stringResource(R.string.analysis_filter_timeSpan_previousYear),
            lastLabel
        ),
        selectedChipIndex = when(period) {
            AnalysisPeriod.CURRENT_YEAR -> 0
            AnalysisPeriod.LAST_YEAR -> 1
            else -> 2
        },
        onSelectionChanged = { index ->
            when (index) {
                0 -> onPeriodSelected(AnalysisPeriod.CURRENT_YEAR)
                1 -> onPeriodSelected(AnalysisPeriod.LAST_YEAR)
                else -> onPeriodSelected(null)
            }
        },
        modifier = modifier
    )
}


@Composable
private fun AnalysisPrecisionSelector(
    precision: AnalysisPrecision?,
    onPrecisionSelected: (AnalysisPrecision?) -> Unit,
    modifier: Modifier = Modifier
) {
    ChipRow(
        chipLabels = listOf(
            stringResource(R.string.analysis_filter_precision_automatic),
            stringResource(R.string.analysis_filter_precision_month),
            stringResource(R.string.analysis_filter_precision_quarter),
            stringResource(R.string.analysis_filter_precision_year)
        ),
        selectedChipIndex = when (precision) {
            AnalysisPrecision.Month -> 1
            AnalysisPrecision.Quarter -> 2
            AnalysisPrecision.Year -> 3
            else -> 0
        },
        onSelectionChanged = { index ->
            when (index) {
                1 -> onPrecisionSelected(AnalysisPrecision.Month)
                2 -> onPrecisionSelected(AnalysisPrecision.Quarter)
                3 -> onPrecisionSelected(AnalysisPrecision.Year)
                else -> onPrecisionSelected(null)
            }
        },
        modifier = modifier
    )
}


@Composable
private fun ChipRow(
    chipLabels: List<String>,
    selectedChipIndex: Int,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
    ) {
        chipLabels.forEachIndexed { index, label ->
            FilterChip(
                selected = index == selectedChipIndex,
                onClick = {
                    onSelectionChanged(index)
                },
                label = {
                    Text(label)
                },
                modifier = Modifier.padding(
                    start = if (index != 0) { dimensionResource(R.dimen.padding_horizontal) } else { 0.dp }
                )
            )
        }
    }
}


/**
 * Displays a model date picker through which to select a date range for analysis.
 *
 * @param selectedAnalysisPeriod    Time period that is selected currently.
 * @param onPeriodSelected          Callback invoked once a new time period is selected.
 * @param onDismiss                 Callback invoked to close the dialog without selecting a new
 *                                  date range.
 * @param onFormatDate              Callback invoked to format a date.
 */
@Composable
private fun DateRangePickerModal(
    selectedAnalysisPeriod: AnalysisPeriod,
    onPeriodSelected: (AnalysisPeriod) -> Unit,
    onDismiss: () -> Unit,
    onFormatDate: (LocalDate) -> String
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDate = selectedAnalysisPeriod.startDate,
        initialSelectedEndDate = selectedAnalysisPeriod.endDate
    )

    val dateFormatter: DatePickerFormatter = object: DatePickerFormatter {
        override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
            return ""
        }

        override fun formatDate(dateMillis: Long?, locale: CalendarLocale, forContentDescription: Boolean): String? {
            val date: LocalDate = if (dateMillis != null) {
                LocalDate.ofInstant(Instant.ofEpochMilli(dateMillis), ZoneOffset.UTC)
            } else {
                LocalDate.now()
            }
            return onFormatDate(date)
        }

    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startDate: LocalDate? = dateRangePickerState.getSelectedStartDate() ?: selectedAnalysisPeriod.startDate
                    val endDate: LocalDate? = dateRangePickerState.getSelectedEndDate() ?: selectedAnalysisPeriod.endDate
                    if (startDate != null && endDate != null) {
                        val selectedPeriod = AnalysisPeriod(startDate, endDate)
                        onPeriodSelected(selectedPeriod)
                    }
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
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = dateRangePickerState.displayMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding( //Library has incorrect padding. So we need to override here manually!
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    dateFormatter = dateFormatter,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp) //Library has incorrect padding. So we need to override here manually!
                )
            }
        )
    }
}
