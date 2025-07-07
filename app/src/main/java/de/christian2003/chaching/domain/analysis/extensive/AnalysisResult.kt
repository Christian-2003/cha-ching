package de.christian2003.chaching.domain.analysis.extensive

import de.christian2003.chaching.domain.type.Type


/**
 * Value object stores the analysis result.
 */
data class AnalysisResult(

    /**
     * Data for the diagram that displays the sum of transfer values by type.
     */
    val transfersByTypeDiagram: AnalysisDiagram,

    /**
     * Data for the diagram that displays the cumulated sum of transfer values by type.
     */
    val cumulatedTransfersByTypeDiagram: AnalysisDiagram,

    /**
     * Stores the sum of transfers by type.
     * If some types are squashed into a single entry within the map, the entry's key is null!
     */
    val totalTransferByType: Map<Type?, Int>,

    /**
     * Stores the average transfer value by type.
     * If some types are squashed into a single entry within the map, the entry's key is null!
     */
    val averageTransferByType: Map<Type?, Int>,

    /**
     * Total transfer value in the analyzed time period.
     */
    val total: Int,

    /**
     * Average transfer value in the analyzed time period.
     */
    val average: Int

) {

    /**
     * Builder for the analysis result.
     */
    class Builder(

        /**
         * Precision for which to create the analysis result.
         */
        private val precision: AnalysisPrecision

    ) {

        /**
         * Data for the diagram that displays the sum of transfer values by type.
         */
        private val transfersByType: MutableList<AnalysisDiagramLine> = mutableListOf()

        /**
         * Data for the diagram that displays the cumulated sum of transfer values by type.
         */
        private val cumulatedTransfersByType: MutableList<AnalysisDiagramLine> = mutableListOf()

        /**
         * Stores the sum of transfers by type.
         */
        private var totalTransferByType: MutableMap<Type?, Int> = mutableMapOf()

        /**
         * Stores the average transfer value by type.
         */
        private var averageTransferByType: MutableMap<Type?, Int> = mutableMapOf()


        /**
         * Adds a transfer by type for the diagram that displays the sum of transfer values by type.
         *
         * @param transferByType    Diagram line to add.
         * @return                  This.
         */
        fun addTransferByType(transferByType: AnalysisDiagramLine): Builder {
            transfersByType.add(transferByType)
            return this
        }

        /**
         * Adds a transfer by type for the diagram that displays the cumulated sum of transfer values
         * by type.
         *
         * @param cumulatedTransferByType   Diagram line to add.
         * @return                          This.
         */
        fun addCumulatedTransferByType(cumulatedTransferByType: AnalysisDiagramLine): Builder {
            cumulatedTransfersByType.add(cumulatedTransferByType)
            return this
        }

        /**
         * Adds a total transfer value by type.
         *
         * @param type  Type for which to add the total transfer value.
         * @param value Value to add.
         * @return      This.
         */
        fun addTotalTransferByType(type: Type?, value: Int): Builder {
            totalTransferByType.put(type, value)
            return this
        }

        /**
         * Adds an average transfer value by type.
         *
         * @param type  Type for which to add the average transfer value.
         * @param value Value to add.
         * @return      This.
         */
        fun addAverageTransferByType(type: Type?, value: Int): Builder {
            averageTransferByType.put(type, value)
            return this
        }


        /**
         * Builds the analysis result.
         *
         * @return  Analysis result.
         */
        fun build(): AnalysisResult {
            val transfersByTypeDiagram = AnalysisDiagram(transfersByType, precision)
            val cumulatedTransfersByTypeDiagram = AnalysisDiagram(cumulatedTransfersByType, precision)
            var total = 0
            totalTransferByType.forEach { (_, value) ->
                total += value
            }

            return AnalysisResult(
                transfersByTypeDiagram = transfersByTypeDiagram,
                cumulatedTransfersByTypeDiagram = cumulatedTransfersByTypeDiagram,
                totalTransferByType = totalTransferByType,
                averageTransferByType = averageTransferByType,
                total = total,
                average = if (transfersByType.isNotEmpty()) { total / transfersByType[0].data.size } else { total }
            )
        }

    }

}
