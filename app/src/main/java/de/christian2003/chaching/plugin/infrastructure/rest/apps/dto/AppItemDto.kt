package de.christian2003.chaching.plugin.infrastructure.rest.apps.dto

import android.net.Uri
import de.christian2003.chaching.plugin.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO for an app that should be advertised.
 */
@Serializable
data class AppItemDto(

    /**
     * Package name of the app.
     */
    @SerialName("package")
    val packageName: String,

    /**
     * URL to a web page advertising the app.
     */
    @SerialName("url")
    @Serializable(with = UriSerializer::class)
    val url: Uri,

    /**
     * Display name of the app.
     */
    @SerialName("name")
    val name: String,

    /**
     * URL to the SVG displaying the app icon.
     */
    @SerialName("icon")
    @Serializable(with = UriSerializer::class)
    val iconUrl: Uri

)
