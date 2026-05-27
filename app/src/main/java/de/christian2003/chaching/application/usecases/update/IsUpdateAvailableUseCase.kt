package de.christian2003.chaching.application.usecases.update

import de.christian2003.chaching.application.repository.UpdateRepository
import javax.inject.Inject


/**
 * Use case to check whether an app update is available.
 *
 * @param updateRepository  Repository for app updates.
 */
class IsUpdateAvailableUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {

    /**
     * Checks whether an app update is available.
     *
     * @return  Whether an update is available.
     */
    suspend fun isUpdateAvailable(): Boolean {
        return updateRepository.isUpdateAvailable()
    }

}
