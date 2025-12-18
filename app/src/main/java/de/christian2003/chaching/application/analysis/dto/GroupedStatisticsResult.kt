package de.christian2003.chaching.application.analysis.dto

data class GroupedStatisticsResult(
    val incomes: StatisticsResult,
    val expenses: StatisticsResult,
    val hoursIncomes: StatisticsResult,
    val hoursExpenses: StatisticsResult
)
