package de.christian2003.chaching.plugin.infrastructure.db.entities

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
data class TransferEntity(

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

)
