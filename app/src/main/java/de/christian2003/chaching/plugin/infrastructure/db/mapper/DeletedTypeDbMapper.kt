package de.christian2003.chaching.plugin.infrastructure.db.mapper

import de.christian2003.chaching.domain.type.DeletedType
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.plugin.infrastructure.db.entities.DeletedTypeEntity


/**
 * Mapper maps the domain model type to the database entity.
 */
class DeletedTypeDbMapper {

    /**
     * Maps the database entity which is passed as argument to the domain model type.
     *
     * @param entity    Database entity to map to the domain model type.
     * @param type      Type queried from the database.
     * @return          Domain model type.
     */
    fun toDomain(entity: DeletedTypeEntity, type: Type): DeletedType {
        return DeletedType(
            type = type,
            deletedAt = entity.deletedAt
        )
    }


    /**
     * Maps the domain model type which is passed as argument to the database entity.
     *
     * @param domain    Domain model type to map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: DeletedType): DeletedTypeEntity {
        return DeletedTypeEntity(
            typeId = domain.type.id,
            deletedAt = domain.deletedAt
        )
    }

}
