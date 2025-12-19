package de.christian2003.chaching.domain.analysis

data class ComparableAnalysisResult(
    val currentTimePeriod: AnalysisResult,
    val previousTimePeriod: AnalysisResult
)
