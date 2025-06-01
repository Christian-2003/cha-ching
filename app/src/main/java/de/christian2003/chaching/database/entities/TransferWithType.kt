package de.christian2003.chaching.database.entities

import androidx.room.Embedded
import androidx.room.Relation


/**
 * Connects each transfer with a type.
 */
data class TransferWithType(

    /**
     * Transfer.
     */
    @Embedded
    val transfer: Transfer,

    /**
     * Type of the transfer.
     */
    @Relation(
        parentColumn = "type",
        entityColumn = "typeId"
    )
    val type: Type

)
