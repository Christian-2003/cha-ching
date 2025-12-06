package de.christian2003.chaching.application.usecases.apps

import de.christian2003.chaching.domain.apps.AppItem
import de.christian2003.chaching.domain.repository.AppsRepository


/**
 * Use case to get a list of all apps.
 *
 * @param repository    Repository to access apps.
 */
class GetAllAppsUseCase(
    private val repository: AppsRepository
) {

    /**
     * Returns a list of all apps.
     *
     * @return  List of all apps.
     */
    suspend fun getAllApps(): List<AppItem> {
        return repository.getApps()
    }

}
