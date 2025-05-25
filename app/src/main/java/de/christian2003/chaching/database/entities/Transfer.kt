package de.christian2003.chaching.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.christian2003.chaching.model.transfers.Currency
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "transfers")
class Transfer(

	val value: Int,

	val hoursWorked: Int,

	val currency: Currency,

	val isSalary: Boolean,

	val valueDate: LocalDate,

	@PrimaryKey
	val id: UUID = UUID.randomUUID(),

	val created: LocalDateTime = LocalDateTime.now(),

	val edited: LocalDateTime = LocalDateTime.now()

)
