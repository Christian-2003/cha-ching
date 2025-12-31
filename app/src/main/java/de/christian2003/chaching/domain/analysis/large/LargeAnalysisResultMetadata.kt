package de.christian2003.chaching.domain.analysis.large

import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import java.time.LocalDateTime


/**
 * Value object contains the metadata for the large analysis result.
 *
 * @param precision Analysis precision.
 * @param begin     Time at which the analysis begins.
 * @param finish    Time at which the analysis finishes.
 */
data class LargeAnalysisResultMetadata(
    val precision: AnalysisPrecision,
    val begin: LocalDateTime,
    val finish: LocalDateTime
) {

    /**
     * Initializes the value object and ensures that all properties are valid.
     */
    init {
        require(!finish.isBefore(begin)) { "Finish time must not be before start time" }
    }

}
