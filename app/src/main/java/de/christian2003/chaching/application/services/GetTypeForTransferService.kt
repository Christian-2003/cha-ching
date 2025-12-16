package de.christian2003.chaching.application.services

import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import java.util.UUID
import javax.inject.Inject


/**
 * Service through which to get the type for a transfer.
 *
 * @param repository    Repository through which to access types.
 */
class GetTypeForTransferService @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Returns the type for the specified transfer. If no type exists, null is returned.
     *
     * @param transfer  Transfer whose type to return.
     * @return          Type of the specified transfer or null.
     */
    suspend fun getType(transfer: Transfer): Type? {
        val typeId: UUID = transfer.type
        val type: Type? = repository.getTypeById(typeId)
        return type
    }

}
