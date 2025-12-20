package de.christian2003.chaching.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID


@Entity(
    tableName = "deletedTypes",
    foreignKeys = [ForeignKey(
        entity = TypeEntity::class,
        parentColumns = arrayOf("typeId"),
        childColumns = arrayOf("typeId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DeletedTypeEntity(
    @ColumnInfo("typeId") @PrimaryKey val typeId: UUID = UUID.randomUUID(),
    @ColumnInfo("deletedAt") val deletedAt: LocalDateTime = LocalDateTime.now()
)
