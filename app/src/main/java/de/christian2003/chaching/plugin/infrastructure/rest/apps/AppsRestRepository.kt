package de.christian2003.chaching.plugin.infrastructure.rest.apps

import de.christian2003.chaching.domain.apps.AppItem
import de.christian2003.chaching.domain.repository.AppsRepository
import de.christian2003.chaching.plugin.infrastructure.rest.apps.dto.AppsResponseDto
import de.christian2003.chaching.plugin.infrastructure.rest.apps.mapper.AppItemRestMapper
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


/**
 * Repository loads all apps that should be advertised from a REST API.
 *
 * @param packageName   Package name of this app.
 * @param client        OkHttp client.
 */
class AppsRestRepository @Inject constructor(
    private val packageName: String,
    private val client: OkHttpClient
): AppsRepository {

    /**
     * Mapper to app item DTOs to their domain objects.
     */
    private val appItemMapper = AppItemRestMapper()

    /**
     * JSON for deserialization.
     */
    private val json = Json {
        ignoreUnknownKeys = true
    }


    /**
     * Returns a list of all apps to advertise.
     *
     * @return  List of all apps.
     */
    override suspend fun getApps(): List<AppItem> {
        val response: AppsResponseDto? = getAppsResponse()
        if (response == null) {
            return emptyList()
        }

        val apps: MutableList<AppItem> = mutableListOf()
        response.apps.forEach { app ->
            if (app.packageName != packageName) {
                apps.add(appItemMapper.toDomain(app))
            }
        }
        return apps
    }


    /**
     * Loads the JSON from the REST API and returns the deserialized root JSON object.
     *
     * @return  Root JSON object returned from the REST API.
     */
    private fun getAppsResponse(): AppsResponseDto? {
        try {
            val request = Request.Builder()
                .url("https://api.christian2003.de/v1/apps")
                .build()

            val response: Response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return null
            }
            val bodyString: String = response.body?.string() ?: return null

            return json.decodeFromString(bodyString)
        }
        catch (_: Exception) {
            return null
        }
    }

}
