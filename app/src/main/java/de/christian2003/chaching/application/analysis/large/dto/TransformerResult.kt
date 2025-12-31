package de.christian2003.chaching.application.analysis.large.dto

data class TransformerResult(
    val incomes: List<TransformerTypeResult>,
    val expenses: List<TransformerTypeResult>
)
