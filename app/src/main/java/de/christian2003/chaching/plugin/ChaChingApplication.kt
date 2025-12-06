package de.christian2003.chaching.plugin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingDatabase
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingRepository


/**
 * Application for the app.
 */
@HiltAndroidApp
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
     * Returns the repository of the application.
     * The repository could technically be provided through hilt as well. This way, we would not need
     * to maintain this code. However, the repository is not only used within the app, but also the
     * app widgets. Therefore, we choose to keep the repository instantiation in this class.
     *
     * @return  Repository.
     */
    fun getRepository(): ChaChingRepository {
        if (repository == null) {
            database = ChaChingDatabase.getInstance(this)
            repository = ChaChingRepository(database.transferDao, database.typeDao)
        }
        return repository!!
    }

}
