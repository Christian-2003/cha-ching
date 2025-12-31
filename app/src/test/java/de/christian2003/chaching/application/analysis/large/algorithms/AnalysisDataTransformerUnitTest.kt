package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.dto.SummarizerGroupedTypeResult
import de.christian2003.chaching.application.analysis.large.dto.SummarizerTypeResult
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.util.UUID

class AnalysisDataTransformerUnitTest {

    private val type1Id: UUID = UUID.randomUUID()
    private val type2Id: UUID = UUID.randomUUID()

    private val transformer: AnalysisDataTransformer = AnalysisDataTransformer()



    @Test
    fun `transform regular data without special cases should return`() {
        //With:
        val input: Map<UUID, List<SummarizerGroupedTypeResult>> = mapOf(
            type1Id to listOf(
                SummarizerGroupedTypeResult(
                    date = LocalDate.of(2025, 1, 1),
                    incomes = SummarizerTypeResult(
                        sum = 1234,
                        count = 1,
                        hoursWorked = 140
                    ),
                    expenses = SummarizerTypeResult(
                        sum = 0,
                        count = 0,
                        hoursWorked = 0
                    )
                ),
                SummarizerGroupedTypeResult(
                    date = LocalDate.of(2025, 2, 1),
                    incomes = SummarizerTypeResult(
                        sum = 2345,
                        count = 2,
                        hoursWorked = 160
                    ),
                    expenses = SummarizerTypeResult(
                        sum = 3000,
                        count = 2,
                        hoursWorked = 0
                    )
                )
            ),
            type2Id to listOf(
                SummarizerGroupedTypeResult(
                    date = LocalDate.of(2025, 1, 1),
                    incomes = SummarizerTypeResult(
                        sum = 300,
                        count = 2,
                        hoursWorked = 0
                    ),
                    expenses = SummarizerTypeResult(
                        sum = 1000,
                        count = 5,
                        hoursWorked = 0
                    )
                ),
                SummarizerGroupedTypeResult(
                    date = LocalDate.of(2025, 2, 1),
                    incomes = SummarizerTypeResult(
                        sum = 1234,
                        count = 1,
                        hoursWorked = 140
                    ),
                    expenses = SummarizerTypeResult(
                        sum = 0,
                        count = 0,
                        hoursWorked = 0
                    )
                )
            )
        )

        //When:
        val result = transformer.transform(input)


        //Then:
        val incomes = result.incomes
        val expenses = result.expenses

        //Incomes
        Assert.assertEquals(2, incomes.size)

        Assert.assertEquals(type1Id, incomes[0].typeId)
        Assert.assertEquals(2, incomes[0].dateResults.size)
        Assert.assertEquals(LocalDate.of(2025,1,1), incomes[0].dateResults[0].normalizedDate)
        Assert.assertEquals(1234, incomes[0].dateResults[0].sum)
        Assert.assertEquals(1, incomes[0].dateResults[0].count)
        Assert.assertEquals(140, incomes[0].dateResults[0].hoursWorked)
        Assert.assertEquals(LocalDate.of(2025,2,1), incomes[0].dateResults[1].normalizedDate)
        Assert.assertEquals(2345, incomes[0].dateResults[1].sum)
        Assert.assertEquals(2, incomes[0].dateResults[1].count)
        Assert.assertEquals(160, incomes[0].dateResults[1].hoursWorked)

        Assert.assertEquals(type2Id, incomes[1].typeId)
        Assert.assertEquals(2, incomes[1].dateResults.size)
        Assert.assertEquals(LocalDate.of(2025,1,1), incomes[1].dateResults[0].normalizedDate)
        Assert.assertEquals(300, incomes[1].dateResults[0].sum)
        Assert.assertEquals(2, incomes[1].dateResults[0].count)
        Assert.assertEquals(0, incomes[1].dateResults[0].hoursWorked)
        Assert.assertEquals(LocalDate.of(2025,2,1), incomes[1].dateResults[1].normalizedDate)
        Assert.assertEquals(1234, incomes[1].dateResults[1].sum)
        Assert.assertEquals(1, incomes[1].dateResults[1].count)
        Assert.assertEquals(140, incomes[1].dateResults[1].hoursWorked)

        //Expenses:
        Assert.assertEquals(2, expenses.size)

        Assert.assertEquals(type1Id, expenses[0].typeId)
        Assert.assertEquals(2, expenses[0].dateResults.size)
        Assert.assertEquals(LocalDate.of(2025,1,1), expenses[0].dateResults[0].normalizedDate)
        Assert.assertEquals(0, expenses[0].dateResults[0].sum)
        Assert.assertEquals(0, expenses[0].dateResults[0].count)
        Assert.assertEquals(0, expenses[0].dateResults[0].hoursWorked)
        Assert.assertEquals(LocalDate.of(2025,2,1), expenses[0].dateResults[1].normalizedDate)
        Assert.assertEquals(3000, expenses[0].dateResults[1].sum)
        Assert.assertEquals(2, expenses[0].dateResults[1].count)
        Assert.assertEquals(0, expenses[0].dateResults[1].hoursWorked)

        Assert.assertEquals(type2Id, expenses[1].typeId)
        Assert.assertEquals(2, expenses[1].dateResults.size)
        Assert.assertEquals(LocalDate.of(2025,1,1), expenses[1].dateResults[0].normalizedDate)
        Assert.assertEquals(1000, expenses[1].dateResults[0].sum)
        Assert.assertEquals(5, expenses[1].dateResults[0].count)
        Assert.assertEquals(0, expenses[1].dateResults[0].hoursWorked)
        Assert.assertEquals(LocalDate.of(2025,2,1), expenses[1].dateResults[1].normalizedDate)
        Assert.assertEquals(0, expenses[1].dateResults[1].sum)
        Assert.assertEquals(0, expenses[1].dateResults[1].count)
        Assert.assertEquals(0, expenses[1].dateResults[1].hoursWorked)
    }

}
