package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.application.analysis.dto.GroupedStatisticsResult
import de.christian2003.chaching.application.analysis.dto.GroupedTypeSum
import de.christian2003.chaching.application.analysis.dto.StatisticsResult


class GroupedTypeSumsToStatistics {

    fun toStatistics(groupedTypeSums: List<GroupedTypeSum>): GroupedStatisticsResult {
        var incomesTotal = 0
        var incomesCount = 0
        var incomesHoursTotal = 0

        var expensesTotal = 0
        var expensesCount = 0
        var expensesHoursTotal = 0

        groupedTypeSums.forEach { groupedTypeSum ->
            incomesTotal += groupedTypeSum.incomes.sum
            incomesCount += groupedTypeSum.incomes.count
            incomesHoursTotal += groupedTypeSum.incomes.hoursWorked

            expensesTotal += groupedTypeSum.expenses.sum
            expensesCount += groupedTypeSum.expenses.count
            expensesHoursTotal += groupedTypeSum.expenses.hoursWorked
        }

        val incomesResult: StatisticsResult = generateStatisticsResult(incomesTotal, incomesCount, groupedTypeSums.size)
        val incomesHoursResult: StatisticsResult = generateStatisticsResult(incomesHoursTotal, incomesCount, groupedTypeSums.size)
        val expensesResult: StatisticsResult = generateStatisticsResult(expensesTotal, expensesCount, groupedTypeSums.size)
        val expensesHoursResult: StatisticsResult = generateStatisticsResult(expensesHoursTotal, expensesCount, groupedTypeSums.size)

        return GroupedStatisticsResult(
            incomes = incomesResult,
            expenses = expensesResult,
            hoursIncomes = incomesHoursResult,
            hoursExpenses = expensesHoursResult
        )
    }



    private fun generateStatisticsResult(total: Int, totalCount: Int, normalizedDateCount: Int): StatisticsResult {
        val averagePerNormalizedDate: Int = if (normalizedDateCount > 0) {
            total / normalizedDateCount
        } else {
            0
        }

        val averagePerTransfer: Int = if (totalCount > 0) {
            total / totalCount
        } else {
            0
        }

        val result = StatisticsResult(
            total = total,
            totalCount = totalCount,
            averagePerNormalizedDate = averagePerNormalizedDate,
            averagePerTransfer = averagePerTransfer
        )

        return result
    }

}
