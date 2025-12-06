package de.christian2003.chaching.plugin.infrastructure.db.mapper

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferMetadata
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.plugin.infrastructure.db.entities.TransferEntity


/**
 * Mapper maps the domain model transfer to the database entity.
 */
class TransferDbMapper {

    /**
     * Maps the database entity that is passed as argument to the domain model transfer.
     *
     * @param entity    Database entity to map to the domain model transfer.
     * @return          Domain model transfer.
     */
    fun toDomain(entity: TransferEntity): Transfer {
        return Transfer(
            transferValue = TransferValue(
                value = entity.value,
                date = entity.valueDate,
                isSalary = entity.isSalary
            ),
            hoursWorked = entity.hoursWorked,
            type = entity.type,
            id = entity.transferId,
            metadata = TransferMetadata(
                created = entity.created,
                edited = entity.edited
            )
        )
    }


    /**
     * Maps the domain model transfer that is passed as argument to the database entity.
     *
     * @param domain    Domain model transfer to map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: Transfer): TransferEntity {
        return TransferEntity(
            value = domain.transferValue.value,
            hoursWorked = domain.hoursWorked,
            isSalary = domain.transferValue.isSalary,
            valueDate = domain.transferValue.date,
            type = domain.type,
            transferId = domain.id,
            created = domain.metadata.created,
            edited = domain.metadata.edited
        )
    }

}
