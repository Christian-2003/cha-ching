package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.TypeShapes
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.view.analysis.AnalysisViewModel
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabDto
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabTypeDto
import java.util.UUID


/**
 * Tab for the analysis screen displays information about incomes OR expenses.
 *
 * @param viewModel     View model.
 * @param bottomPadding Bottom padding.
 * @param options       Data tab options indicating whether this tab displays incomes or expenses.
 */
@Composable
fun AnalysisDataTab(
    viewModel: AnalysisViewModel,
    bottomPadding: Dp,
    options: DataTabOptions
) {
    val analysisResult: LargeAnalysisResult? = viewModel.analysisResult
    if (analysisResult == null) {
        return
    }

    val data: DataTabDto = when (options) {
        DataTabOptions.Incomes -> viewModel.incomesTabData
        DataTabOptions.Expenses -> viewModel.expensesTabData
    }
    val precision: AnalysisPrecision = analysisResult.metadata.precision

    //Colors for the analysis:
    val valueColor: Color = when (options) {
        DataTabOptions.Incomes -> MaterialTheme.colorScheme.primary
        DataTabOptions.Expenses -> MaterialTheme.colorScheme.tertiary
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        item {
            OverviewCard(
                options = options,
                overview = data.overview,
                valueColor = valueColor,
                precision = precision,
                currentStart = analysisResult.currentSpan.start,
                currentEnd = analysisResult.currentSpan.end,
                previousStart = analysisResult.previousSpan.start,
                previousEnd = analysisResult.previousSpan.end,
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onFormatDate = {
                    viewModel.formatDate(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
            )
        }
        item {
            Headline(
                title = when (options) {
                    DataTabOptions.Incomes -> when (precision) {
                        AnalysisPrecision.Month -> stringResource(R.string.analysis_incomes_monthDiagramValues)
                        AnalysisPrecision.Quarter -> stringResource(R.string.analysis_incomes_quarterDiagramValues)
                        AnalysisPrecision.Year -> stringResource(R.string.analysis_incomes_yearDiagramValues)
                    }
                    DataTabOptions.Expenses -> when (precision) {
                        AnalysisPrecision.Month -> stringResource(R.string.analysis_expenses_monthDiagramValues)
                        AnalysisPrecision.Quarter -> stringResource(R.string.analysis_expenses_quarterDiagramValues)
                        AnalysisPrecision.Year -> stringResource(R.string.analysis_expenses_yearDiagramValues)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        item {
            DataLineDiagram(
                options = options,
                diagram = data.valuesDiagram,
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onQueryType = {
                    viewModel.queryType(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
            )
        }
        item {
            Headline(
                title = when (options) {
                    DataTabOptions.Incomes -> when (precision) {
                        AnalysisPrecision.Month -> stringResource(R.string.analysis_incomes_monthDiagramCumulated)
                        AnalysisPrecision.Quarter -> stringResource(R.string.analysis_incomes_quarterDiagramCumulated)
                        AnalysisPrecision.Year -> stringResource(R.string.analysis_incomes_yearDiagramCumulated)
                    }
                    DataTabOptions.Expenses -> when (precision) {
                        AnalysisPrecision.Month -> stringResource(R.string.analysis_expenses_monthDiagramCumulated)
                        AnalysisPrecision.Quarter -> stringResource(R.string.analysis_expenses_quarterDiagramCumulated)
                        AnalysisPrecision.Year -> stringResource(R.string.analysis_expenses_yearDiagramCumulated)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        item {
            DataLineDiagram(
                options = options,
                diagram = data.cumulatedDiagram,
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onQueryType = {
                    viewModel.queryType(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
            )
        }
        item {
            Headline(
                title = stringResource(R.string.analysis_data_typesListTitle),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    ))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
        itemsIndexed(data.types) { index, type ->
            TypeResultItem(
                typeResult = type,
                valueColor = valueColor,
                isFirst = index == 0,
                isLast = index == data.types.size - 1,
                onClick = {
                    viewModel.displayType(type)
                },
                onFormatValue = {
                    viewModel.formatValue(it)
                },
                onQueryType = {
                    viewModel.queryType(it)
                }
            )
        }
        item {
            Box(
                modifier = Modifier.height(bottomPadding)
            )
        }
    }

    val displayedTypeInfo: DataTabTypeDto? = viewModel.displayedTypeInfo
    if (displayedTypeInfo != null) {
        val transfers: List<Transfer> by viewModel.transfersOfDisplayedType.collectAsState(emptyList())
        AnalysisTypeSheet(
            options = options,
            valueColor = valueColor,
            precision = precision,
            typeData = displayedTypeInfo,
            currentStart = analysisResult.currentSpan.start,
            currentEnd = analysisResult.currentSpan.end,
            previousStart = analysisResult.previousSpan.start,
            previousEnd = analysisResult.previousSpan.end,
            transfers = transfers,
            onDismiss = {
                viewModel.dismissDisplayedType()
            },
            onFormatValue = {
                viewModel.formatValue(it)
            },
            onFormatTransferValue = {
                viewModel.formatValue(it)
            },
            onFormatDate = {
                viewModel.formatDate(it)
            },
            onQueryType = {
                viewModel.queryType(it)
            }
        )
    }
}


/**
 * Item displaying the result for a single type.
 *
 * @param typeResult    Type result to display.
 * @param valueColor    Color with which to display the value.
 * @param isFirst       Whether this is the first item in the list.
 * @param isLast        Whether this is the last item in the list.
 * @param onClick       Callback invoked once the item is clicked.
 * @param onFormatValue Callback invoked to format a value.
 * @param onQueryType   Callback invoked to query a type by it's ID.
 * @param modifier      Modifier.
 */
@Composable
private fun TypeResultItem(
    typeResult: DataTabTypeDto,
    valueColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onFormatValue: (Double) -> String,
    onQueryType: suspend (UUID) -> Type?,
    modifier: Modifier = Modifier
) {
    val type: Type? by produceState(null) {
        value = onQueryType(typeResult.typeId)
    }
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            if (type != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                        .size(dimensionResource(R.dimen.image_m))
                ) {
                    Shape(
                        shape = TypeShapes.getShapeForTypeIcon(type!!.icon).shape,
                        color = TypeShapes.getShapeColor(type!!.icon)
                    )
                    Icon(
                        painter = painterResource(type!!.icon.drawableResourceId),
                        contentDescription = "",
                        tint = TypeShapes.getOnShapeColor(type!!.icon),
                        modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Text(
                    text = type?.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pluralStringResource(R.plurals.analysis_data_typeListTransferCount, typeResult.overview.transferCount, typeResult.overview.transferCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = onFormatValue(typeResult.overview.sum),
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        }
    }
}
