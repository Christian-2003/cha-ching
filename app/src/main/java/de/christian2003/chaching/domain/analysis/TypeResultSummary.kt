package de.christian2003.chaching.domain.analysis


/**
 * Value object models the resulting summary of a list of transfers for a type. Examples might be:
 * - Incomes for a time period for the type 'Salary'
 * - Expenses for a time period for the type 'Taxes'
 * - Incomes for a time period for the type 'Shares'
 * - Expenses for a time period for the type 'Insurance'
 *
 * @param transferSum               Sum of the values of the summarized transfers.
 * @param transferCount             Number of transfers that were summarized.
 * @param transferAvg               Average value per transfer.
 * @param normalizedDateAvg         Average value per normalized date (e.g. month, quarter or year).
 * @param valuesDiagram             Diagram containing the values for the normalized dates.
 * @param cumulatedDiagram          Diagram containing the cumulated values for the normalized dates.
 * @throws IllegalArgumentException Some of the arguments are invalid.
 */
class TypeResultSummary(
    transferSum: Double,
    transferCount: Int,
    transferAvg: Double,
    normalizedDateAvg: Double,
    val valuesDiagram: AnalysisDiagram,
    val cumulatedDiagram: AnalysisDiagram
): ResultSummary(
    transferSum = transferSum,
    transferCount = transferCount,
    transferAvg = transferAvg,
    normalizedDateAvg = normalizedDateAvg
) {

    /**
     * Tests whether the passed object is identical to this object.
     *
     * @param other Other object to test.
     * @return      Whether the other object is identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is TypeResultSummary
                && super.equals(other)
                && other.valuesDiagram == this.valuesDiagram
                && other.cumulatedDiagram == this.cumulatedDiagram
    }


    /**
     * Returns the hash code for this object.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash: Int = super.hashCode()
        hash = 31 * hash + valuesDiagram.hashCode()
        hash = 31 * hash + cumulatedDiagram.hashCode()
        return hash
    }

}
