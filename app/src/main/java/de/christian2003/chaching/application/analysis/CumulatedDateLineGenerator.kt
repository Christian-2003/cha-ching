package de.christian2003.chaching.application.analysis

class CumulatedDateLineGenerator {

    fun generateCumulatedDataLine(list: List<Int>): List<Int> {
        val cumulated: MutableList<Int> = mutableListOf()

        list.forEach { value ->
            val previousValue: Int = cumulated.lastOrNull() ?: 0
            val newValue: Int = value + previousValue
            cumulated.add(newValue)
        }

        return cumulated
    }

}
