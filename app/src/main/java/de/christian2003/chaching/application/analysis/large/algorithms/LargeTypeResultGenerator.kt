package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.dto.TransformerDateResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerTypeResult
import de.christian2003.chaching.domain.analysis.large.LargeTypeDiagram
import de.christian2003.chaching.domain.analysis.large.LargeTypeHoursWorked
import de.christian2003.chaching.domain.analysis.large.LargeTypeResult
import de.christian2003.chaching.domain.analysis.large.LargeTypeValue
import kotlin.math.roundToInt


/**
 * Generator can generate an instance of LargeTypeResult (and all it's subclasses) based on a
 * type result that is returned from the AnalysisDataTransformer.
 */
class LargeTypeResultGenerator {

    /**
     * Generates the result for a single type.
     *
     * @param typeResult    Type result returned from the transformer to use for generating the final
     *                      type result.
     * @return              Generated large type result.
     */
    fun generate(typeResult: TransformerTypeResult): LargeTypeResult {
        val valueResult: LargeTypeValue = generateValueResult(typeResult.dateResults)
        val hoursWorkedResult: LargeTypeHoursWorked = generateHoursWorkedResult(typeResult.dateResults)
        val transferCount: Int = getTransferCount(typeResult.dateResults)
        val valuesDiagram: LargeTypeDiagram = generateValuesDiagram(typeResult.dateResults)
        val cumulatedDiagram: LargeTypeDiagram = generateCumulatedDiagram(typeResult.dateResults)

        val result = LargeTypeResult(
            typeId = typeResult.typeId,
            valueResult = valueResult,
            hoursWorkedResult = hoursWorkedResult,
            transferCount = transferCount,
            valuesDiagram = valuesDiagram,
            cumulatedDiagram = cumulatedDiagram
        )

        return result
    }


    /**
     * Generates the value result.
     *
     * @param dateResults   Date results from which to generate the value result.
     * @return              Value result.
     */
    private fun generateValueResult(dateResults: List<TransformerDateResult>): LargeTypeValue {
        var sum = 0
        var count = 0

        dateResults.forEach { dateResult ->
            sum += dateResult.sum
            count += dateResult.count
        }

        val numberOfLocalizedDates = dateResults.size

        val avgPerTransfer = if (count == 0) { sum } else { sum / count }
        val avgPerNormalizedDate = if (numberOfLocalizedDates == 0) { sum } else { sum / numberOfLocalizedDates }

        val result = LargeTypeValue(
            sum = centsToEuros(sum),
            avgPerTransfer = centsToEuros(avgPerTransfer),
            avgPerNormalizedDate = centsToEuros(avgPerNormalizedDate)
        )

        return result
    }


    /**
     * Generates the value result.
     *
     * @param dateResults   Date results from which to generate the value result.
     * @return              Value result.
     */
    private fun generateHoursWorkedResult(dateResults: List<TransformerDateResult>): LargeTypeHoursWorked {
        var sum = 0
        var count = 0

        dateResults.forEach { dateResult ->
            sum += dateResult.hoursWorked
            count += dateResult.count
        }

        val numberOfLocalizedDates = dateResults.size

        val avgPerTransfer = if (count == 0) { sum } else { (sum.toDouble() / count).roundToInt() }
        val avgPerNormalizedDate = if (numberOfLocalizedDates == 0) { sum } else { (sum.toDouble() / numberOfLocalizedDates).roundToInt() }

        val result = LargeTypeHoursWorked(
            sum = sum,
            avgPerTransfer = avgPerTransfer,
            avgPerNormalizedDate = avgPerNormalizedDate
        )

        return result
    }


    /**
     * Gets the number of transfers.
     *
     * @param dateResults   Date results from which to get the number of transfers.
     * @return              Number of transfers.
     */
    private fun getTransferCount(dateResults: List<TransformerDateResult>): Int {
        var count = 0

        dateResults.forEach { dateResult ->
            count += dateResult.count
        }

        return count
    }


    /**
     * Generates the values diagram.
     *
     * @param dateResults   Date results from which to generate the values diagram.
     * @return              Values diagram.
     */
    private fun generateValuesDiagram(dateResults: List<TransformerDateResult>): LargeTypeDiagram {
        val values: MutableList<Double> = mutableListOf()

        dateResults.forEach { dateResult ->
            values.add(centsToEuros(dateResult.sum))
        }

        val result = LargeTypeDiagram(
            values = values
        )

        return result
    }


    /**
     * Generates the cumulated values diagram.
     *
     * @param dateResults   Date results from which to generate the cumulated diagram.
     * @return              Cumulated values diagram.
     */
    private fun generateCumulatedDiagram(dateResults: List<TransformerDateResult>): LargeTypeDiagram {
        val cumulatedValues: MutableList<Double> = mutableListOf()
        var cumulatedValue = 0

        dateResults.forEach { dateResult ->
            cumulatedValue += dateResult.sum
            cumulatedValues.add(centsToEuros(cumulatedValue))
        }

        val result = LargeTypeDiagram(
            values = cumulatedValues
        )

        return result
    }


    /**
     * Converts the passed cents value to euros.
     *
     * @param cents Cents value to convert.
     * @return      Euros.
     */
    private fun centsToEuros(cents: Int): Double {
        return cents.toDouble() / 100.0
    }

}
