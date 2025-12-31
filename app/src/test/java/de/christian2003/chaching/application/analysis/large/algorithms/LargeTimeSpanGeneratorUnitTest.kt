package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.dto.TransformerDateResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerTypeResult
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.util.UUID


class LargeTimeSpanGeneratorUnitTest {

    private val type1Id: UUID = UUID.randomUUID()
    private val type2Id: UUID = UUID.randomUUID()
    private val type3Id: UUID = UUID.randomUUID()

    private val generator: LargeTimeSpanGenerator = LargeTimeSpanGenerator()


    @Test
    fun `generate with regular data without special cases should return`() {
        //With:
        val input = TransformerResult(
            incomes = listOf(
                TransformerTypeResult(
                    typeId = type1Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 4000_00,
                            count = 4,
                            hoursWorked = 140
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 2000_00,
                            count = 1,
                            hoursWorked = 160
                        )
                    )
                ),
                TransformerTypeResult(
                    typeId = type2Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 1000_00,
                            count = 1,
                            hoursWorked = 0
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 1000_00,
                            count = 2,
                            hoursWorked = 0
                        )
                    )
                )
            ),
            expenses = listOf(
                TransformerTypeResult(
                    typeId = type2Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 4000_00,
                            count = 2,
                            hoursWorked = 0
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 2000_00,
                            count = 1,
                            hoursWorked = 0
                        )
                    )
                ),
                TransformerTypeResult(
                    typeId = type3Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 1000_00,
                            count = 1,
                            hoursWorked = 0
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 1000_00,
                            count = 2,
                            hoursWorked = 0
                        )
                    )
                )
            )
        )


        //When:
        val result = generator.generate(
            start = LocalDate.of(2025, 1, 1),
            end = LocalDate.of(2025, 3, 15),
            transformerResult = input
        )


        //Then:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.start)
        Assert.assertEquals(LocalDate.of(2025, 3, 15), result.end)
        Assert.assertEquals(2, result.normalizedDates.size)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.normalizedDates[0])
        Assert.assertEquals(LocalDate.of(2025, 2, 1), result.normalizedDates[1])

        //Incomes:
        Assert.assertEquals(2, result.incomes.typeResults.size)
        Assert.assertEquals(8000.0, result.incomes.totalSum, 0.0)
        Assert.assertEquals(1000.0, result.incomes.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(4000.0, result.incomes.totalAvgPerNormalizedDate, 0.0)

        //Expenses:
        Assert.assertEquals(2, result.expenses.typeResults.size)
        Assert.assertEquals(8000.0, result.expenses.totalSum, 0.0)
        Assert.assertEquals(1333.33, result.expenses.totalAvgPerTransfer, 0.1)
        Assert.assertEquals(4000.0, result.expenses.totalAvgPerNormalizedDate, 0.0)
    }


    @Test
    fun `generate with no data should return`() {
        //With:
        val input = TransformerResult(
            incomes = listOf(),
            expenses = listOf()
        )


        //When:
        val result = generator.generate(
            start = LocalDate.of(2025, 1, 1),
            end = LocalDate.of(2025, 3, 15),
            transformerResult = input
        )


        //Then:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.start)
        Assert.assertEquals(LocalDate.of(2025, 3, 15), result.end)
        Assert.assertEquals(0, result.normalizedDates.size)

        //Incomes:
        Assert.assertEquals(0, result.incomes.typeResults.size)
        Assert.assertEquals(0.0, result.incomes.totalSum, 0.0)
        Assert.assertEquals(0.0, result.incomes.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(0.0, result.incomes.totalAvgPerNormalizedDate, 0.0)

        //Expenses:
        Assert.assertEquals(0, result.expenses.typeResults.size)
        Assert.assertEquals(0.0, result.expenses.totalSum, 0.0)
        Assert.assertEquals(0.0, result.expenses.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(0.0, result.expenses.totalAvgPerNormalizedDate, 0.0)
    }


    @Test
    fun `generate with only incomes should return`() {
        //With:
        val input = TransformerResult(
            incomes = listOf(
                TransformerTypeResult(
                    typeId = type1Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 4000_00,
                            count = 4,
                            hoursWorked = 140
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 2000_00,
                            count = 1,
                            hoursWorked = 160
                        )
                    )
                ),
                TransformerTypeResult(
                    typeId = type2Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 1000_00,
                            count = 1,
                            hoursWorked = 0
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 1000_00,
                            count = 2,
                            hoursWorked = 0
                        )
                    )
                )
            ),
            expenses = listOf()
        )


        //When:
        val result = generator.generate(
            start = LocalDate.of(2025, 1, 1),
            end = LocalDate.of(2025, 3, 15),
            transformerResult = input
        )


        //Then:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.start)
        Assert.assertEquals(LocalDate.of(2025, 3, 15), result.end)
        Assert.assertEquals(2, result.normalizedDates.size)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.normalizedDates[0])
        Assert.assertEquals(LocalDate.of(2025, 2, 1), result.normalizedDates[1])

        //Incomes:
        Assert.assertEquals(2, result.incomes.typeResults.size)
        Assert.assertEquals(8000.0, result.incomes.totalSum, 0.0)
        Assert.assertEquals(1000.0, result.incomes.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(4000.0, result.incomes.totalAvgPerNormalizedDate, 0.0)

        //Expenses:
        Assert.assertEquals(0, result.expenses.typeResults.size)
        Assert.assertEquals(0.0, result.expenses.totalSum, 0.0)
        Assert.assertEquals(0.0, result.expenses.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(0.0, result.expenses.totalAvgPerNormalizedDate, 0.0)
    }


    @Test
    fun `generate with only expenses should return`() {
        //With:
        val input = TransformerResult(
            incomes = listOf(),
            expenses = listOf(
                TransformerTypeResult(
                    typeId = type2Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 4000_00,
                            count = 2,
                            hoursWorked = 0
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 2000_00,
                            count = 1,
                            hoursWorked = 0
                        )
                    )
                ),
                TransformerTypeResult(
                    typeId = type3Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 1000_00,
                            count = 1,
                            hoursWorked = 0
                        ),
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 2, 1),
                            sum = 1000_00,
                            count = 2,
                            hoursWorked = 0
                        )
                    )
                )
            )
        )


        //When:
        val result = generator.generate(
            start = LocalDate.of(2025, 1, 1),
            end = LocalDate.of(2025, 3, 15),
            transformerResult = input
        )


        //Then:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.start)
        Assert.assertEquals(LocalDate.of(2025, 3, 15), result.end)
        Assert.assertEquals(2, result.normalizedDates.size)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.normalizedDates[0])
        Assert.assertEquals(LocalDate.of(2025, 2, 1), result.normalizedDates[1])

        //Incomes:
        Assert.assertEquals(0, result.incomes.typeResults.size)
        Assert.assertEquals(0.0, result.incomes.totalSum, 0.0)
        Assert.assertEquals(0.0, result.incomes.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(0.0, result.incomes.totalAvgPerNormalizedDate, 0.0)

        //Expenses:
        Assert.assertEquals(2, result.expenses.typeResults.size)
        Assert.assertEquals(8000.0, result.expenses.totalSum, 0.0)
        Assert.assertEquals(1333.33, result.expenses.totalAvgPerTransfer, 0.1)
        Assert.assertEquals(4000.0, result.expenses.totalAvgPerNormalizedDate, 0.0)
    }


    @Test
    fun `generate with only one TransformerTypeResult should return`() {
        //With:
        val input = TransformerResult(
            incomes = listOf(
                TransformerTypeResult(
                    typeId = type1Id,
                    dateResults = listOf(
                        TransformerDateResult(
                            normalizedDate = LocalDate.of(2025, 1, 1),
                            sum = 4000_00,
                            count = 4,
                            hoursWorked = 140
                        )
                    )
                )
            ),
            expenses = listOf()
        )


        //When:
        val result = generator.generate(
            start = LocalDate.of(2025, 1, 1),
            end = LocalDate.of(2025, 3, 15),
            transformerResult = input
        )


        //Then:
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.start)
        Assert.assertEquals(LocalDate.of(2025, 3, 15), result.end)
        Assert.assertEquals(1, result.normalizedDates.size)
        Assert.assertEquals(LocalDate.of(2025, 1, 1), result.normalizedDates[0])

        //Incomes:
        Assert.assertEquals(1, result.incomes.typeResults.size)
        Assert.assertEquals(4000.0, result.incomes.totalSum, 0.0)
        Assert.assertEquals(1000.0, result.incomes.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(4000.0, result.incomes.totalAvgPerNormalizedDate, 0.0)

        //Expenses:
        Assert.assertEquals(0, result.expenses.typeResults.size)
        Assert.assertEquals(0.0, result.expenses.totalSum, 0.0)
        Assert.assertEquals(0.0, result.expenses.totalAvgPerTransfer, 0.0)
        Assert.assertEquals(0.0, result.expenses.totalAvgPerNormalizedDate, 0.0)
    }

}
