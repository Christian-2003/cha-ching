package de.christian2003.chaching.domain.analysis.large


/**
 * Value object contains the entire analysis result.
 *
 * @param currentSpan   Result for the current time span.
 * @param previousSpan  Result for the previous time span.
 */
data class LargeAnalysisResult(
    val currentSpan: LargeTimeSpan,
    val previousSpan: LargeTimeSpan,
    val metadata: LargeAnalysisResultMetadata
)
