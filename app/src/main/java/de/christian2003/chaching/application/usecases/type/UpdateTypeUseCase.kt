package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.type.TypeIcon
import java.util.UUID
import javax.inject.Inject


/**
 * Use case to update an existing type.
 *
 * @param repository    Repository through which to access types.
 */
class UpdateTypeUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Updates an existing type with the values specified. If no type with the specified ID exists,
     * nothing happens.
     *
     * @param typeId                    ID of the type to update.
     * @param name                      New name for the type.
     * @param icon                      New icon for the type.
     * @param isHoursWorkedEditable     Whether the "Hours worked" field should be editable.
     * @param isEnabledInQuickAccess    Whether the type is visible in quick access.
     */
    suspend fun updateType(
        typeId: UUID,
        name: String,
        icon: TypeIcon,
        isHoursWorkedEditable: Boolean,
        isEnabledInQuickAccess: Boolean
    ) {
        val type: Type? = repository.getTypeById(typeId)
        if (type != null) {
            type.name = name
            type.icon = icon
            type.metadata = type.metadata.copy(
                isHoursWorkedEditable = isHoursWorkedEditable,
                isEnabledInQuickAccess = isEnabledInQuickAccess
            )
            repository.updateExistingType(type)
        }
    }

}
