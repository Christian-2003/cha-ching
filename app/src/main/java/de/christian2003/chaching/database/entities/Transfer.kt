package de.christian2003.chaching.database.entities

import android.icu.text.DecimalFormat
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.christian2003.chaching.model.transfers.Currency
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


@Entity(tableName = "transfers",
	foreignKeys = [ForeignKey(
		entity = Type::class,
		parentColumns = arrayOf("typeId"),
		childColumns = arrayOf("type"),
		onDelete = ForeignKey.CASCADE
	)]
)
class Transfer(

	var value: Int,

	var hoursWorked: Int,

	var isSalary: Boolean,

	var valueDate: LocalDate,

	val type: UUID,

	@PrimaryKey
	val transferId: UUID = UUID.randomUUID(),

	val created: LocalDateTime = LocalDateTime.now(),

	var edited: LocalDateTime = LocalDateTime.now()

) {

	fun getFormattedValue(): String {
		val numberFormat = DecimalFormat("#,###.00")
		val formattedNumber: String = numberFormat.format(value.toDouble() / 100)
		return formattedNumber
	}

}
