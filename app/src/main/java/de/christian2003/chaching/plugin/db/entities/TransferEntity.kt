package de.christian2003.chaching.plugin.db.entities

import android.icu.text.DecimalFormat
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


/**
 * Database entity storing the data for a transfer.
 */
@Entity(tableName = "transfers",
	foreignKeys = [ForeignKey(
		entity = TypeEntity::class,
		parentColumns = arrayOf("typeId"),
		childColumns = arrayOf("type"),
		onDelete = ForeignKey.CASCADE
	)]
)
class TransferEntity(

	/**
	 * Value of the transfer in cents, e.g. 500 (for $ 5.00).
	 */
	var value: Int,

	/**
	 * Hours worked.
	 */
	var hoursWorked: Int,

	/**
	 * Indicates whether the transfer is a salary.
	 */
	var isSalary: Boolean,

	/**
	 * Date on which the transfer takes effect, e.g. 2025-05-29
	 */
	var valueDate: LocalDate,

	/**
	 * Type of the transfer.
	 */
	val type: UUID,

	/**
	 * UUID of the transfer.
	 */
	@PrimaryKey
	val transferId: UUID = UUID.randomUUID(),

	/**
	 * Date time on which the transfer was created by the user. This is NOT the value date. Instead,
	 * this value exists for statistical purposes.
	 */
	val created: LocalDateTime = LocalDateTime.now(),

	/**
	 * Date time on which the transfer was last edited by the user. This is fpr statistical
	 * purposes.
	 */
	var edited: LocalDateTime = LocalDateTime.now()

) {

	/**
	 * Returns the transfer value (i.e. "value") formatted according to the current locale.
	 *
	 * Examples for value = 123456:
	 * * en-US: "1,234.56"
	 * * de-DE: "1.234,56"
	 *
	 * @return	Formatted value.
	 */
	fun getFormattedValue(): String {
		val numberFormat = DecimalFormat("#,###.00")
		val formattedNumber: String = numberFormat.format(value.toDouble() / 100)
		return formattedNumber
	}

}
