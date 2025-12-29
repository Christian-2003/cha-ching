package de.christian2003.chaching.plugin.presentation.view.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisData
import de.christian2003.chaching.domain.analysis.small.SmallTypeResult
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.model.ChartColorGenerator
import de.christian2003.chaching.plugin.presentation.model.TypeShapes
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import de.christian2003.chaching.plugin.presentation.ui.theme.isDarkTheme
import java.util.UUID
import kotlin.math.abs


/**
 * Composable displays the small analysis result on the main screen.
 *
 * @param smallAnalysisResult   Small analysis result to display.
 * @param onQueryType           Callback invoked to query a type by it's ID.
 * @param onFormatValue         Callback invoked to format a value.
 * @param modifier              Modifier.
 */
@Composable
fun SmallAnalysisOverview(
    smallAnalysisResult: SmallAnalysisResult,
    onQueryType: suspend (UUID) -> Type?,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            SmallAnalysisBudgetOverview(
                smallAnalysisResult = smallAnalysisResult,
                onFormatValue = onFormatValue,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.5f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            )
            SmallAnalysisBudgetDifferenceToLastMonth(
                smallAnalysisResult = smallAnalysisResult,
                onFormatValue = onFormatValue,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        if (smallAnalysisResult.currentMonth.incomes.totalSum > 0.0) {
            SmallAnalysisDataOverview(
                analysisData = smallAnalysisResult.currentMonth.incomes,
                previousAnalysisData = smallAnalysisResult.previousMonth.incomes,
                isSalary = true,
                onQueryType = onQueryType,
                onFormatValue = onFormatValue,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
        if (smallAnalysisResult.currentMonth.expenses.totalSum > 0.0) {
            SmallAnalysisDataOverview(
                analysisData = smallAnalysisResult.currentMonth.expenses,
                previousAnalysisData = smallAnalysisResult.previousMonth.expenses,
                isSalary = false,
                onQueryType = onQueryType,
                onFormatValue = onFormatValue,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
    }
}


/**
 * Displays the budget overview.
 *
 * @param smallAnalysisResult   Small analysis result whose budget to display.
 * @param onFormatValue         Callback invoked to format a value.
 * @param modifier              Modifier.
 */
@Composable
private fun SmallAnalysisBudgetOverview(
    smallAnalysisResult: SmallAnalysisResult,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = onFormatValue(smallAnalysisResult.currentMonth.budget),
            color = when {
                smallAnalysisResult.currentMonth.budget > 0 -> MaterialTheme.colorScheme.primary
                smallAnalysisResult.currentMonth.budget < 0 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            },
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.main_analysis_budgetLabel),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
        if (smallAnalysisResult.currentMonth.budget > 0) {
            Text(
                text = smallAnalysisResult.overviewComparisonConnection.getLocalizedString(
                    context = LocalContext.current,
                    value = smallAnalysisResult.currentMonth.budget
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
    }
}


/**
 * Composable displays the budget difference from this to the last month.
 *
 * @param smallAnalysisResult   Small analysis result whose budget difference to display.
 * @param onFormatValue         Callback invoked to format a value.
 * @param modifier              Modifier.
 */
@Composable
private fun SmallAnalysisBudgetDifferenceToLastMonth(
    smallAnalysisResult: SmallAnalysisResult,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val differenceToPreviousMonth: Double = smallAnalysisResult.currentMonth.budget - smallAnalysisResult.previousMonth.budget
    val foregroundColor: Color = when {
        (differenceToPreviousMonth > 0.0) -> MaterialTheme.colorScheme.primary
        (differenceToPreviousMonth < 0.0) -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(when {
                (differenceToPreviousMonth > 0.0) -> MaterialTheme.colorScheme.primaryContainer
                (differenceToPreviousMonth < 0.0) -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceContainer
            })
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(dimensionResource(R.dimen.image_l))
        ) {
            Shape(
                shape = MaterialShapes.Clover8Leaf,
                color = MaterialTheme.colorScheme.surface
            )
            Icon(
                painter = painterResource(R.drawable.ic_increase),
                contentDescription = "",
                tint = foregroundColor,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_s))
                    .rotate(if (differenceToPreviousMonth > 0.0) { 0f } else if (differenceToPreviousMonth < 0.0) { 180f } else { 90f })
            )
        }
        Text(
            text = if (differenceToPreviousMonth > 0) {
                AnnotatedString.fromHtml(stringResource(R.string.main_analysis_upFromLastMonthLabel, onFormatValue(differenceToPreviousMonth)))
            } else if (differenceToPreviousMonth < 0) {
                AnnotatedString.fromHtml(stringResource(R.string.main_analysis_downFromLastMonthLabel, onFormatValue(abs(differenceToPreviousMonth))))
            } else {
                AnnotatedString.fromHtml(stringResource(R.string.main_analysis_noDiffToLastMonthLabel))
            },
            color = foregroundColor,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
        )
    }
}


/**
 * Displays the small analysis data (i.e. the incomes OR expenses).
 *
 * @param analysisData          Analysis data of the current month to display.
 * @param previousAnalysisData  Analysis data from the previous month.
 * @param isSalary              Whether the composable displays incomes or expenses.
 * @param onQueryType           Callback invoked to query a type based on it's ID.
 * @param modifier              Modifier.
 */
@Composable
private fun SmallAnalysisDataOverview(
    analysisData: SmallAnalysisData,
    previousAnalysisData: SmallAnalysisData,
    isSalary: Boolean,
    onQueryType: suspend (UUID) -> Type?,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val colorGenerator = ChartColorGenerator()
    val colors: List<Color> = colorGenerator.generateChartColors(
        primary = if (isSalary) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.tertiary
       },
        darkTheme = MaterialTheme.isDarkTheme()
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Text(
                text = onFormatValue(analysisData.totalSum),
                color = if (isSalary) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.tertiary },
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (isSalary) {
                    stringResource(R.string.main_analysis_incomesLabel)
                } else {
                    stringResource(R.string.main_analysis_expensesLabel)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
            )

            SmallAnalysisDataDiagram(
                analysisData = analysisData,
                colors = colors
            )
            analysisData.typeResults.forEachIndexed { index, typeResult ->
                SmallTypeResultOverview(
                    typeResult = typeResult,
                    index = index,
                    colors = colors,
                    onQueryType = onQueryType,
                    onFormatValue = onFormatValue
                )
            }

            SmallAnalysisDataDifferenceToPreviousMonth(
                analysisData = analysisData,
                previousAnalysisData = previousAnalysisData,
                isSalary = isSalary,
                onFormatValue = onFormatValue,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
    }
}


/**
 * Displays the difference between the current month and the previous month for the analysis data
 * (i.e. either incomes OR expenses).
 *
 * @param analysisData          Analysis data for the current month.
 * @param previousAnalysisData  Analysis data for the previous month.
 * @param isSalary              Whether the analysis data is incomes or expenses.
 * @param onFormatValue         Callback invoked to format a value.
 * @param modifier              Modifier.
 */
@Composable
private fun SmallAnalysisDataDifferenceToPreviousMonth(
    analysisData: SmallAnalysisData,
    previousAnalysisData: SmallAnalysisData,
    isSalary: Boolean,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val differenceToPreviousMonth: Double = analysisData.totalSum - previousAnalysisData.totalSum
    val foregroundColor: Color = when {
        (differenceToPreviousMonth > 0.0) && isSalary -> MaterialTheme.colorScheme.onPrimaryContainer
        (differenceToPreviousMonth < 0.0) && isSalary -> MaterialTheme.colorScheme.onErrorContainer
        (differenceToPreviousMonth > 0.0) && !isSalary -> MaterialTheme.colorScheme.onErrorContainer
        (differenceToPreviousMonth < 0.0) && !isSalary -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraExtraLarge)
            .background(when {
                (differenceToPreviousMonth > 0.0) && isSalary -> MaterialTheme.colorScheme.primaryContainer
                (differenceToPreviousMonth < 0.0) && isSalary -> MaterialTheme.colorScheme.errorContainer
                (differenceToPreviousMonth > 0.0) && !isSalary -> MaterialTheme.colorScheme.errorContainer
                (differenceToPreviousMonth < 0.0) && !isSalary -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainer
            })
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    start = 4.dp,
                    top = 4.dp,
                    end = dimensionResource(R.dimen.padding_horizontal),
                    bottom = 4.dp
                )
                .size(dimensionResource(R.dimen.image_m))
        ) {
            Shape(
                shape = if (isSalary) { MaterialShapes.VerySunny } else { MaterialShapes.SoftBurst },
                color = MaterialTheme.colorScheme.surface
            )
            Icon(
                painter = painterResource(R.drawable.ic_increase),
                contentDescription = "",
                tint = foregroundColor,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_xs))
                    .rotate(if (differenceToPreviousMonth > 0.0) { 0f } else if (differenceToPreviousMonth < 0.0) { 180f } else { 90f })
            )
        }
        Text(
            text = if (differenceToPreviousMonth > 0) {
                AnnotatedString.fromHtml(stringResource(R.string.main_analysis_upFromLastMonthLabel, onFormatValue(differenceToPreviousMonth)))
            } else if (differenceToPreviousMonth < 0) {
                AnnotatedString.fromHtml(stringResource(R.string.main_analysis_downFromLastMonthLabel, onFormatValue(abs(differenceToPreviousMonth))))
            } else {
                AnnotatedString.fromHtml(stringResource(R.string.main_analysis_noDiffToLastMonthLabel))
            },
            color = foregroundColor,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.padding_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
        )
    }
}


