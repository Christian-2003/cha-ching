package de.christian2003.chaching.plugin.infrastructure.backup.mapper

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferMetadata
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TransferDto


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
            transferValue = TransferValue(
                value = dto.value,
                date = dto.valueDate,
                isSalary = dto.isSalary
            ),
            hoursWorked = dto.hoursWorked,
            type = dto.type,
            id = dto.id,
            metadata = TransferMetadata(
                created = dto.created,
                edited = dto.edited
            )
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
            value = domain.transferValue.value,
            hoursWorked = domain.hoursWorked,
            isSalary = domain.transferValue.isSalary,
            valueDate = domain.transferValue.date,
            type = domain.type,
            id = domain.id,
            created = domain.metadata.created,
            edited = domain.metadata.edited
        )
    }

}
