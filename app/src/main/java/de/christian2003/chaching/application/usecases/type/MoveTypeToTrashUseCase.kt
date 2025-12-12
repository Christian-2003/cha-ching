package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import javax.inject.Inject


/**
 * Use case to move types to the trash bin.
 *
 * @param repository    Repository through which to access types.
 */
class MoveTypeToTrashUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Moves the specified type to the trash bin.
     *
     * @param type  Type to move to the trash.
     */
    suspend fun moveTypeToTrash(type: Type) {
        repository.moveTypeToTrash(type)
    }

}
