package de.christian2003.chaching.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.christian2003.chaching.domain.type.TypeIcon
import java.time.LocalDateTime
import java.util.UUID


/**
 * Database entity for storing types.
 *
 * @param name                      Display name of the type.
 * @param icon                      Icon of the type.
 * @param typeId                    UUID of the type.
 * @param isHoursWorkedEditable     Whether the hoursWorked-field of the transfers for this type can be edited.
 * @param isEnabledInQuickAccess    Whether the type is available through the "+"-FAB on the main screen.
 * @param isSalaryByDefault         Whether the transfers of this type are salaries by default.
 * @param created                   Date time on which the type was created. This is for statistical purposes.
 * @param edited                    Date time on which the type was last edited. This is for statistical purposes.
 */
@Entity(tableName = "types")
data class TypeEntity(
    @ColumnInfo("name") val name: String,
    @ColumnInfo("icon") val icon: TypeIcon,
    @ColumnInfo("typeId") @PrimaryKey val typeId: UUID = UUID.randomUUID(),
    @ColumnInfo("isHoursWorkedEditable") val isHoursWorkedEditable: Boolean = true,
    @ColumnInfo("isEnabledInQuickAccess") val isEnabledInQuickAccess: Boolean = true,
    @ColumnInfo("isSalaryByDefault") val isSalaryByDefault: Boolean = true,
    @ColumnInfo("created") val created: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo("edited") val edited: LocalDateTime = LocalDateTime.now()
) {

    override fun hashCode(): Int {
        return typeId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is TypeEntity) {
            return other.typeId == typeId
        }
        return false
    }

}
