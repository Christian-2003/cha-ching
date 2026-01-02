package de.christian2003.chaching.application.analysis.large

import de.christian2003.chaching.application.analysis.large.algorithms.AnalysisDataSummarizer
import de.christian2003.chaching.application.analysis.large.algorithms.AnalysisDataTransformer
import de.christian2003.chaching.application.analysis.large.algorithms.LargeTimeSpanGenerator
import de.christian2003.chaching.application.analysis.large.dto.SummarizerGroupedTypeResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerResult
import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.application.services.NormalizedDateConverterService
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResultMetadata
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpan
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject


/**
 * Use case for the large and more powerful analysis. The result is displayed on the AnalysisScreen.
 *
 * @param repository                        Repository used to access the data.
 * @param normalizedDateConverterService    Service used to convert normalized dates.
 */
class LargeAnalysisUseCase @Inject constructor(
    private val repository: AnalysisRepository,
    private val normalizedDateConverterService: NormalizedDateConverterService
) {

    /**
     * Analyzes the data in the specified time span with the provided precision.
     *
     * @param precision Precision (i.e. month, quarter, year) with which to return the data.
     * @param start     Start day for the time span.
     * @param end       End day for the time span.
     */
    suspend fun analyze(precision: AnalysisPrecision, start: LocalDate, end: LocalDate): LargeAnalysisResult {
        val startTimestamp: LocalDateTime = LocalDateTime.now()

        //Start and end dates for previous time span:
        val timeSpanLengthInDays: Long = ChronoUnit.DAYS.between(start, end)
        val previousEnd = start
        val previousStart = previousEnd.minusDays(timeSpanLengthInDays)

        //Generate time spans:
        val currentSpan: LargeTimeSpan = generateLargeTimeSpan(precision, start, end)
        val previousSpan: LargeTimeSpan = generateLargeTimeSpan(precision, previousStart, previousEnd)

        //Generate result:
        val result = LargeAnalysisResult(
            currentSpan = currentSpan,
            previousSpan = previousSpan,
            metadata = LargeAnalysisResultMetadata(
                precision = precision,
                begin = startTimestamp,
                finish = LocalDateTime.now()
            )
        )

        return result
    }


    /**
     * Generates an instance of LargeTimeSpan for the specified analysis time span.
     *
     * @param precision Analysis precision.
     * @param start     Start date for the time span.
     * @param end       End date for the time span.
     * @return          Generated LargeTimeSpan-instance.
     */
    private suspend fun generateLargeTimeSpan(precision: AnalysisPrecision, start: LocalDate, end: LocalDate): LargeTimeSpan {
        val transfers: List<Transfer> = repository.getAllTransfersInTimeSpan(start, end).first()
        val types: List<Type> = repository.getAllTypes().first()

        //Summarize data:
        val summarizer = AnalysisDataSummarizer(precision, start, end, normalizedDateConverterService)
        val summarizedData: Map<UUID, List<SummarizerGroupedTypeResult>> = summarizer.summarizeData(transfers, types)

        //Transform data:
        val transformer = AnalysisDataTransformer()
        val transformedData: TransformerResult = transformer.transform(summarizedData)

        //Generate result:
        val generator = LargeTimeSpanGenerator()
        val result: LargeTimeSpan = generator.generate(start, end, transformedData)

        return result
    }

}
