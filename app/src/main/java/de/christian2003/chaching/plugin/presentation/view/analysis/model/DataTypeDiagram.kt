package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.AnalysisDiagram
import de.christian2003.chaching.domain.analysis.TypeResult
import de.christian2003.chaching.plugin.presentation.view.analysis.DataTabOptions


data class DataTypeDiagram(
    val lines: List<DataTypeDiagramLine>,
    val options: DataTabOptions,
    val isCumulated: Boolean
) {

    class Builder(
        private val typeResults: List<TypeResult> = emptyList()
    ) {

        private lateinit var options: DataTabOptions

        private var isCumulated: Boolean = false

        private var limit: Int = 3


        fun setOptions(options: DataTabOptions): Builder {
            this.options = options
            return this
        }

        fun setCumulated(): Builder {
            isCumulated = true
            return this
        }

        fun setLimit(limit: Int): Builder {
            this.limit = limit
            return this
        }


        fun build(): DataTypeDiagram {
            //Determine the list of diagrams:
            val analysisDiagrams: MutableList<Pair<Double, AnalysisDiagram>> = mutableListOf()
            typeResults.forEach { typeResult ->
                when (options) {
                    DataTabOptions.Incomes -> {
                        if (isCumulated) {
                            analysisDiagrams.add(Pair(
                                first = typeResult.incomes.transferSum,
                                second = typeResult.incomes.cumulatedDiagram
                            ))
                        }
                        else {
                            analysisDiagrams.add(Pair(
                                first = typeResult.incomes.transferSum,
                                second = typeResult.incomes.valuesDiagram))
                        }
                    }
                    DataTabOptions.Expenses -> {
                        if (isCumulated) {
                            analysisDiagrams.add(Pair(
                                first = typeResult.incomes.transferSum,
                                second = typeResult.expenses.cumulatedDiagram
                            ))
                        }
                        else {
                            analysisDiagrams.add(Pair(
                                first = typeResult.incomes.transferSum,
                                second = typeResult.expenses.valuesDiagram
                            ))
                        }
                    }
                }
            }

            //Sort desc by sum:
            val sortedAnalysisDiagrams: List<Pair<Double, AnalysisDiagram>> = analysisDiagrams.sortedByDescending { (sum, _) -> sum }

            val lines: MutableList<DataTypeDiagramLine> = mutableListOf()

            //For the first few diagrams, copy their values to the result:
            val itemsToDrop = limit - 1
            if (itemsToDrop > 0) {
                sortedAnalysisDiagrams.take(itemsToDrop).forEachIndexed { index, (_, analysisDiagram) ->
                    lines.add(DataTypeDiagramLine(analysisDiagram.values))
                }
            }

            //Summarize the remaining diagrams into a single line:
            var summarizedDataLine: MutableList<Double>? = null
            sortedAnalysisDiagrams.drop(itemsToDrop).forEach { (_, analysisDiagram) ->
                if (summarizedDataLine == null) {
                    summarizedDataLine = analysisDiagram.values.toMutableList()
                }
                else {
                    for (i in 0..summarizedDataLine.size - 1) {
                        summarizedDataLine[i] += analysisDiagram.values[i]
                    }
                }
            }
            if (summarizedDataLine != null) {
                lines.add(DataTypeDiagramLine(summarizedDataLine))
            }

            if (options == DataTabOptions.Expenses) {
                lines.size == lines.size
            }

            //Return result:
            val result = DataTypeDiagram(lines, options, isCumulated)
            return result
        }
    }

}
