package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import java.util.UUID
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
     * @param typeId    ID of the type to restore from the trash bin.
     */
    suspend fun restoreTypeFromTrash(typeId: UUID) {
        val type: Type? = repository.getTypeById(typeId)
        if (type != null) {
            repository.restoreTypeFromTrash(type)
        }
    }

}
