package de.christian2003.chaching.plugin.backup.mapper

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.plugin.backup.dto.TransferDto


/**
 * Mapper maps the domain model transfer to the backup data transfer object (DTO).
 */
class TransferBackupMapper {

    /**
     * Maps the backup DTO that is passed as argument to the domain model transfer.
     *
     * @param dto   Backup DTO to map to the domain model transfer.
     * @return      Domain model transfer.
     */
    fun toDomain(dto: TransferDto): Transfer {
        return Transfer(
            value = dto.value,
            hoursWorked = dto.hoursWorked,
            isSalary = dto.isSalary,
            valueDate = dto.valueDate,
            type = dto.type,
            id = dto.id,
            created = dto.created,
            edited = dto.edited
        )
    }


    /**
     * Maps the domain model transfer that is passed as argument to the backup DTO.
     *
     * @param domain    Domain model transfer to map to the backup DTO.
     * @return          Backup DTO.
     */
    fun toDto(domain: Transfer): TransferDto {
        return TransferDto(
            value = domain.value,
            hoursWorked = domain.hoursWorked,
            isSalary = domain.isSalary,
            valueDate = domain.valueDate,
            type = domain.type,
            id = domain.id,
            created = domain.created,
            edited = domain.edited
        )
    }

}
