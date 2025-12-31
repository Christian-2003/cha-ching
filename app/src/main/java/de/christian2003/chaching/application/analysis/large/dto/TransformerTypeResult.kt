package de.christian2003.chaching.application.analysis.large.dto

import java.util.UUID


data class TransformerTypeResult(
    val typeId: UUID,
    val dateResults: List<TransformerDateResult>
)
