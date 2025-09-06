package de.christian2003.chaching.plugin

import android.app.Application
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingDatabase
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingRepository
import de.christian2003.chaching.plugin.infrastructure.rest.HttpClientProvider
import okhttp3.OkHttpClient


class ChaChingApplication(): Application() {

    /**
     * Stores the database for the application.
     */
    private lateinit var database: ChaChingDatabase

    /**
     * Stores the repository for the application.
     */
    private var repository: ChaChingRepository? = null

    /**
     * Stores the OkHttpClient to use for REST requests in the app.
     */
    private var client: OkHttpClient? = null


    /**
     * Returns the repository of the application.
     *
     * @return  Repository.
     */
    fun getRepository(): ChaChingRepository {
        if (repository == null) {
            database = ChaChingDatabase.getInstance(this)
            repository = ChaChingRepository(database.transferDao, database.typeDao);
        }
        return repository!!
    }


    /**
     * Returns the OkHttpClient to use for all web requests.
     *
     * @return  OkHttpClient.
     */
    fun getClient(): OkHttpClient {
        if (client == null) {
            client = HttpClientProvider().provideOkHttpClient(this)
        }
        return client!!
    }

}
