package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.dto.TransformerDateResult
import de.christian2003.chaching.application.analysis.large.dto.TransformerTypeResult
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.util.UUID


class LargeTypeResultGeneratorUnitTest {

    private val typeId: UUID = UUID.randomUUID()

    private val generator: LargeTypeResultGenerator = LargeTypeResultGenerator()


    @Test
    fun `generate from regular data without special cases should return`() {
        //With:
        val input = TransformerTypeResult(
            typeId = typeId,
            dateResults = listOf(
                TransformerDateResult(
                    normalizedDate = LocalDate.of(2025, 1, 1),
                    sum = 4000_00,
                    count = 2,
                    hoursWorked = 140
                ),
                TransformerDateResult(
                    normalizedDate = LocalDate.of(2025, 2, 1),
                    sum = 6000_00,
                    count = 4,
                    hoursWorked = 160
                ),
                TransformerDateResult(
                    normalizedDate = LocalDate.of(2025, 3, 1),
                    sum = 1000_00,
                    count = 1,
                    hoursWorked = 0
                )
            )
        )

        //When:
        val result = generator.generate(input)


        //Then:
        Assert.assertEquals(typeId, result.typeId)
        Assert.assertEquals(7, result.transferCount)
        Assert.assertEquals(11000.0, result.valueResult.sum, 0.0)
        Assert.assertEquals(1571.43, result.valueResult.avgPerTransfer, 0.1)
        Assert.assertEquals(3666.67, result.valueResult.avgPerNormalizedDate, 0.1)
        Assert.assertEquals(300, result.hoursWorkedResult.sum)
        Assert.assertEquals(43, result.hoursWorkedResult.avgPerTransfer)
        Assert.assertEquals(100, result.hoursWorkedResult.avgPerNormalizedDate)

        //Values diagram
        Assert.assertEquals(3, result.valuesDiagram.values.size)
        Assert.assertEquals(6000.00, result.valuesDiagram.max, 0.0)
        Assert.assertEquals(1000.00, result.valuesDiagram.min, 0.0)
        Assert.assertEquals(4000.00, result.valuesDiagram.values[0], 0.0)
        Assert.assertEquals(6000.00, result.valuesDiagram.values[1], 0.0)
        Assert.assertEquals(1000.00, result.valuesDiagram.values[2], 0.0)

        //Cumulated diagram
        Assert.assertEquals(3, result.cumulatedDiagram.values.size)
        Assert.assertEquals(11000.00, result.cumulatedDiagram.max, 0.0)
        Assert.assertEquals(4000.00, result.cumulatedDiagram.min, 0.0)
        Assert.assertEquals(4000.00, result.cumulatedDiagram.values[0], 0.0)
        Assert.assertEquals(10000.00, result.cumulatedDiagram.values[1], 0.0)
        Assert.assertEquals(11000.00, result.cumulatedDiagram.values[2], 0.0)
    }


    @Test
    fun `generate with no data should return empty result`() {
        //With:
        val input = TransformerTypeResult(
            typeId = typeId,
            dateResults = listOf()
        )

        //When:
        val result = generator.generate(input)

        //Then:
        Assert.assertEquals(typeId, result.typeId)
        Assert.assertEquals(0, result.transferCount)
        Assert.assertEquals(0.0, result.valueResult.sum, 0.0)
        Assert.assertEquals(0.0, result.valueResult.avgPerTransfer, 0.0)
        Assert.assertEquals(0.0, result.valueResult.avgPerNormalizedDate, 0.0)
        Assert.assertEquals(0, result.hoursWorkedResult.sum)
        Assert.assertEquals(0, result.hoursWorkedResult.avgPerTransfer)
        Assert.assertEquals(0, result.hoursWorkedResult.avgPerNormalizedDate)

        //Values diagram
        Assert.assertEquals(0, result.valuesDiagram.values.size)
        Assert.assertEquals(0.0, result.valuesDiagram.max, 0.0)
        Assert.assertEquals(0.0, result.valuesDiagram.min, 0.0)

        //Cumulated diagram
        Assert.assertEquals(0, result.cumulatedDiagram.values.size)
        Assert.assertEquals(0.0, result.cumulatedDiagram.max, 0.0)
        Assert.assertEquals(0.0, result.cumulatedDiagram.min, 0.0)
    }


