package de.christian2003.chaching.domain.analysis.extensive

import de.christian2003.chaching.domain.type.Type


/**
 * Models a single line for a diagram.
 */
data class AnalysisDiagramLine(

    /**
     * List of data points for the diagram line.
     */
    val data: List<AnalysisItem>,

    /**
     * Type for which the diagram line is created.
     */
    val type: Type?

)
