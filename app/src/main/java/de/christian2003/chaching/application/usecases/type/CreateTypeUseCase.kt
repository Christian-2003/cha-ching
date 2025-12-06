package de.christian2003.chaching.application.usecases.type

import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.type.TypeIcon
import de.christian2003.chaching.domain.type.TypeMetadata
import javax.inject.Inject


/**
 * Use case to create a new type for transfers.
 *
 * @param repository    Repository through which to access types.
 */
class CreateTypeUseCase @Inject constructor(
    private val repository: TypeRepository
) {

    /**
     * Creates a new type with the values specified.
     *
     * @param name                      Name for the type.
     * @param icon                      Icon for the type.
     * @param isHoursWorkedEditable     Whether the "Hours worked" field should be editable.
     * @param isEnabledInQuickAccess    Whether the type is visible in quick access.
     */
    suspend fun createType(
        name: String,
        icon: TypeIcon,
        isHoursWorkedEditable: Boolean,
        isEnabledInQuickAccess: Boolean
    ) {
        val type = Type(
            name = name,
            icon = icon,
            metadata = TypeMetadata(
                isHoursWorkedEditable = isHoursWorkedEditable,
                isEnabledInQuickAccess = isEnabledInQuickAccess
            )
        )

        repository.createNewType(type)
    }

}
