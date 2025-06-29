package de.christian2003.chaching.plugin.presentation.view.analysis

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.AnalysisDiagram
import de.christian2003.chaching.domain.analysis.AnalysisPrecision
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.Value
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlinx.coroutines.selects.select
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.analysis_title))
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
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (viewModel.analysisResult == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadingIndicator()
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AnalysisPeriodSelector(
                        analysisPeriod = viewModel.analysisPeriod,
                        onAnalysisPeriodChange = { period ->
                            if (period != null) {
                                viewModel.startAnalysis(period)
                            }
                            else {
                                viewModel.showDatePeriodPickerDialog = true
                            }
                        },
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                    )
                    TotalOverview(
                        formattedTotal = viewModel.buildIndicator(viewModel.analysisResult!!.total.toDouble() / 100.0),
                        formattedAverage = viewModel.buildIndicator(viewModel.analysisResult!!.average.toDouble() / 100.0),
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                    )
                    TransfersByTypeOverview(
                        title = stringResource(R.string.analysis_transfersByType),
                        transfersByType = viewModel.analysisResult!!.totalTransferByType,
                        formatValue = { value ->
                            viewModel.buildIndicator(value)
                        },
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                    )
                    LineDiagram(
                        title = stringResource(R.string.analysis_diagram_transfers, when (viewModel.analysisResult!!.transfersByTypeDiagram.precision) {
                            AnalysisPrecision.MONTH -> stringResource(R.string.analysis_precision_month)
                            AnalysisPrecision.QUARTER -> stringResource(R.string.analysis_precision_quarter)
                            AnalysisPrecision.YEAR -> stringResource(R.string.analysis_precision_year)
                        }),
                        diagram = viewModel.analysisResult!!.transfersByTypeDiagram,
                        curvedEdges = true,
                        indicatorBuilder = {
                            viewModel.buildIndicator(it)
                        },
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                    )
                    LineDiagram(
                        title = stringResource(R.string.analysis_diagram_cumulated, when (viewModel.analysisResult!!.transfersByTypeDiagram.precision) {
                            AnalysisPrecision.MONTH -> stringResource(R.string.analysis_precision_month)
                            AnalysisPrecision.QUARTER -> stringResource(R.string.analysis_precision_quarter)
                            AnalysisPrecision.YEAR -> stringResource(R.string.analysis_precision_year)
                        }),
                        diagram = viewModel.analysisResult!!.cumulatedTransfersByTypeDiagram,
                        curvedEdges = false,
                        indicatorBuilder = {
                            viewModel.buildIndicator(it)
                        },
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                    )
                }
            }
        }
        if (viewModel.showDatePeriodPickerDialog) {
            DateRangePickerModal(
                selectedAnalysisPeriod = viewModel.analysisPeriod,
                onPeriodSelected = { period ->
                    viewModel.showDatePeriodPickerDialog = false
                    viewModel.startAnalysis(period)
                },
                onDismiss = {
                    viewModel.showDatePeriodPickerDialog = false
                }
            )
        }
    }
}


@Composable
private fun AnalysisPeriodSelector(
    analysisPeriod: AnalysisPeriod,
    onAnalysisPeriodChange: (AnalysisPeriod?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        FilterChip(
            selected = analysisPeriod == AnalysisPeriod.CURRENT_YEAR,
            onClick = {
                if (analysisPeriod != AnalysisPeriod.CURRENT_YEAR) {
                    onAnalysisPeriodChange(AnalysisPeriod.CURRENT_YEAR)
                }
            },
            label = {
                Text(stringResource(R.string.analysis_period_currentYear))
            },
            modifier = Modifier.padding(start = dimensionResource(R.dimen.margin_horizontal))
        )
        FilterChip(
            selected = analysisPeriod == AnalysisPeriod.LAST_YEAR,
            onClick = {
                if (analysisPeriod != AnalysisPeriod.LAST_YEAR) {
                    onAnalysisPeriodChange(AnalysisPeriod.LAST_YEAR)
                }
            },
            label = {
                Text(stringResource(R.string.analysis_period_lastYear))
            },
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
        )
        FilterChip(
            selected = analysisPeriod != AnalysisPeriod.CURRENT_YEAR && analysisPeriod != AnalysisPeriod.LAST_YEAR,
            onClick = {
                onAnalysisPeriodChange(null)
            },
            label = {
                Text(if (analysisPeriod != AnalysisPeriod.CURRENT_YEAR && analysisPeriod != AnalysisPeriod.LAST_YEAR) {
                    stringResource(R.string.analysis_period_customValue, analysisPeriod.startDate.format(dateFormatter), analysisPeriod.endDate.format(dateFormatter))
                } else {
                    stringResource(R.string.analysis_period_custom)
                })
            },
            modifier = Modifier.padding(end = dimensionResource(R.dimen.margin_horizontal))
        )
    }
}


