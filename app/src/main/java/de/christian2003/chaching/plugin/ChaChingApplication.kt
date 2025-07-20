package de.christian2003.chaching.plugin

import android.app.Application
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingDatabase
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingRepository


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

}
