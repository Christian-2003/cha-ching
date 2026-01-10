package de.christian2003.chaching.plugin.infrastructure.backup.mapper

import de.christian2003.chaching.domain.type.DeletedType
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.backup.dto.DeletedTypeDto


/**
 * Mapper maps the domain model DeletedType to the backup data transfer object (DTO).
 */
class DeletedTypeBackupMapper {

    /**
     * Maps the backup DTO that is passed as argument to the domain model DeletedType.
     *
     * @param dto   Backup DTO to map to the domain model DeletedType.
     * @param type  Type for which the DeletedType is created.
     * @return      Domain model DeletedType.
     */
    fun toDomain(dto: DeletedTypeDto, type: Type): DeletedType {
        return DeletedType(
            type = type,
            deletedAt = dto.deletedAt
        )
    }


    /**
     * Maps the domain model type that is passed as argument to the backup DTO.
     *
     * @param domain    Domain model type to map to the backup DTO.
     * @return          Backup DTO.
     */
    fun toDto(domain: DeletedType): DeletedTypeDto {
        return DeletedTypeDto(
            typeId = domain.type.id,
            deletedAt = domain.deletedAt
        )
    }

}
