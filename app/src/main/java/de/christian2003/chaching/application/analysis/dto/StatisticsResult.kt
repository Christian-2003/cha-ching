package de.christian2003.chaching.application.analysis.dto

data class StatisticsResult(
    val total: Int,
    val totalCount: Int,
    val averagePerNormalizedDate: Int,
    val averagePerTransfer: Int
)
