package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.application.analysis.dto.DataLines
import de.christian2003.chaching.application.analysis.large.dto.SummarizerGroupedTypeResult


/**
 * Converts the list of grouped type sums to data lines that can be used for diagrams.
 */
class GroupedTypeSumToDataLineConverter{

    /**
     * Converts a list of grouped type sums to data lines.
     *
     * @param groupedTypeSums   List of grouped type sums.
     * @return                  Data lines.
     */
    fun getDataLine(groupedTypeSums: List<SummarizerGroupedTypeResult>): DataLines {
        val incomes: MutableList<Int> = mutableListOf()
        val expenses: MutableList<Int> = mutableListOf()

        groupedTypeSums.forEach { groupedTypeSum ->
            incomes.add(groupedTypeSum.incomes.sum)
            expenses.add(groupedTypeSum.expenses.sum)
        }

        return DataLines(
            incomes = incomes,
            expenses = expenses
        )
    }

}
