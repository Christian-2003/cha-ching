package de.christian2003.chaching.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.christian2003.chaching.model.transfers.TypeIcon
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "types")
class Type(

    var name: String,

    var icon: TypeIcon,

    @PrimaryKey
    val typeId: UUID = UUID.randomUUID(),

    val created: LocalDateTime = LocalDateTime.now(),

    var edited: LocalDateTime = LocalDateTime.now()

)
