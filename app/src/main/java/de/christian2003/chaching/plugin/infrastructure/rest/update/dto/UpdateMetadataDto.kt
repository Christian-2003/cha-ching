package de.christian2003.chaching.plugin.infrastructure.rest.update.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO for the update REST API containing the response metadata.
 *
 * @param version       JSON version of the response.
 * @param serverTime    Server time.
 */
@Serializable
data class UpdateMetadataDto(
    @SerialName("version") val version: Int = -1,
    @SerialName("servertime") val serverTime: Long = 0
)
