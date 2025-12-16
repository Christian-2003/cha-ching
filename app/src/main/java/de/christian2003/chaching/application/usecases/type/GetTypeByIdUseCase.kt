package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to get a type by it's ID.
 *
 * @param repository    Repository to access types.
 */
class GetTypeByIdUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Returns the type with the specified ID. If no type with the ID exists, null is returned.
     *
     * @param typeId    ID of the type to return.
     * @return          Type with the specified ID or null.
     */
    suspend fun getTypeById(typeId: UUID): Type? {
        return repository.getTypeById(typeId)
    }

}
