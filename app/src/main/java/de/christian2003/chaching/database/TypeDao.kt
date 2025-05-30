package de.christian2003.chaching.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
interface TypeDao {

    @Query("SELECT * FROM types ORDER BY created DESC")
    fun selectAllTypesSortedByDate(): Flow<List<Type>>


    @Query("SELECT * FROM types WHERE typeId=:typeId")
    suspend fun selectTypeById(typeId: UUID): Type


    @Insert
    suspend fun insert(type: Type)


    @Delete
    suspend fun delete(type: Type)


    @Update
    suspend fun update(type: Type)

}
