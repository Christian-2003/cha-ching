package de.christian2003.chaching.application.analysis.large.dto

import java.time.LocalDate


data class TransformerDateResult(
    val normalizedDate: LocalDate,
    val sum: Int,
    val count: Int,
    val hoursWorked: Int
)
