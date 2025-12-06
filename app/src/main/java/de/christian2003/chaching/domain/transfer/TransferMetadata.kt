package de.christian2003.chaching.domain.transfer

import java.time.LocalDateTime


/**
 * Metadata for transfers.
 *
 * @param created   Date time on which the transfer was created.
 * @param edited    Date time on which the transfer was edited.
 */
data class TransferMetadata(
    val created: LocalDateTime = LocalDateTime.now(),
    var edited: LocalDateTime = LocalDateTime.now()
) {

    /**
     * Initializes this data class.
     */
    init {
        require(!edited.isBefore(created)) { "Edited date cannot be before created date" }
    }

}
