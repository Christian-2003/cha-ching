package de.christian2003.chaching.domain.repository

import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.Flow
import java.util.UUID


/**
 * Repository allows access and manipulation of types.
 */
interface TypeRepository {

    /**
     * Returns a list of all types.
     *
     * @return  List of all types.
     */
    fun getAllTypes(): Flow<List<Type>>


    /**
     * Returns a list of all types that are in the trash bin.
     *
     * @return  List of all types that are in the trash bin.
     */
    suspend fun getAllTypesInTrash(): Flow<List<Type>>


    /**
     * Returns a list of all types that are NOT in the trash bin.
     *
     * @return  List of all types that are not in the trash bin.
     */
    suspend fun getAllTypesNotInTrash(): Flow<List<Type>>


    /**
     * Returns the type with the ID specified. If no type of the specified ID exists, null is
     * returned.
     *
     * @param id    ID of the type to return.
     * @return      Type of the ID specified.
     */
    suspend fun getTypeById(id: UUID): Type?


    /**
     * Creates a new type.
     *
     * @param type  Type to create.
     */
    suspend fun createNewType(type: Type)


    /**
     * Updates the existing type with the data passed as argument. The existing type which is
     * updated with the new type is determined by the ID. This means that the ID of the type passed
     * must be identical to the ID of the type to update.
     *
     * @param type  Data with which to update the existing type.
     */
    suspend fun updateExistingType(type: Type)


    /**
     * Moves the specified type to the trash bin.
     *
     * @param type  Type to move to the trash bin.
     */
    suspend fun moveTypeToTrash(type: Type)


    /**
     * Restores the specified type from the trash bin.
     *
     * @param type  Type to restore from the trash bin.
     */
    suspend fun restoreTypeFromTrash(type: Type)


    /**
     * Deletes the type which is passed as argument.
     *
     * @param type  Type to delete.
     */
    suspend fun deleteType(type: Type)

}