/**
 * Stacked bar chart for the analysis data (i.e. either incomes OR expenses).
 *
 * @param analysisData  Analysis data to display within the stacked bar chart.
 * @param colors        Colors to use for the stacked bars.
 * @param modifier      Modifier.
 */
@Composable
private fun SmallAnalysisDataDiagram(
    analysisData: SmallAnalysisData,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .height(24.dp)
            .clip(MaterialTheme.shapes.extraLargeIncreased)
            .background(MaterialTheme.colorScheme.outlineVariant)
    ) {
        val cumulatedSums: MutableList<Double> = mutableListOf()
        var currentSum = 0.0
        analysisData.typeResults.take(colors.size).forEach { typeResult ->
            currentSum += typeResult.sum
            cumulatedSums.add(currentSum)
        }

        cumulatedSums.reversed().forEachIndexed { index, cumulatedSum ->
            drawRoundRect(
                color = colors[cumulatedSums.size - (index % colors.size) - 1],
                size = Size(size.width * (cumulatedSum / analysisData.totalSum).toFloat(), size.height),
                cornerRadius = CornerRadius(100.0f, 100.0f)
            )
        }
    }
}


/**
 * Overview for a single type result.
 *
 * @param typeResult    Type result to display.
 * @param index         Index of the type result within the displayed list.
 * @param colors        Colors to use.
 * @param onQueryType   Callback invoked to query a type by it's ID.
 * @param onFormatValue Callback invoked to format a value.
 * @param modifier      Modifier.
 */
@Composable
private fun SmallTypeResultOverview(
    typeResult: SmallTypeResult,
    index: Int,
    colors: List<Color>,
    onQueryType: suspend (UUID) -> Type?,
    onFormatValue: (Double) -> String,
    modifier: Modifier = Modifier
) {
    val type: Type? by produceState(null) {
        value = if (typeResult.typeId != null) {
            onQueryType(typeResult.typeId)
        } else {
            null
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Shape(
            shape = if (type != null) {
                TypeShapes.getShapeForTypeIcon(type!!.icon).shape
            } else {
                MaterialShapes.Circle
            },
            color = if (index < colors.size) { colors[index] } else { MaterialTheme.colorScheme.outlineVariant },
            modifier = Modifier.size(dimensionResource(R.dimen.image_xxs))
        )
        Text(
            text = if (type != null) {
                type!!.name
            } else {
                stringResource(R.string.main_analysis_otherTypeLabel)
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
        )
        Text(
            text = onFormatValue(typeResult.sum),
            color = if (index < colors.size) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurfaceVariant },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
