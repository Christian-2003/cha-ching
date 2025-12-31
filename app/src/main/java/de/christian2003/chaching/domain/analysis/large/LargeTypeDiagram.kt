package de.christian2003.chaching.domain.analysis.large


/**
 * Value object contains the values for a type over a time span. For each normalized date within a
 * time span, the diagram contains exactly one value.
 *
 * @param values    Values for the diagram.
 * @param min       Min value in the diagram.
 * @param max       Max value in the diagram.
 */
data class LargeTypeDiagram(
    val values: List<Double>,
    val min: Double = values.minOrNull() ?: 0.0,
    val max: Double = values.maxOrNull() ?: 0.0
) {

    /**
     * Initializes the value object and ensures that all properties are valid.
     */
    init {
        if (values.isNotEmpty()) {
            require(values.contains(min)) { "Min value must be included in diagram" }
            require(values.contains(max)) { "Max value must be included in diagram" }
        }
        else {
            require(min == 0.0) { "Min value must be 0.0 because diagram is empty" }
            require(max == 0.0) { "Max value must be 0.0 because diagram is empty" }
        }
        require(min <= max) { "Min value must be smaller than max value" }
        values.forEach { value ->
            require(value >= 0.0) { "All values must not be negative" }
        }
    }

}
