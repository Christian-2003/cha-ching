package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import java.util.UUID
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
     * @param typeId    ID of the type to move to the trash bin.
     */
    suspend fun moveTypeToTrash(typeId: UUID) {
        val type: Type? = repository.getTypeById(typeId)
        if (type != null) {
            repository.moveTypeToTrash(type)
        }
    }

}
