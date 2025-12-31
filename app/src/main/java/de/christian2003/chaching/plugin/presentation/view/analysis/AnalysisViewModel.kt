package de.christian2003.chaching.plugin.presentation.view.analysis

import android.app.Application
import android.content.res.Resources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.application.usecases.type.GetTypeByIdUseCase
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import de.christian2003.chaching.R
import de.christian2003.chaching.application.analysis.large.LargeAnalysisUseCase
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabDto
import java.time.format.DateTimeFormatter


/**
 * View model for the analysis screen.
 *
 * @param application               Application object.
 * @param largeAnalysisUseCase      Use case through which to start the large analysis.
 * @param valueFormatterService     Service used to format values.
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    application: Application,
    private val largeAnalysisUseCase: LargeAnalysisUseCase,
    private val valueFormatterService: ValueFormatterService,
    private val getTypeByIdUseCase: GetTypeByIdUseCase
): AndroidViewModel(application) {

    /**
     * Period for which to analyze data.
     */
    var analysisPeriod: AnalysisPeriod = AnalysisPeriod.CURRENT_YEAR

    var analysisResult: LargeAnalysisResult? by mutableStateOf(null)
        private set

    lateinit var incomesTabData: DataTabDto
        private set

    lateinit var expensesTabData: DataTabDto
        private set

    var diagramLabels: List<String> = emptyList()
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
     * @param force             Whether to force start an analysis, even if one is already running.
     */
    fun startAnalysis(force: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (analysisResult != null || force) {
            val periodLength = analysisPeriod.endDate.toEpochDay() - analysisPeriod.startDate.toEpochDay()
            val precision: AnalysisPrecision = if (periodLength <= 365) {
                AnalysisPrecision.Month //0 Months - 12 Months
            } else if (periodLength <= 1825) {
                AnalysisPrecision.Quarter //13 Months - 5 Years
            } else {
                AnalysisPrecision.Year //More than 5 Years
            }

            val result: LargeAnalysisResult = largeAnalysisUseCase.analyze(precision, analysisPeriod.startDate, analysisPeriod.endDate)

            createDiagramLabels(result.currentSpan.normalizedDates, precision)

            this@AnalysisViewModel.incomesTabData = DataTabDto.getInstance(
                options = DataTabOptions.Incomes,
                analysisResult = result,
                diagramLabels = diagramLabels
            )
            this@AnalysisViewModel.expensesTabData = DataTabDto.getInstance(
                options = DataTabOptions.Expenses,
                analysisResult = result,
                diagramLabels = diagramLabels
            )
            this@AnalysisViewModel.analysisResult = result
        }
    }


    fun formatValue(value: Double): String {
        return valueFormatterService.format(value)
    }


    suspend fun queryType(typeId: UUID): Type? {
        return getTypeByIdUseCase.getTypeById(typeId)
    }


    private fun createDiagramLabels(normalizedDates: List<LocalDate>, precision: AnalysisPrecision) {
        val labels: MutableList<String> = mutableListOf()
        val resources: Resources = application.resources
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yy")

        normalizedDates.forEach { normalizedDate ->
            when (precision) {
                AnalysisPrecision.Month -> {
                    val months: Array<String> = resources.getStringArray(R.array.months_short)
                    val label: String = months[normalizedDate.monthValue - 1].format(normalizedDate.format(formatter))
                    labels.add(label)
                }
                AnalysisPrecision.Quarter -> {
                    val quarters: Array<String> = resources.getStringArray(R.array.quarters)
                    val index: Int = (normalizedDate.monthValue - 1) / 3
                    val label: String = quarters[index].format(normalizedDate.format(formatter))
                    labels.add(label)
                }
                AnalysisPrecision.Year -> {
                    val label: String = normalizedDate.year.toString()
                    labels.add(label)
                }
            }
        }

        this.diagramLabels = labels
    }

}
