package de.christian2003.chaching.domain.type

import java.time.LocalDateTime


data class DeletedType(
    val type: Type,
    val deletedAt: LocalDateTime
)
