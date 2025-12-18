package de.christian2003.chaching.application.analysis

import de.christian2003.chaching.application.analysis.dto.DataLines
import de.christian2003.chaching.application.analysis.dto.GroupedTypeSum
import de.christian2003.chaching.application.analysis.dto.TypeSum
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate


class GroupedTypeSumToDataLineConverterUnitTest {

    @Test
    fun groupNormally() {
        val groupedTypeSums: List<GroupedTypeSum> = listOf(
            GroupedTypeSum(
                date = LocalDate.of(2025, 1, 1),
                incomes = TypeSum(1500, 3, 140),
                expenses = TypeSum(1000, 2, 0)
            ),
            GroupedTypeSum(
                date = LocalDate.of(2025, 2, 1),
                incomes = TypeSum(3000, 2, 160),
                expenses = TypeSum(500, 1, 0)
            ),
            GroupedTypeSum(
                date = LocalDate.of(2025, 3, 1),
                incomes = TypeSum(2000, 3, 120),
                expenses = TypeSum(2000, 4, 0)
            )
        )

        val converter = GroupedTypeSumToDataLineConverter()

        val result: DataLines = converter.getDataLine(groupedTypeSums)

        Assert.assertEquals(3, result.incomes.size)
        Assert.assertEquals(3, result.expenses.size)

        Assert.assertEquals(1500, result.incomes[0])
        Assert.assertEquals(3000, result.incomes[1])
        Assert.assertEquals(2000, result.incomes[2])

        Assert.assertEquals(1000, result.expenses[0])
        Assert.assertEquals(500, result.expenses[1])
        Assert.assertEquals(2000, result.expenses[2])
    }

}
