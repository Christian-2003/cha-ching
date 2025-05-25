package de.christian2003.chaching.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.christian2003.chaching.model.transfers.Currency
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "transfers")
class Transfer(

	@PrimaryKey
	val id: UUID = UUID.randomUUID(),

	val value: Int,

	val hoursWorked: Int,

	val currency: Currency,

	val isSalary: Boolean,

	val timestamp: LocalDateTime

)
