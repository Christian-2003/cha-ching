package de.christian2003.chaching.plugin.infrastructure.rest.apps.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO for the root JSON object returned by the REST API.
 */
@Serializable
data class AppsResponseDto(

    /**
     * Version of the JSON.
     */
    @SerialName("version")
    val version: Int,

    /**
     * List of apps to advertise.
     */
    @SerialName("android")
    val apps: List<AppItemDto>

)
