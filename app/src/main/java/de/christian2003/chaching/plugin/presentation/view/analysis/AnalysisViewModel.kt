package de.christian2003.chaching.plugin.presentation.view.analysis

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.analysis.ExtensiveAnalysisUseCase
import de.christian2003.chaching.domain.analysis.AnalysisResult
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject


/**
 * View model for the analysis screen.
 *
 * @param application               Application object.
 * @param extensiveAnalysisUseCase  Use case through which to analyze the data.
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    application: Application,
    private val extensiveAnalysisUseCase: ExtensiveAnalysisUseCase,
): AndroidViewModel(application) {

    /**
     * Period for which to analyze data.
     */
    var analysisPeriod: AnalysisPeriod = AnalysisPeriod.CURRENT_YEAR

    var analysisResult: AnalysisResult? by mutableStateOf(null)
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

            analysisResult = result
        }
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
