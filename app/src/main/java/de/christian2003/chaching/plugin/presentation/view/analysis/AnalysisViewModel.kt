package de.christian2003.chaching.plugin.presentation.view.analysis

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.chaching.application.analysis.AnalysisService
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.extensive.AnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject


/**
 * View model for the analysis screen.
 *
 * @param application       Application object.
 * @param analysisService   Service with which to analyze data.
 */
@HiltViewModel
class AnalysisViewModel @Inject constructor(
    application: Application,
    private val analysisService: AnalysisService
): AndroidViewModel(application) {

    /**
     * Period for which to analyze data.
     */
    lateinit var analysisPeriod: AnalysisPeriod

    /**
     * Result of the analysis. This is null while the analysis is running.
     */
    var analysisResult: AnalysisResult? by mutableStateOf(null)

    /**
     * Indicates whether to show the dialog through which to select a custom date range for the analysis.
     */
    var showDatePeriodPickerDialog: Boolean by mutableStateOf(false)


    /**
     * Initializes the view model.
     */
    init {
        startAnalysis(analysisPeriod = AnalysisPeriod.CURRENT_YEAR, force = true)
    }


    /**
     * Starts the analysis for the time period specified.
     *
     * @param analysisPeriod    Time period for the analysis.
     * @param force             Whether to force start an analysis, even if one is already running.
     */
    fun startAnalysis(analysisPeriod: AnalysisPeriod, force: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
        if (analysisResult != null || force) {
            analysisResult = null
            this@AnalysisViewModel.analysisPeriod = analysisPeriod

            val periodLength = analysisPeriod.endDate.toEpochDay() - analysisPeriod.startDate.toEpochDay()
            val precision: AnalysisPrecision = if (periodLength <= 365) {
                AnalysisPrecision.MONTH //0 Months - 12 Months
            } else if (periodLength <= 1825) {
                AnalysisPrecision.QUARTER //13 Months - 5 Years
            } else {
                AnalysisPrecision.YEAR //More than 5 Years
            }

            val result: AnalysisResult = analysisService.analyzeData(analysisPeriod.startDate, analysisPeriod.endDate, precision)
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
