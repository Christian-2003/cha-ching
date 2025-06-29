package de.christian2003.chaching.plugin.presentation.view.analysis

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christian2003.chaching.application.analysis.AnalysisService
import de.christian2003.chaching.application.analysis.AnalysisServiceImpl
import de.christian2003.chaching.application.analysis.AnalysisSquasher
import de.christian2003.chaching.domain.analysis.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.AnalysisResult
import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate


class AnalysisViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var transferRepository: TransferRepository

    private lateinit var typeRepository: TypeRepository

    private lateinit var analysisService: AnalysisService

    private var isInitialized: Boolean = false


    lateinit var analysisPeriod: AnalysisPeriod

    var analysisResult: AnalysisResult? by mutableStateOf(null)

    var showDatePeriodPickerDialog: Boolean by mutableStateOf(false)


    fun init(transferRepository: TransferRepository, typeRepository: TypeRepository) {
        if (!isInitialized) {
            this.transferRepository = transferRepository
            this.typeRepository = typeRepository
            this.analysisService = AnalysisSquasher(AnalysisServiceImpl(transferRepository, typeRepository))
            startAnalysis(analysisPeriod = AnalysisPeriod.CURRENT_YEAR, force = true)
            isInitialized = true
        }
    }


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


    fun buildIndicator(value: Double): String {
        val numberFormat: NumberFormat = DecimalFormat("#,##0.00")
        val formattedNumber: String = numberFormat.format(value)
        return formattedNumber
    }

}
