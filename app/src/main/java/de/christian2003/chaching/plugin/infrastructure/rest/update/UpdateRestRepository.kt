package de.christian2003.chaching.plugin.infrastructure.rest.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.chaching.R
import de.christian2003.chaching.application.repository.UpdateRepository
import de.christian2003.chaching.plugin.infrastructure.rest.update.dto.UpdateRootDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume


/**
 * Repository to check for app updates that uses the REST API to get information about the newest
 * app version.
 *
 * @param context   Application context.
 * @param client    HTTP client.
 */
@Singleton
class UpdateRestRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val client: OkHttpClient
) : UpdateRepository {

    /**
     * Response from the REST API. This is null if the request was not made yet.
     */
    private var updateResponse: UpdateRootDto? = null

    /**
     * Indicates whether the app is currently downloading an update.
     */
    private var isDownloading: Boolean = false


    /**
     * Checks whether an update is available.
     *
     * @return  Whether an update is available.
     */
    override suspend fun isUpdateAvailable(): Boolean = withContext(Dispatchers.IO) {
        val updateResponse: UpdateRootDto? = this@UpdateRestRepository.updateResponse
        if (updateResponse != null) {
            return@withContext isNewVersionAvailable(updateResponse)
        }
        val packageName: String = context.packageName
        val requestUrl = "https://api.christian2003.de/v1/versions/latest/?package=$packageName"
        val httpRequest = Request.Builder()
            .url(requestUrl)
            .build()

        val httpResponse: Response = client.newCall(httpRequest).execute()

        if (!httpResponse.isSuccessful) {
            return@withContext false
        }

        val jsonResponse: String = httpResponse.body?.string() ?: ""

        val responseBody: UpdateRootDto? = try {
            Json.decodeFromString<UpdateRootDto>(jsonResponse)
        } catch (_: Exception) {
            null
        }

        if (responseBody == null) {
            return@withContext false
        }

        val isNewVersionAvailable: Boolean = isNewVersionAvailable(responseBody)
        this@UpdateRestRepository.updateResponse = responseBody

        return@withContext isNewVersionAvailable
    }


    /**
     * Requests the download of the newest version of the app.
     * The download runs in a separate coroutine through an Android DownloadManager.
     */
    override suspend fun requestDownload() = suspendCancellableCoroutine { continuation ->
        if (isDownloading) {
            return@suspendCancellableCoroutine
        }
        isDownloading = true

        val downloadUrl: Uri? = try {
            updateResponse?.version?.downloadUrl?.toUri()
        } catch (_: Exception) {
            null
        }
        if (downloadUrl == null) {
            isDownloading = false
            return@suspendCancellableCoroutine
        }

        val apkFileName: String = downloadUrl.lastPathSegment ?: "${context.packageName}-update.apk"

        val downloadRequest = DownloadManager.Request(downloadUrl)
            .setTitle(context.getString(R.string.update_download_title))
            .setDescription(apkFileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId: Long = downloadManager.enqueue(downloadRequest)

        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id: Long = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != downloadId) {
                    return
                }

                context.unregisterReceiver(this)

                val uri: Uri = downloadManager.getUriForDownloadedFile(downloadId)
                Log.d("Update", "Begin installing '${uri}'")

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(installIntent)
                if (!continuation.isCompleted) {
                    continuation.resume(Unit)
                }
            }
        }

        context.registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_EXPORTED
        )

        continuation.invokeOnCancellation {
            try {
                context.unregisterReceiver(broadcastReceiver)
            }
            catch (_: Exception) { }
            finally {
                isDownloading = false
            }
        }
    }


    /**
     * Returns the code of the app version currently installed.
     *
     * @return  Current version code.
     */
    private fun getCurrentVersionCode(): Int {
        val packageName: String = context.packageName
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(packageName, 0)
        return packageInfo.longVersionCode.toInt()
    }


    /**
     * Checks whether the specified update response contains a version that is newer than the
     * version of the installed app.
     *
     * @param updateResponse    Update response.
     * @return                  Whether the response contains a newer version.
     */
    private fun isNewVersionAvailable(updateResponse: UpdateRootDto): Boolean {
        val currentVersionCode: Int = getCurrentVersionCode()
        val latestVersionCode: Int = updateResponse.version?.versionCode ?: -1
        return latestVersionCode > currentVersionCode
    }

}
