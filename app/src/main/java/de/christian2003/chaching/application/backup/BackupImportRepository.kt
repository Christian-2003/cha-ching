package de.christian2003.chaching.application.backup

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type


/**
 * Repository allows to import data from a backup to the repository.
 */
interface BackupImportRepository {

    /**
     * Imports the data passed as arguments based on the specified import strategy.
     *
     * @param transfers         List of transfers to import
     * @param types             List of types to import
     * @param importStrategy    Indicates what should happen to existing data during import.
     */
    suspend fun importFromBackup(transfers: List<Transfer>, types: List<Type>, importStrategy: ImportStrategy)

}
