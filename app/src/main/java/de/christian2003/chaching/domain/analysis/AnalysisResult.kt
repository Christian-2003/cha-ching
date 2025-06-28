package de.christian2003.chaching.domain.analysis

import de.christian2003.chaching.domain.type.Type


data class AnalysisResult(

    val transfersByTypeDiagram: AnalysisDiagram,

    val cumulatedTransfersByTypeDiagram: AnalysisDiagram,

    val totalTransferByType: Map<Type?, Int>,

    val averageTransferByType: Map<Type?, Int>

) {

    class Builder(

        private val precision: AnalysisPrecision

    ) {

        private val transfersByType: MutableList<AnalysisDiagramLine> = mutableListOf()

        private val cumulatedTransfersByType: MutableList<AnalysisDiagramLine> = mutableListOf()

        private var totalTransferByType: MutableMap<Type?, Int> = mutableMapOf()

        private var averageTransferByType: MutableMap<Type?, Int> = mutableMapOf()


        fun addTransferByType(transferByType: AnalysisDiagramLine): Builder {
            transfersByType.add(transferByType)
            return this
        }

        fun addCumulatedTransferByType(cumulatedTransferByType: AnalysisDiagramLine): Builder {
            cumulatedTransfersByType.add(cumulatedTransferByType)
            return this
        }

        fun addTotalTransferByType(type: Type?, value: Int): Builder {
            totalTransferByType.put(type, value)
            return this
        }

        fun addAverageTransferByType(type: Type?, value: Int): Builder {
            averageTransferByType.put(type, value)
            return this
        }


        fun build(): AnalysisResult {
            val transfersByTypeDiagram = AnalysisDiagram(transfersByType, precision)
            val cumulatedTransfersByTypeDiagram = AnalysisDiagram(cumulatedTransfersByType, precision)

            return AnalysisResult(
                transfersByTypeDiagram = transfersByTypeDiagram,
                cumulatedTransfersByTypeDiagram = cumulatedTransfersByTypeDiagram,
                totalTransferByType = totalTransferByType,
                averageTransferByType = averageTransferByType
            )
        }

    }

}
