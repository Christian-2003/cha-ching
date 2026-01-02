package de.christian2003.chaching.plugin.presentation.view.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabTypeDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun AnalysisTypeSheet(
    options: DataTabOptions,
    valueColor: Color,
    precision: AnalysisPrecision,
    typeData: DataTabTypeDto,
    onDismiss: () -> Unit,
    onFormatValue: (Double) -> String,
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
                    modifier = Modifier.fillMaxSize()
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
                            }
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
                                .fillMaxWidth()
                                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
                        )
                    }

                    item {
                        Headline(
                            title = stringResource(R.string.analysis_types_diagramDifference)
                        )
                    }

                    item {
                        DataLineDiagram(
                            options = options,
                            diagram = typeData.differencesDiagram,
                            onFormatValue = onFormatValue,
                            onQueryType = { type },
                            showLegend = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dimensionResource(R.dimen.padding_vertical))
                        )
                    }
                }
            }
        }
    }
}
