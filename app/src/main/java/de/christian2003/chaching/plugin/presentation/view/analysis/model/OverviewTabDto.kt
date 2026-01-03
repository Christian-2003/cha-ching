package de.christian2003.chaching.plugin.presentation.view.analysis.model

import de.christian2003.chaching.domain.analysis.large.LargeAnalysisResult
import de.christian2003.chaching.domain.analysis.large.LargeTimeSpan

data class OverviewTabDto(
    val currentBudget: Double,
    val previousBudget: Double,
    val currentAvgBudgetPerNormalizedDate: Double,
    val previousAvgBudgetPerNormalizedDate: Double,
    val budgetByNormalizedDateDiagram: DiagramDto
) {

    companion object {

        fun getInstance(
            analysisResult: LargeAnalysisResult,
            diagramLabels: List<String>
        ): OverviewTabDto {
            val currentSpan: LargeTimeSpan = analysisResult.currentSpan
            val previousSpan: LargeTimeSpan = analysisResult.previousSpan

            //Budget:
            val currentBudget: Double = currentSpan.incomes.totalSum - currentSpan.expenses.totalSum
            val previousBudget: Double = previousSpan.incomes.totalSum - previousSpan.expenses.totalSum
            val currentAvgBudgetPerNormalizedDate: Double = if (currentSpan.normalizedDates.isNotEmpty()) { currentBudget / currentSpan.normalizedDates.size } else { currentBudget }
            val previousAvgBudgetPerNormalizedDate: Double = if (previousSpan.normalizedDates.isNotEmpty()) { previousBudget / previousSpan.normalizedDates.size } else { previousBudget }
            val budgetByNormalizedDateDiagram: DiagramDto = DiagramDto.getInstance(currentSpan, diagramLabels)

            //Result:
            val result = OverviewTabDto(
                currentBudget = currentBudget,
                previousBudget = previousBudget,
                currentAvgBudgetPerNormalizedDate = currentAvgBudgetPerNormalizedDate,
                previousAvgBudgetPerNormalizedDate = previousAvgBudgetPerNormalizedDate,
                budgetByNormalizedDateDiagram = budgetByNormalizedDateDiagram
            )

            return result
        }

    }

}
