package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to get a list of all types that are in trash.
 *
 * @param repository    Repository through which to access types.
 */
class GetAllTypesInTrashUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Returns all types that are in trash.
     *
     * @return  List of all types that are in trash.
     */
    fun getAllTypesInTrash(): Flow<List<Type>> {
        return repository.getAllTypesInTrash()
    }

}
