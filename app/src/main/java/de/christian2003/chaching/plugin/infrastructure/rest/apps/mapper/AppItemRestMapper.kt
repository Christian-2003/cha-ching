package de.christian2003.chaching.plugin.infrastructure.rest.apps.mapper

import de.christian2003.chaching.domain.apps.AppItem
import de.christian2003.chaching.plugin.infrastructure.rest.apps.dto.AppItemDto


/**
 * Mapper which maps the REST DTO to the domain model AppItem.
 */
class AppItemRestMapper {

    /**
     * Maps the REST DTO that is passed as argument to the domain model AppItem.
     *
     * @param dto   REST DTO to map to the domain model AppItem.
     * @return      Domain model AppItem.
     */
    fun toDomain(dto: AppItemDto): AppItem {
        return AppItem(
            packageName = dto.packageName,
            url = dto.url,
            displayName = dto.name,
            iconUrl = dto.iconUrl
        )
    }

}
