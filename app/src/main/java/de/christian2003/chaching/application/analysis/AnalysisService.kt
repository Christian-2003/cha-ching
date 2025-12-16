package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.extensive.AnalysisResult
import java.time.LocalDate


/**
 * Analysis service can analyze the transfers for a specified period of time.
 */
@Deprecated("Use use case instead")
interface AnalysisService {

    /**
     * Analyzes all data in between the passed start and end days. The analysis results will be
     * generated for the precision specified.
     *
     * @param startDay  First day of the analysis results to include.
     * @param endDay    Last day of the analysis results to include.
     * @param precision Precision for the analysis results.
     * @return          Analysis result.
     */
    suspend fun analyzeData(startDay: LocalDate, endDay: LocalDate, precision: AnalysisPrecision): AnalysisResult

}
