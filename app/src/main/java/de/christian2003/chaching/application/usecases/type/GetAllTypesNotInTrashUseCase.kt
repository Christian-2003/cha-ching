package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to get all types that are not in trash.
 *
 * @param repository    Repository to access types.
 */
class GetAllTypesNotInTrashUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Returns all types that are not in trash.
     *
     * @return  List of all types that are not in trash.
     */
    fun getAllTypesNotInTrash(): Flow<List<Type>> {
        return repository.getAllTypesNotInTrash()
    }

}
