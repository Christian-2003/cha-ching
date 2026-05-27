package de.christian2003.chaching.plugin.infrastructure.rest.update.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root DTO for the update REST API.
 *
 * @param metadata      Metadata for the JSON response.
 * @param packageName   Name of the package queried (Should be identical to this package's name).
 * @param version       Info about the newest release.
 */
@Serializable
data class UpdateRootDto(
    @SerialName("metadata") val metadata: UpdateMetadataDto? = null,
    @SerialName("package") val packageName: String = "",
    @SerialName("version") val version: UpdateVersionDto? = null
)
