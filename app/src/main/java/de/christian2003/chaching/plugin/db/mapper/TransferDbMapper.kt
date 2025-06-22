package de.christian2003.chaching.plugin.db.mapper

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.plugin.db.entities.TransferEntity


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
            value = entity.value,
            hoursWorked = entity.hoursWorked,
            isSalary = entity.isSalary,
            valueDate = entity.valueDate,
            type = entity.type,
            id = entity.transferId,
            created = entity.created,
            edited = entity.edited
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
            value = domain.value,
            hoursWorked = domain.hoursWorked,
            isSalary = domain.isSalary,
            valueDate = domain.valueDate,
            type = domain.type,
            transferId = domain.id,
            created = domain.created,
            edited = domain.edited
        )
    }

}
