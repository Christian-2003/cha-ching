package de.christian2003.chaching.plugin.db.entities

import androidx.room.Embedded
import androidx.room.Relation


/**
 * Connects each transfer with a type.
 */
data class TransferWithTypeEntity(

    /**
     * Transfer.
     */
    @Embedded
    val transfer: TransferEntity,

    /**
     * Type of the transfer.
     */
    @Relation(
        parentColumn = "type",
        entityColumn = "typeId"
    )
    val typeEntity: TypeEntity

)
