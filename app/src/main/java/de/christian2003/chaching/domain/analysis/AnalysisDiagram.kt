package de.christian2003.chaching.domain.analysis


/**
 * Value object models a single diagram line for the analysis result. Examples of diagram lines are:
 * - Incomes for type 'Salary'
 * - Expenses for type 'Taxes'
 * - Cumulated incomes for type 'Salary'
 * - Cumulated expenses for type 'Health Insurance'
 * Instances of this class only contain data for a single line in a diagram. Other data (such as
 * labels for the x-axis) need to be determined separately.
 *
 * @param values                    Values for the diagram line.
 * @param min                       Min value of the diagram line. This value must exist in values.
 * @param max                       Max value of the diagram line. This value must exist in values.
 * @throws IllegalArgumentException Some of the arguments are invalid.
 */
data class AnalysisDiagram(
    val values: List<Double>,
    val min: Double,
    val max: Double
) {

    /**
     * Initializes the diagram and makes sure that the values provided are valid.
     */
    init {
        require(min <= max) { "Min value cannot be greater than the max value" }
        require(values.contains(min)) { "Min value not available in the list of values" }
        require(values.contains(max)) { "Max value not available in the list of values" }
    }

}
