package de.christian2003.chaching.plugin.presentation.view.analysis

import android.util.Log
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import java.nio.file.WatchEvent


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
                        .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                ) {
                    Text("Finished")
                    LineDiagram(
                        diagram = viewModel.analysisResult!!.transfersByTypeDiagram,
                        curvedEdges = true,
                        indicatorBuilder = {
                            viewModel.buildIndicator(it)
                        }
                    )
                    LineDiagram(
                        diagram = viewModel.analysisResult!!.cumulatedTransfersByTypeDiagram,
                        curvedEdges = false,
                        indicatorBuilder = {
                            viewModel.buildIndicator(it)
                        }
                    )
                    Text("Totals")
                    ListByType(viewModel.analysisResult!!.totalTransferByType)
                    Text("Averages")
                    ListByType(viewModel.analysisResult!!.averageTransferByType)
                }
            }
        }
    }
}


@Composable
fun LineDiagram(
    diagram: AnalysisDiagram,
    curvedEdges: Boolean,
    indicatorBuilder: (Double) -> String
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
        labels.reverse()
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
            label = line.type?.name ?: "Other",
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
        modifier = Modifier
            .fillMaxWidth()
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
            .padding(bottom = 20.dp) //Always require bottom padding to make space for x-axis labels!
    ) {
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


@Composable
fun ListByType(map: Map<Type?, Int>) {
    map.forEach { (type, i) ->
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = type?.name ?: "Other"
            )
            Text(
                text = (i.toDouble() / 100).toString(),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            )
        }
    }
}
