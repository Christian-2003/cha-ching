package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.Flow


/**
 * Use case to get a list of all types.
 *
 * @param repository    Repository through which to access types.
 */
class GetAllTypesUseCase(
    private val repository: TypeRepository
) {

    /**
     * Returns a list of all types.
     *
     * @return  Flow which contains a list of all types.
     */
    fun getAllTypes(): Flow<List<Type>> {
        return repository.getAllTypes()
    }

}
