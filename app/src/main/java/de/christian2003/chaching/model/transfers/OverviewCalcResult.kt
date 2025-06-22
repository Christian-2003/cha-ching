package de.christian2003.chaching.model.transfers

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type


/**
 * Stores the result for the overview on the main screen.
 */
class OverviewCalcResult(

    /**
     * Transfers for which to generate the overview.
     */
    val transfers: List<Transfer>,

    val types: List<Type>

) {

    /**
     * Total value.
     */
    var totalValue: Int = 0

    /**
     * Overview comparison connection to get some additional (useless) info to the user.
     */
    var overviewComparisonConnection: OverviewComparisonConnection

    /**
     * Values by types.
     */
    var results: List<OverviewCalcResultItem>


    /**
     * Calculates the overview results.
     */
    init {
        val valueByType: Map<Type, Int> = mapTransfersToType()
        results = convertMapToListOfOverviewCalcResultItems(valueByType)
        results.forEach { item ->
            totalValue += item.value
        }
        overviewComparisonConnection = OverviewComparisonConnection.getRandomComparisonConnection()
    }


    /**
     * Maps the values of all transfers to a type.
     *
     * @return  Value mapped to type.
     */
    private fun mapTransfersToType(): Map<Type, Int> {
        val map: MutableMap<Type, Int> = mutableMapOf()
        transfers.forEach { transfer ->
            val type: Type? = getTypeForTransfer(transfer)
            if (type != null) {
                var value: Int? = map[type]
                if (value == null) {
                    value = transfer.value
                }
                else {
                    value += transfer.value
                }
                map[type] = value
            }
        }
        return map.toMap()
    }


    /**
     * Converts the passed map (value to type) into a list containing 3 items max.
     *
     * @param map   Map which maps the value to type.
     * @return      List of result items.
     */
    private fun convertMapToListOfOverviewCalcResultItems(map: Map<Type, Int>): List<OverviewCalcResultItem> {
        val list: MutableList<OverviewCalcResultItem> = mutableListOf()
        val sortedPairs: List<Pair<Type, Int>> = map.toList().sortedByDescending { item -> item.second }

        if (sortedPairs.size <= 3) {
            // 3 or less types:
            sortedPairs.forEach { pair ->
                list.add(OverviewCalcResultItem(pair.first, pair.second))
            }
        }
        else {
            // More than 3 types:
            list.add(OverviewCalcResultItem(sortedPairs[0].first, sortedPairs[0].second))
            list.add(OverviewCalcResultItem(sortedPairs[1].first, sortedPairs[1].second))
            var value = 0
            for (i in 2..sortedPairs.size - 1) {
                value += sortedPairs[i].second
            }
            list.add(OverviewCalcResultItem(null, value))
        }

        return list.toList()
    }


    private fun getTypeForTransfer(transfer: Transfer): Type? {
        var transferType: Type? = null
        types.forEach { type ->
            if (type.id == transfer.type) {
                transferType = type
                return@forEach
            }
        }
        return transferType
    }

}
