package de.christian2003.chaching.database.entities

import androidx.room.Embedded
import androidx.room.Relation


data class TransferWithType(

    @Embedded
    val transfer: Transfer,

    @Relation(
        parentColumn = "type",
        entityColumn = "typeId"
    )
    val type: Type

)
