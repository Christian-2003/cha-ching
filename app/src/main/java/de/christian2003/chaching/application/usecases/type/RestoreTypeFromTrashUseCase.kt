package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import javax.inject.Inject


/**
 * Use case to restore types from the trash bin.
 *
 * @param repository    Repository through which to access types.
 */
class RestoreTypeFromTrashUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Restores the specified type from the trash bin.
     *
     * @param type  Type to restore from the trash bin.
     */
    suspend fun restoreTypeFromTrash(type: Type) {
        repository.restoreTypeFromTrash(type)
    }

}
