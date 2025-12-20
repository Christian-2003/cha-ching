package de.christian2003.chaching.plugin.infrastructure.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import de.christian2003.chaching.plugin.infrastructure.db.entities.DeletedTypeEntity
import de.christian2003.chaching.plugin.infrastructure.db.entities.TypeEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID


/**
 * Data access object through which to access types.
 */
@Dao
interface TypeDao {

    /**
     * Returns a list of all types sorted by date of creation.
     *
     * @return  List of all types.
     */
    @Query("SELECT * FROM types ORDER BY created DESC")
    fun selectAllTypesSortedByDate(): Flow<List<TypeEntity>>


    /**
     * Returns a list of all types that are not in trash sorted by date of creation.
     *
     * @return  List of all types.
     */
    @Query("SELECT * FROM types t WHERE NOT EXISTS (SELECT 1 FROM deletedTypes d WHERE d.typeId = t.typeId) ORDER BY created DESC")
    fun selectAllTypesNotInTrashSortedByDate(): Flow<List<TypeEntity>>


    /**
     * Returns the type with the ID passed as argument. If no type with the ID specified exists,
     * null is returned.
     *
     * @return  Type with ID specified or null.
     */
    @Query("SELECT * FROM types WHERE typeId = :typeId")
    suspend fun selectTypeById(typeId: UUID): TypeEntity?


    /**
     * Inserts a new type into the database.
     *
     * @param typeEntity  Type to insert.
     */
    @Insert
    suspend fun insert(typeEntity: TypeEntity)


    /**
     * Deletes the type from the database.
     *
     * @param typeEntity  Type to delete.
     */
    @Delete
    suspend fun delete(typeEntity: TypeEntity)


    /**
     * Updates the transfer within the database.
     *
     * @param typeEntity  Type to update.
     */
    @Update
    suspend fun update(typeEntity: TypeEntity)


    @Query("DELETE FROM types")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAndIgnore(typeEntities: List<TypeEntity>)

    @Upsert
    suspend fun upsert(typeEntities: List<TypeEntity>)

}
