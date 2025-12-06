package de.christian2003.chaching.domain.type

import java.time.LocalDateTime


/**
 * Metadata for transfer types.
 *
 * @param created                   Date time on which the type was created.
 * @param edited                    Date time on which the type was edited.
 * @param isHoursWorkedEditable     Indicates whether the "Hours worked" field should be editable when
 *                                  creating a new transfer for this type.
 * @param isEnabledInQuickAccess    Indicates whether transfers for this type can be created through
 *                                  the quick access on the main screen.
 */
data class TypeMetadata(
    val created: LocalDateTime = LocalDateTime.now(),
    val edited: LocalDateTime = LocalDateTime.now(),
    val isHoursWorkedEditable: Boolean = true,
    val isEnabledInQuickAccess: Boolean = true
) {

    /**
     * Initializes this data class.
     */
    init {
        require(!edited.isBefore(created)) { "Edited date cannot be before created date" }
    }

}
