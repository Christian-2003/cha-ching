package de.christian2003.chaching.application.usecases.update

import de.christian2003.chaching.application.repository.UpdateRepository
import javax.inject.Inject


/**
 * Use case to request a download of the newest app version.
 *
 * @param updateRepository  Repository for app updates.
 */
class RequestDownloadUpdateUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {

    /**
     * Requests to download the newest version.
     */
    suspend fun requestDownload() {
        updateRepository.requestDownload()
    }

}
