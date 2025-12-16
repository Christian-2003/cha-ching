package de.christian2003.chaching.application.analysis.dto

import de.christian2003.chaching.domain.type.Type

data class AnalysisDataSummary(
    val results: Map<Type, List<GroupedTypeSum>>
)