@Composable
private fun TotalOverview(
    formattedTotal: String,
    formattedAverage: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
    ) {
        TotalValueBox(
            title = stringResource(R.string.analysis_total),
            formattedValue = formattedTotal,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.padding_horizontal))
        )
        TotalValueBox(
            title = stringResource(R.string.analysis_average),
            formattedValue = formattedAverage,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}


@Composable
private fun TotalValueBox(
    title: String,
    formattedValue: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )
        Value(formattedValue)
    }
}


@Composable
fun TransfersByTypeOverview(
    title: String,
    transfersByType: Map<Type?, Int>,
    formatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        var i = 0
        transfersByType.forEach { (type, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_vertical))
            ) {
                if (type != null) {
                    Icon(
                        painter = painterResource(type.icon.drawableResourceId),
                        contentDescription = "",
                        modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                    )
                    Text(
                        text = type.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f).padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                    )
                }
                else {
                    Box(modifier = Modifier.size(dimensionResource(R.dimen.image_xs)))
                    Text(
                        text = stringResource(R.string.analysis_other),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f).padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                    )
                }
                Text(
                    text = stringResource(R.string.value_format, formatValue(value.toDouble() / 100.0)),
                    color = when (i) {
                        0 -> MaterialTheme.colorScheme.primary
                        1 -> MaterialTheme.colorScheme.secondary
                        2 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.inverseOnSurface
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            i++
        }
    }
}


@Composable
fun LineDiagram(
    title: String,
    diagram: AnalysisDiagram,
    curvedEdges: Boolean,
    indicatorBuilder: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val labels: MutableList<String> = remember { mutableListOf() }
    if (labels.isEmpty()) {
        diagram.lines[0].data.forEach { analysisItem ->
            labels.add(
                when (diagram.precision) {
                    AnalysisPrecision.MONTH -> stringArrayResource(R.array.months)[analysisItem.date.month.value - 1]
                    AnalysisPrecision.QUARTER -> when(analysisItem.date.month.value) {
                        1 -> stringArrayResource(R.array.quarters)[0]
                        4 -> stringArrayResource(R.array.quarters)[1]
                        7 -> stringArrayResource(R.array.quarters)[2]
                        else -> stringArrayResource(R.array.quarters)[3]
                    }
                    AnalysisPrecision.YEAR -> analysisItem.date.year.toString()
                }
            )
        }
    }
    val data: MutableList<Line> = mutableListOf()
    var lineNumber = 0
    diagram.lines.forEach { line ->
        val values: MutableList<Double> = mutableListOf()
        line.data.forEach { analysisItem ->
            values.add(analysisItem.value.toDouble() / 100)
        }
        val color: Brush = SolidColor(when (lineNumber++) {
            0 -> MaterialTheme.colorScheme.primary
            1 -> MaterialTheme.colorScheme.secondary
            2 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.inverseOnSurface
        })
        val diagramLine = Line(
            label = line.type?.name ?: stringResource(R.string.analysis_other),
            values = values,
            color = color,
            curvedEdges = curvedEdges,
            dotProperties = DotProperties(
                enabled = true,
                color = SolidColor(MaterialTheme.colorScheme.surface),
                strokeWidth = 2.dp,
                radius = 2.dp,
                strokeColor = color
            )
        )
        data.add(diagramLine)
        Log.d("Analysis", "Line data points: ${values.size}")
    }
    Log.d("Analysis", "Lines: ${data.size}")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
            .padding(bottom = 48.dp) //Always require bottom padding to make space for x-axis labels!
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
        )
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            data = data,
            labelHelperProperties = LabelHelperProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            ),
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                labels = labels,
                rotation = LabelProperties.Rotation(LabelProperties.Rotation.Mode.Force, degree = -45f)
            ),
            popupProperties = PopupProperties(
                enabled = true,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                contentBuilder = indicatorBuilder
            ),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                contentBuilder = indicatorBuilder
            ),
            gridProperties = GridProperties(
                xAxisProperties = GridProperties.AxisProperties(
                    enabled = true,
                    color = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                    thickness = 1.dp
                ),
                yAxisProperties = GridProperties.AxisProperties(
                    enabled = true,
                    color = SolidColor(MaterialTheme.colorScheme.outlineVariant),
                    thickness = 1.dp,
                    lineCount = diagram.lines[0].data.size
                )
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerModal(
    selectedAnalysisPeriod: AnalysisPeriod,
    onPeriodSelected: (AnalysisPeriod) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDate = selectedAnalysisPeriod.startDate,
        initialSelectedEndDate = selectedAnalysisPeriod.endDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startDate: LocalDate? = dateRangePickerState.getSelectedStartDate()!!
                    val endDate: LocalDate? = dateRangePickerState.getSelectedEndDate()!!
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
            state = dateRangePickerState
        )
    }
}
