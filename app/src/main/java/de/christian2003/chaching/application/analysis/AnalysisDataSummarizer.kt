package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.application.analysis.dto.GroupedTypeSum
import de.christian2003.chaching.application.analysis.dto.TypeSum
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import java.time.LocalDate
import java.util.UUID


/**
 * The summarizer summarizes a list of transfers.
 * Transfers are summarized by each normalized date in between the specified start and end days, e.g.:
 * Month:   [2025-01-01, 2025-02-01, 2025-03-01, ...]
 * Quarter: [2025-01-01, 2025-03-01, 2025-07-01, ...]
 * Year:    [2025-01-01, 2026-01-01, 2027-01-01, ...]
 * This list is grouped by type. This means, that for each type, there is a separate list.
 *
 * @param precision Analysis precision.
 * @param start     Start date for the analysis.
 * @param end       End date for the analysis.
 */
class AnalysisDataSummarizer(
    private val precision: AnalysisPrecision,
    private val start: LocalDate,
    private val end: LocalDate
) {

    /**
     * Summarizes the list of passed transfers. The return value maps the result of the summary
     * to each type. The map contains an entry for each type passed. Within the list (which is mapped
     * to a type), for each normalized date, incomes and expenses are summarized.
     *
     * @param transfers Transfers to summarize.
     * @param types     Types to which to map the transfers.
     * @return          List of summaries by normalized date mapped to each type.
     */
    fun summarizeData(transfers: List<Transfer>, types: List<Type>): Map<UUID, List<GroupedTypeSum>> {
        //Group transfers by type:
        val groupedTransfersByType: Map<UUID, List<Transfer>> = groupTransfersByType(transfers, types)

        val result: MutableMap<UUID, List<GroupedTypeSum>> = mutableMapOf()

        //For each type: Calculate summaries:
        groupedTransfersByType.forEach { typeId, transfers ->
            val groupedTransfersByDate: Map<LocalDate, List<Transfer>> = groupTransfersByNormalizedDate(transfers)

            //Calculate summary by normalized date:
            val groupedTypeSums: MutableList<GroupedTypeSum> = mutableListOf()
            groupedTransfersByDate.forEach { date, transfers ->
                val groupedTypeSum: GroupedTypeSum = calculateGroupedTypeSumForTransfers(date, transfers)
                groupedTypeSums.add(groupedTypeSum)
            }

            //Sort by date:
            val sortedGroupedTypeSums: List<GroupedTypeSum> = groupedTypeSums.sortedBy { it.date }

            //Make sure that all normalized dates between start and end are in the final result:
            val trimmedGroupTypeSums: List<GroupedTypeSum> = trimGroupedTypesSum(sortedGroupedTypeSums)

            result[typeId] = trimmedGroupTypeSums
        }

        return result
    }


    /**
     * Groups the specified transfers by the types. The resulting map contains an entry for each type
     * that is available in the specified list of types.
     *
     * @param transfers                 List of transfers to group.
     * @param types                     List of all types.
     * @return                          Transfers grouped by type.
     * @throws IllegalStateException    The list of transfers contains a type that is not available in
     *                                  the list of types.
     */
    private fun groupTransfersByType(transfers: List<Transfer>, types: List<Type>): Map<UUID, List<Transfer>> {
        val groupedTransfers: MutableMap<UUID, List<Transfer>> = transfers.groupBy { transfer ->
            transfer.type
        }.toMutableMap()

        if (groupedTransfers.size != types.size) {
            //Some types are missing in the map -> Add missing types:
            types.forEach { type ->
                if (!groupedTransfers.containsKey(type.id)) {
                    groupedTransfers[type.id] = emptyList()
                }
            }
        }

        return groupedTransfers
    }


    /**
     * Groups the list of transfers based on their value dates according to the analysis precision.
     *
     * @param transfers List of transfers to group.
     * @return          Transfers grouped by their value date.
     */
    private fun groupTransfersByNormalizedDate(transfers: List<Transfer>): Map<LocalDate, List<Transfer>> {
        val groupedTransfers: Map<LocalDate, List<Transfer>> = transfers.groupBy { transfer ->
            val date: LocalDate = getNormalizedDate(transfer.transferValue.date)
            return@groupBy date
        }

        return groupedTransfers
    }


    /**
     * Based on the analysis precision, this converts the passed local date as follows:
     * - AnalysisPrecision.MONTH:   Date with the first day of the month is returned.
     * - AnalysisPrecision.QUARTER: Date with the first day of the quarter is returned.
     * - AnalysisPrecision.YEAR:    Date with the first day of the year is returned.
     *
     * @param date  Date to convert.
     * @return      Converted date.
     */
    private fun getNormalizedDate(date: LocalDate): LocalDate {
        when (precision) {
            AnalysisPrecision.Month -> {
                return date.withDayOfMonth(1)
            }
            AnalysisPrecision.Quarter -> {
                val quarterMonth = ((date.month.value - 1) / 3) * 3 + 1 //Q1=1, Q2=4, Q3=7, Q4=10
                return date.withDayOfMonth(1).withMonth(quarterMonth)
            }
            AnalysisPrecision.Year -> {
                return date.withDayOfYear(1)
            }
        }
    }


    /**
     * Calculates the grouped type sum for the specified list of transfers.
     *
     * @param date      Date for the resulting grouped type sum.
     * @param transfers Transfers to use for the calculation.
     * @return          Grouped type sum.
     */
    private fun calculateGroupedTypeSumForTransfers(date: LocalDate, transfers: List<Transfer>): GroupedTypeSum {
        var incomeSum = 0
        var incomeCount = 0
        var incomeHours = 0
        var expenseSum = 0
        var expenseCount = 0
        var expenseHours = 0

        transfers.forEach { transfer ->
            if (transfer.transferValue.isSalary) {
                incomeSum += transfer.transferValue.value
                incomeCount++
                incomeHours += transfer.hoursWorked
            }
            else {
                expenseSum += transfer.transferValue.value
                expenseCount++
                expenseHours = transfer.hoursWorked
            }
        }

        return GroupedTypeSum(
            date = date,
            incomes = TypeSum(
                sum = incomeSum,
                count = incomeCount,
                hoursWorked = incomeHours
            ),
            expenses = TypeSum(
                sum = expenseSum,
                count = expenseCount,
                hoursWorked = expenseHours
            )
        )
    }


    /**
     * Trims the specified list of grouped type sums. The resulting list contains an item for each
     * normalized date in between the specified start and end dates.
     *
     * @param groupedTypeSums   Grouped type sums to trim.
     * @return                  Trimmed grouped type sums.
     */
    private fun trimGroupedTypesSum(groupedTypeSums: List<GroupedTypeSum>): List<GroupedTypeSum> {
        var current: LocalDate = getNormalizedDate(getNormalizedDate(start))
        val endNormalized: LocalDate = getNormalizedDate(getNormalizedDate(end))

        val stepMonths: Long = when(precision) {
            AnalysisPrecision.Month -> 1
            AnalysisPrecision.Quarter -> 3
            AnalysisPrecision.Year -> 12
        }
        val result: MutableList<GroupedTypeSum> = mutableListOf()

        while (!current.isAfter(endNormalized)) {
            val existingItem: GroupedTypeSum? = groupedTypeSums.find { it.date == current }
            if (existingItem != null) {
                //Item exists:
                result.add(existingItem)
            }
            else {
                //Item missing:
                val newItem = GroupedTypeSum(
                    date = current,
                    incomes = TypeSum(0, 0, 0),
                    expenses = TypeSum(0, 0, 0)
                )
                result.add(newItem)
            }

            current = current.plusMonths(stepMonths)
        }

        return result
    }

}
