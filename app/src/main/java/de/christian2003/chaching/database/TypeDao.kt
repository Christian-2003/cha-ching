package de.christian2003.chaching.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.flow.Flow
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
    fun selectAllTypesSortedByDate(): Flow<List<Type>>


    /**
     * Returns the type with the ID passed as argument. If no type with the ID specified exists,
     * null is returned.
     *
     * @return  Type with ID specified or null.
     */
    @Query("SELECT * FROM types WHERE typeId = :typeId")
    suspend fun selectTypeById(typeId: UUID): Type?


    /**
     * Inserts a new type into the database.
     *
     * @param type  Type to insert.
     */
    @Insert
    suspend fun insert(type: Type)


    /**
     * Deletes the type from the database.
     *
     * @param type  Type to delete.
     */
    @Delete
    suspend fun delete(type: Type)


    /**
     * Updates the transfer within the database.
     *
     * @param type  Type to update.
     */
    @Update
    suspend fun update(type: Type)

}
