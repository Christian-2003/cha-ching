package de.christian2003.chaching.plugin.infrastructure.db.mapper

import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.db.entities.TypeEntity


/**
 * Mapper maps the domain model type to the database entity.
 */
class TypeDbMapper {

    /**
     * Maps the database entity which is passed as argument to the domain model type.
     *
     * @param entity    Database entity to map to the domain model type.
     * @return          Domain model type.
     */
    fun toDomain(entity: TypeEntity): Type {
        return Type(
            name = entity.name,
            icon = entity.icon,
            id = entity.typeId,
            isHoursWorkedEditable = entity.isHoursWorkedEditable,
            isEnabledInQuickAccess = entity.isEnabledInQuickAccess,
            created = entity.created,
            edited = entity.edited
        )
    }


    /**
     * Maps the domain model type which is passed as argument to the database entity.
     *
     * @param domain    Domain model type to map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: Type): TypeEntity {
        return TypeEntity(
            name = domain.name,
            icon = domain.icon,
            typeId = domain.id,
            isHoursWorkedEditable = domain.isHoursWorkedEditable,
            isEnabledInQuickAccess = domain.isEnabledInQuickAccess,
            created = domain.created,
            edited = domain.edited
        )
    }

}
