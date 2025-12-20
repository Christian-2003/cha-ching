package de.christian2003.chaching.domain.analysis.small

import java.util.UUID


/**
 * Value object models the type result for the small analysis. It contains the total sum for a type.
 * If no type is specified, this indicates that the instance is used to group multiple types.
 *
 * @param typeId    ID of the type.
 * @param sum       Sum of the transfer values of this type.
 */
data class SmallTypeResult(
    val typeId: UUID?,
    val sum: Double
)
