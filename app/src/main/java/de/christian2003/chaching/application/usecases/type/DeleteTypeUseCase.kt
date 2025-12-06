package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to delete an existing type.
 *
 * @param repository    Repository through which to access the types.
 */
class DeleteTypeUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Deletes the type with the specified ID. If no type exists with this ID, nothing happens.
     *
     * @param typeId    ID of the type to delete.
     */
    suspend fun deleteType(
        typeId: UUID
    ) {
        val type: Type? = repository.getTypeById(typeId)
        if (type != null) {
            repository.deleteType(type)
        }
    }

}
