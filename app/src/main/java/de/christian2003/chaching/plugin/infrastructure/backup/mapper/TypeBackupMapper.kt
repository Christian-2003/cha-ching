package de.christian2003.chaching.plugin.infrastructure.backup.mapper

import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TypeDto


/**
 * Mapper maps the domain model type to the backup data transfer object (DTO).
 */
class TypeBackupMapper {

    /**
     * Maps the backup DTO that is passed as argument to the domain model type.
     *
     * @param dto   Backup DTO to map to the domain model type.
     * @return      Domain model type.
     */
    fun toDomain(dto: TypeDto): Type {
        return Type(
            name = dto.name,
            icon = dto.icon,
            id = dto.id,
            isHoursWorkedEditable = dto.isHoursWorkedEditable,
            isEnabledInQuickAccess = dto.isEnabledInQuickAccess,
            created = dto.created,
            edited = dto.edited
        )
    }


    /**
     * Maps the domain model type that is passed as argument to the backup DTO.
     *
     * @param domain    Domain model type to map to the backup DTO.
     * @return          Backup DTO.
     */
    fun toDto(domain: Type): TypeDto {
        return TypeDto(
            name = domain.name,
            icon = domain.icon,
            id = domain.id,
            isHoursWorkedEditable = domain.isHoursWorkedEditable,
            isEnabledInQuickAccess = domain.isEnabledInQuickAccess,
            created = domain.created,
            edited = domain.edited
        )
    }

}
