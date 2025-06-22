package de.christian2003.chaching.model.transfers

import de.christian2003.chaching.domain.type.Type


/**
 * Connects a type to a value for the overview calc result.
 */
data class OverviewCalcResultItem(

    /**
     * Stores the type for the item.
     * If the overview result has too many types, multiple types are merged into a single item. In
     * such a case, this is null.
     */
    val type: Type?,

    /**
     * Stores the value for the item.
     */
    val value: Int

)
