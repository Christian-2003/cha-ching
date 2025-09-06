package de.christian2003.chaching.plugin.infrastructure.rest

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File


/**
 * Provider for an OkHttp client.
 */
class HttpClientProvider {

    /**
     * Provides the OkHttp client to use within the app.
     *
     * @param context   Context.
     * @return          OkHttp client.
     */
    fun provideOkHttpClient(context: Context): OkHttpClient {
        val cacheSize: Long = 10L * 1024 * 1024 //10 MB
        val cacheDir = File(context.applicationContext.cacheDir, "http_cache")
        return OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .addNetworkInterceptor { chain ->
                val request: Request = chain.request()
                val response: Response = chain.proceed(request)
                val maxAge = 900 //900 seconds = 15 minutes

                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
            }
            .build()
    }

}
