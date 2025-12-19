package de.christian2003.chaching.plugin.presentation.view.analysis

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.analysis.ExtensiveAnalysisUseCase
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.application.usecases.type.GetTypeByIdUseCase
import de.christian2003.chaching.domain.analysis.AnalysisResult
import de.christian2003.chaching.domain.analysis.TypeResult
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTypeDiagram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.UUID
import javax.inject.Inject


/**
 * View model for the analysis screen.
 *
 * @param application               Application object.
 * @param extensiveAnalysisUseCase  Use case through which to analyze the data.
 * @param valueFormatterService     Service used to format values.
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    application: Application,
    private val extensiveAnalysisUseCase: ExtensiveAnalysisUseCase,
    private val valueFormatterService: ValueFormatterService,
    private val getTypeByIdUseCase: GetTypeByIdUseCase
): AndroidViewModel(application) {

    /**
     * Period for which to analyze data.
     */
    var analysisPeriod: AnalysisPeriod = AnalysisPeriod.CURRENT_YEAR

    var analysisResult: AnalysisResult? by mutableStateOf(null)
        private set

    var typeResultsIncomes: List<TypeResult> = listOf()
        private set

    var typeResultsExpenses: List<TypeResult> = listOf()
        private set

    lateinit var valuesDiagramIncomes: DataTypeDiagram
        private set

    lateinit var cumulatedDiagramIncomes: DataTypeDiagram
        private set

    lateinit var valuesDiagramExpenses: DataTypeDiagram
        private set

    lateinit var cumulatedDiagramExpenses: DataTypeDiagram
        private set


    /**
     * Initializes the view model.
     */
    init {
        startAnalysis(force = true)
    }


    /**
     * Starts the analysis for the time period specified.
     *
     * @param analysisPeriod    Time period for the analysis.
     * @param force             Whether to force start an analysis, even if one is already running.
     */
    fun startAnalysis(force: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (analysisResult != null || force) {
            analysisResult = null

            val periodLength = analysisPeriod.endDate.toEpochDay() - analysisPeriod.startDate.toEpochDay()
            val precision: AnalysisPrecision = if (periodLength <= 365) {
                AnalysisPrecision.Month //0 Months - 12 Months
            } else if (periodLength <= 1825) {
                AnalysisPrecision.Quarter //13 Months - 5 Years
            } else {
                AnalysisPrecision.Year //More than 5 Years
            }
            val result: AnalysisResult = extensiveAnalysisUseCase.analyzeData(precision, analysisPeriod.startDate, analysisPeriod.endDate)

            //Convert analysis result format so that it can be used in view:
            typeResultsIncomes = result.typeResults.filter { it.incomes.transferSum > 0 }.sortedByDescending { it.incomes.transferSum }
            typeResultsExpenses = result.typeResults.filter { it.expenses.transferCount > 0 }.sortedByDescending { it.expenses.transferSum }

            valuesDiagramIncomes = DataTypeDiagram.Builder(typeResultsIncomes)
                .setOptions(DataTabOptions.Incomes)
                .setLimit(4)
                .build()
            cumulatedDiagramIncomes = DataTypeDiagram.Builder(typeResultsIncomes)
                .setOptions(DataTabOptions.Incomes)
                .setLimit(4)
                .setCumulated()
                .build()
            valuesDiagramExpenses = DataTypeDiagram.Builder(typeResultsExpenses)
                .setOptions(DataTabOptions.Expenses)
                .setLimit(4)
                .build()
            cumulatedDiagramExpenses = DataTypeDiagram.Builder(typeResultsExpenses)
                .setOptions(DataTabOptions.Expenses)
                .setLimit(4)
                .setCumulated()
                .build()

            analysisResult = result
        }
    }


    fun formatValue(value: Double): String {
        return valueFormatterService.format(value)
    }


    suspend fun queryType(typeId: UUID): Type? {
        return getTypeByIdUseCase.getTypeById(typeId)
    }


    /**
     * Builds the indicator values for diagrams.
     *
     * @param value Value for which to build the indicator.
     * @return      Indicator value.
     */
    fun buildIndicator(value: Double): String {
        val numberFormat: NumberFormat = DecimalFormat("#,##0.00")
        val formattedNumber: String = numberFormat.format(value)
        return formattedNumber
    }

}
