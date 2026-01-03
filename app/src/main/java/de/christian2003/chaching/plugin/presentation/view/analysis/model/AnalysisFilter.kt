package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision


/**
 * Filter for the analysis.
 *
 * @param period    Analysis period.
 * @param precision Requested precision. This is null when "auto" is selected.
 */
data class AnalysisFilter(
    val period: AnalysisPeriod,
    val precision: AnalysisPrecision?
)
