package de.christian2003.chaching.plugin.infrastructure.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.christian2003.chaching.plugin.infrastructure.db.entities.DeletedTypeEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID


/**
 * DAO through which to access the deleted types.
 */
@Dao
interface DeletedTypeDao {

    /**
     * Selects all DeletedTypeEntity-rows from the table.
     *
     * @return  List of all rows.
     */
    @Query("SELECT * FROM deletedTypes ORDER BY deletedAt DESC")
    fun selectAll(): Flow<List<DeletedTypeEntity>>


    /**
     * Selects a deleted type based on it's ID. If no type with the specified ID exists, null
     * is returned.
     *
     * @param typeId    ID of the type to select.
     * @return          Deleted type with the specified ID.
     */
    @Query("SELECT * FROM deletedTypes WHERE typeId = :typeId")
    fun selectById(typeId: UUID): DeletedTypeEntity?


    /**
     * Inserts the DeletedTypeEntity to the table.
     *
     * @param entity    Entity to insert.
     */
    @Insert
    suspend fun insert(entity: DeletedTypeEntity)


    /**
     * Deletes the DeletedTypeEntity-row from the table.
     *
     * @param entity    Entity to delete.
     */
    @Delete
    suspend fun delete(entity: DeletedTypeEntity)

}
