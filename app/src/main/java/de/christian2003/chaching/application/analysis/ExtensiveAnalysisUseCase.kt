package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.transfer.Transfer
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class ExtensiveAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {

    suspend fun analyzeData(start: LocalDate, end: LocalDate, precision: AnalysisPrecision) {
        //Get all transfers:
        val transfers: List<Transfer> = repository.getAllTransfersInDateRange(start, end).first()

    }


}
