package de.christian2003.chaching.domain.analysis.extensive


/**
 * Field indicates the precision for the analysis.
 */
enum class AnalysisPrecision {

    /**
     * Monthly precision means that analysis results are calculated for each month.
     */
    MONTH,

    /**
     * Quarterly precision means that analysis results are calculated for each quarter of a year.
     */
    QUARTER,

    /**
     * Yearly precision means that analysis results are calculated for each year.
     */
    YEAR

}
