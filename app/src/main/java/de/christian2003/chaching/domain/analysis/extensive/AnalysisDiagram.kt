package de.christian2003.chaching.domain.analysis.extensive


/**
 * Models a single analysis diagram.
 */
data class AnalysisDiagram(

    /**
     * Stores the lines for the diagram.
     */
    val lines: List<AnalysisDiagramLine>,

    /**
     * Stores the precision for the lines of the diagram.
     */
    val precision: AnalysisPrecision

)