    @Test
    fun `generate with single date result should return`() {
        //With:
        val input = TransformerTypeResult(
            typeId = typeId,
            dateResults = listOf(
                TransformerDateResult(
                    normalizedDate = LocalDate.of(2025, 1, 1),
                    sum = 4000_00,
                    count = 2,
                    hoursWorked = 140
                )
            )
        )

        //When:
        val result = generator.generate(input)


        //Then:
        Assert.assertEquals(typeId, result.typeId)
        Assert.assertEquals(2, result.transferCount)
        Assert.assertEquals(4000.0, result.valueResult.sum, 0.0)
        Assert.assertEquals(2000.0, result.valueResult.avgPerTransfer, 0.0)
        Assert.assertEquals(4000.0, result.valueResult.avgPerNormalizedDate, 0.0)
        Assert.assertEquals(140, result.hoursWorkedResult.sum)
        Assert.assertEquals(70, result.hoursWorkedResult.avgPerTransfer)
        Assert.assertEquals(140, result.hoursWorkedResult.avgPerNormalizedDate)

        //Values diagram
        Assert.assertEquals(1, result.valuesDiagram.values.size)
        Assert.assertEquals(4000.00, result.valuesDiagram.max, 0.0)
        Assert.assertEquals(4000.00, result.valuesDiagram.min, 0.0)
        Assert.assertEquals(4000.00, result.valuesDiagram.values[0], 0.0)

        //Cumulated diagram
        Assert.assertEquals(1, result.cumulatedDiagram.values.size)
        Assert.assertEquals(4000.00, result.cumulatedDiagram.max, 0.0)
        Assert.assertEquals(4000.00, result.cumulatedDiagram.min, 0.0)
        Assert.assertEquals(4000.00, result.cumulatedDiagram.values[0], 0.0)
    }


    @Test
    fun `generate with no transfer count should return empty result`() {
        //With:
        val input = TransformerTypeResult(
            typeId = typeId,
            dateResults = listOf(
                TransformerDateResult(
                    normalizedDate = LocalDate.of(2025, 1, 1),
                    sum = 4000_00,
                    count = 0,
                    hoursWorked = 140
                )
            )
        )

        //When:
        val result = generator.generate(input)

        //Then:
        Assert.assertEquals(typeId, result.typeId)
        Assert.assertEquals(0, result.transferCount)
        Assert.assertEquals(4000.0, result.valueResult.sum, 0.0)
        Assert.assertEquals(4000.0, result.valueResult.avgPerTransfer, 0.0)
        Assert.assertEquals(4000.0, result.valueResult.avgPerNormalizedDate, 0.0)
        Assert.assertEquals(140, result.hoursWorkedResult.sum)
        Assert.assertEquals(140, result.hoursWorkedResult.avgPerTransfer)
        Assert.assertEquals(140, result.hoursWorkedResult.avgPerNormalizedDate)

        //Values diagram
        Assert.assertEquals(1, result.valuesDiagram.values.size)
        Assert.assertEquals(4000.0, result.valuesDiagram.max, 0.0)
        Assert.assertEquals(4000.0, result.valuesDiagram.min, 0.0)
        Assert.assertEquals(4000.00, result.valuesDiagram.values[0], 0.0)

        //Cumulated diagram
        Assert.assertEquals(1, result.cumulatedDiagram.values.size)
        Assert.assertEquals(4000.0, result.cumulatedDiagram.max, 0.0)
        Assert.assertEquals(4000.0, result.cumulatedDiagram.min, 0.0)
        Assert.assertEquals(4000.00, result.cumulatedDiagram.values[0], 0.0)
    }

}
