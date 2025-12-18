package de.christian2003.chaching.domain.analysis


/**
 * Value object models the resulting summary of a list of transfers. Examples might be:
 * - Total expenses for a time period (e.g. 2025-01-01 to 2025-06-01)
 * - Total income for a time period (e.g. 2025-01-01 to 2025-06-01)
 *
 * @param transferSum               Sum of the values of the summarized transfers.
 * @param transferCount             Number of transfers that were summarized.
 * @param transferAvg               Average value per transfer.
 * @param normalizedDateAvg         Average value per normalized date (e.g. month, quarter or year).
 * @throws IllegalArgumentException Some of the arguments are invalid.
 */
//No use of 'data class' because TypeResultSummary needs to extend this class
open class ResultSummary(
    val transferSum: Double,
    val transferCount: Int,
    val transferAvg: Double,
    val normalizedDateAvg: Double
) {

    /**
     * Initializes the summary and makes sure that the data provided are valid.
     */
    init {
        require(transferSum >= 0.0) { "Transfer sum cannot be negative" }
        require(transferCount >= 0) { "Count cannot be negative" }
        require(transferAvg >= 0.0) { "Transfer average cannot be negative" }
        require(normalizedDateAvg >= 0.0) { "Normalized date average cannot be negative" }
    }


    /**
     * Tests whether the passed object is identical to this object.
     *
     * @param other Other object to test.
     * @return      Whether the other object is identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is ResultSummary
                && other.transferSum == this.transferSum
                && other.transferCount == this.transferCount
                && other.transferAvg == this.transferAvg
                && other.normalizedDateAvg == this.normalizedDateAvg
    }


    /**
     * Returns the hash code for this object.
     *
     * @return  Hash code.
     */
    override fun hashCode(): Int {
        var hash: Int = transferSum.hashCode()
        hash = 31 * hash + transferCount.hashCode()
        hash = 31 * hash + transferAvg.hashCode()
        hash = 31 * hash + normalizedDateAvg.hashCode()
        return hash
    }

}
