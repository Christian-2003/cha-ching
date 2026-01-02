package de.christian2003.chaching.plugin.presentation.view.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.ChartColorGenerator
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.chart.ColumnChart
import de.christian2003.chaching.plugin.presentation.ui.theme.isDarkTheme
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabTypeDto
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DiagramDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID


@Composable
fun AnalysisTypeSheet(
    options: DataTabOptions,
    valueColor: Color,
    precision: AnalysisPrecision,
    typeData: DataTabTypeDto,
    transfers: List<Transfer>,
    onDismiss: () -> Unit,
    onFormatValue: (Double) -> String,
    onFormatTransferValue: (TransferValue) -> String,
    onFormatDate: (LocalDate) -> String,
    onQueryType: suspend (UUID) -> Type?
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val type: Type? by produceState(null) {
        value = onQueryType(typeData.typeId)
    }

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
                        text = if (type != null) {
                            type!!.name
                        } else {
                            ""
                        },
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
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )

            HorizontalDivider()

            if (type == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadingIndicator()
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    item {
                        OverviewCard(
                            options = options,
                            overview = typeData.overview,
                            valueColor = valueColor,
                            precision = precision,
                            onFormatValue = onFormatValue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(
                                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                                    vertical = dimensionResource(R.dimen.padding_vertical)
                                )
                        )
                    }

                    item {
                        Headline(
                            title = when (precision) {
                                AnalysisPrecision.Month -> stringResource(R.string.analysis_types_monthDiagramValues, type!!.name)
                                AnalysisPrecision.Quarter -> stringResource(R.string.analysis_types_quarterDiagramValues, type!!.name)
                                AnalysisPrecision.Year -> stringResource(R.string.analysis_types_yearDiagramValues, type!!.name)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        )
                    }

                    item {
                        DataLineDiagram(
                            options = options,
                            diagram = typeData.valuesDiagram,
                            onFormatValue = onFormatValue,
                            onQueryType = { type },
                            showLegend = false,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .fillMaxWidth()
                                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
                        )
                    }

                    item {
                        Headline(
                            title = stringResource(R.string.analysis_types_diagramDifference),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        )
                    }

                    item {
                        DifferenceDataLineDiagram(
                            options = options,
                            diagram = typeData.differencesDiagram,
                            onFormatValue = onFormatValue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
                        )
                    }

                    item {
                        Headline(
                            title = stringResource(R.string.analysis_types_transfers),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .clip(RoundedCornerShape(
                                    topStart = 24.dp,
                                    topEnd = 24.dp
                                ))
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        )
                    }

                    itemsIndexed(transfers) { index, transfer ->
                        TransferListItem(
                            transfer = transfer,
                            isFirst = index == 0,
                            isLast = index == transfers.size - 1,
                            valueColor = valueColor,
                            onFormatValue = onFormatValue,
                            onFormatTransferValue = onFormatTransferValue,
                            onFormatDate = onFormatDate
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DifferenceDataLineDiagram(
    options: DataTabOptions,
    diagram: DiagramDto,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    if (diagram.chartColumns.isEmpty()) {
        return
    }

    val colorGenerator = ChartColorGenerator()
    val positiveColors: List<Color> = listOf(colorGenerator.generatePositiveColorFromNegativeColor(
        negative = MaterialTheme.colorScheme.error,
        darkTheme = MaterialTheme.isDarkTheme()
    ))
    val negativeColors: List<Color> = listOf(MaterialTheme.colorScheme.error)

    ColumnChart(
        columns = diagram.chartColumns,
        positiveColors = when (options) {
            DataTabOptions.Incomes -> positiveColors
            DataTabOptions.Expenses -> negativeColors
        },
        negativeColors = when (options) {
            DataTabOptions.Incomes -> negativeColors
            DataTabOptions.Expenses -> positiveColors
        },
        onFormatValue = onFormatValue,
        modifier = modifier.fillMaxWidth()
    )
}


@Composable
private fun TransferListItem(
    transfer: Transfer,
    isFirst: Boolean,
    isLast: Boolean,
    valueColor: Color,
    onFormatValue: (Double) -> String,
    onFormatTransferValue: (TransferValue) -> String,
    onFormatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = onFormatDate(transfer.transferValue.date),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (transfer.transferValue.value > 0 && transfer.hoursWorked > 0) {
                    val payPerHour: Double = ((transfer.transferValue.value.toDouble() / 100.0) / transfer.hoursWorked)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(end = dimensionResource(R.dimen.padding_horizontal) / 2)
                                .size(dimensionResource(R.dimen.image_xxs))
                        )
                        Text(
                            text = stringResource(R.string.analysis_types_payPerHour, onFormatValue(payPerHour)),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Text(
                text = onFormatTransferValue(transfer.transferValue),
                color = valueColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
