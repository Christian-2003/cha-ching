package de.christian2003.chaching.application.analysis.small

import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.domain.analysis.small.SmallAnalysisResult
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID


@RunWith(MockitoJUnitRunner::class)
class SmallAnalysisUseCaseUnitTest {

    private val repository: AnalysisRepository = mock()

    lateinit var useCase: SmallAnalysisUseCase

    private val salaryTypeId: UUID = UUID.randomUUID()
    private val taxesTypeId: UUID = UUID.randomUUID()
    private val insuranceTypeId: UUID = UUID.randomUUID()
    private val sharesTypeId: UUID = UUID.randomUUID()
    private val holidayPayTypeId: UUID = UUID.randomUUID()
    private val sickPayTypeId: UUID = UUID.randomUUID()


    @Before
    fun setup() {
        useCase = SmallAnalysisUseCase(repository)
    }


    @Test
    fun `data without special cases should be analyzed`() = runTest {
        //Given:
        val month: LocalDate = LocalDate.of(2025, 4, 28)
        val currentStart: LocalDate = LocalDate.of(2025, 4, 1)
        val currentEnd: LocalDate = LocalDate.of(2025, 4, 30)
        val previousStart: LocalDate = LocalDate.of(2025, 3, 1)
        val previousEnd: LocalDate = LocalDate.of(2025, 3, 31)

        //Rule for current month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = eq(currentStart),
                end = eq(currentEnd)
            )
        ).thenReturn(flowOf(listOf(
            //Incomes:
            Transfer(
                transferValue = TransferValue(1500_00, LocalDate.of(2025, 4, 15), true),
                hoursWorked = 140,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(2000_00, LocalDate.of(2025, 4, 29), true),
                hoursWorked = 160,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = sharesTypeId
            ),
            //Expenses:
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 15), false),
                hoursWorked = 0,
                type = taxesTypeId
            ),
            Transfer(
                transferValue = TransferValue(100_00, LocalDate.of(2025, 4, 29), false),
                hoursWorked = 0,
                type = insuranceTypeId
            ),
            Transfer(
                transferValue = TransferValue(1000_00, LocalDate.of(2025, 4, 4), false),
                hoursWorked = 0,
                type = insuranceTypeId
            )
        )))

        //Rule for previous month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = eq(previousStart),
                end = eq(previousEnd)
            )
        ).thenReturn(flowOf(listOf(
            //Incomes:
            Transfer(
                transferValue = TransferValue(1000_00, LocalDate.of(2025, 4, 15), true),
                hoursWorked = 160,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(1500_00, LocalDate.of(2025, 4, 29), true),
                hoursWorked = 140,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = sharesTypeId
            ),
            //Expenses:
            Transfer(
                transferValue = TransferValue(300_00, LocalDate.of(2025, 4, 15), false),
                hoursWorked = 0,
                type = taxesTypeId
            ),
            Transfer(
                transferValue = TransferValue(100_00, LocalDate.of(2025, 4, 29), false),
                hoursWorked = 0,
                type = insuranceTypeId
            ),
            Transfer(
                transferValue = TransferValue(1000_00, LocalDate.of(2025, 4, 4), false),
                hoursWorked = 0,
                type = insuranceTypeId
            )
        )))


        //When:
        val result: SmallAnalysisResult = useCase.analyzeData(month)


        //Then:

        //Current month:
        Assert.assertEquals(currentStart, result.currentMonth.start)
        Assert.assertEquals(currentEnd, result.currentMonth.end)
        Assert.assertEquals(2400.00, result.currentMonth.budget, 0.0)

        Assert.assertEquals(4000.00, result.currentMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(2, result.currentMonth.incomes.typeResults.size)
        Assert.assertEquals(salaryTypeId, result.currentMonth.incomes.typeResults[0].typeId)
        Assert.assertEquals(3500.00, result.currentMonth.incomes.typeResults[0].sum, 0.0)
        Assert.assertEquals(sharesTypeId, result.currentMonth.incomes.typeResults[1].typeId)
        Assert.assertEquals(500.00, result.currentMonth.incomes.typeResults[1].sum, 0.0)

        Assert.assertEquals(1600.00, result.currentMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(2, result.currentMonth.expenses.typeResults.size)
        Assert.assertEquals(insuranceTypeId, result.currentMonth.expenses.typeResults[0].typeId)
        Assert.assertEquals(1100.00, result.currentMonth.expenses.typeResults[0].sum, 0.0)
        Assert.assertEquals(taxesTypeId, result.currentMonth.expenses.typeResults[1].typeId)
        Assert.assertEquals(500.00, result.currentMonth.expenses.typeResults[1].sum, 0.0)

        //Previous month:
        Assert.assertEquals(previousStart, result.previousMonth.start)
        Assert.assertEquals(previousEnd, result.previousMonth.end)
        Assert.assertEquals(1600.00, result.previousMonth.budget, 0.0)

        Assert.assertEquals(3000.00, result.previousMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(2, result.previousMonth.incomes.typeResults.size)
        Assert.assertEquals(salaryTypeId, result.previousMonth.incomes.typeResults[0].typeId)
        Assert.assertEquals(2500.00, result.previousMonth.incomes.typeResults[0].sum, 0.0)
        Assert.assertEquals(sharesTypeId, result.previousMonth.incomes.typeResults[1].typeId)
        Assert.assertEquals(500.00, result.previousMonth.incomes.typeResults[1].sum, 0.0)

        Assert.assertEquals(1400.00, result.previousMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(2, result.previousMonth.expenses.typeResults.size)
        Assert.assertEquals(insuranceTypeId, result.previousMonth.expenses.typeResults[0].typeId)
        Assert.assertEquals(1100.00, result.previousMonth.expenses.typeResults[0].sum, 0.0)
        Assert.assertEquals(taxesTypeId, result.previousMonth.expenses.typeResults[1].typeId)
        Assert.assertEquals(300.00, result.previousMonth.expenses.typeResults[1].sum, 0.0)
    }


    @Test
    fun `more types then limit should make last types grouped`() = runTest {
        //Given:
        val month: LocalDate = LocalDate.of(2025, 4, 28)
        val currentStart: LocalDate = LocalDate.of(2025, 4, 1)
        val currentEnd: LocalDate = LocalDate.of(2025, 4, 30)


        //Rule for previous month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = any(),
                end = any()
            )
        ).thenReturn(flowOf(listOf()))

        //Rule for current month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = eq(currentStart),
                end = eq(currentEnd)
            )
        ).thenReturn(flowOf(listOf(
            //Incomes:
            Transfer(
                transferValue = TransferValue(1500_00, LocalDate.of(2025, 4, 15), true),
                hoursWorked = 0,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = taxesTypeId
            ),
            Transfer(
                transferValue = TransferValue(2000_00, LocalDate.of(2025, 4, 29), true),
                hoursWorked = 0,
                type = insuranceTypeId
            ),
            Transfer(
                transferValue = TransferValue(4000_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = sharesTypeId
            ),
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = holidayPayTypeId
            ),
            Transfer(
                transferValue = TransferValue(3000_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = sickPayTypeId
            )
        )))


        //When:
        val result: SmallAnalysisResult = useCase.analyzeData(month)


        //Then:
        Assert.assertEquals(currentStart, result.currentMonth.start)
        Assert.assertEquals(currentEnd, result.currentMonth.end)
        Assert.assertEquals(11_500.00, result.currentMonth.budget, 0.0)

        Assert.assertEquals(11_500.00, result.currentMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(4, result.currentMonth.incomes.typeResults.size)
        Assert.assertEquals(sharesTypeId, result.currentMonth.incomes.typeResults[0].typeId)
        Assert.assertEquals(4000.00, result.currentMonth.incomes.typeResults[0].sum, 0.0)
        Assert.assertEquals(sickPayTypeId, result.currentMonth.incomes.typeResults[1].typeId)
        Assert.assertEquals(3000.00, result.currentMonth.incomes.typeResults[1].sum, 0.0)
        Assert.assertEquals(insuranceTypeId, result.currentMonth.incomes.typeResults[2].typeId)
        Assert.assertEquals(2000.00, result.currentMonth.incomes.typeResults[2].sum, 0.0)
        Assert.assertEquals(null, result.currentMonth.incomes.typeResults[3].typeId)
        Assert.assertEquals(2500.00, result.currentMonth.incomes.typeResults[3].sum, 0.0)

        Assert.assertEquals(0.00, result.currentMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(0, result.currentMonth.expenses.typeResults.size)
    }


    @Test
    fun `exactly 3 types should not be grouped`() = runTest {
        //Given:
        val month: LocalDate = LocalDate.of(2025, 4, 28)
        val currentStart: LocalDate = LocalDate.of(2025, 4, 1)
        val currentEnd: LocalDate = LocalDate.of(2025, 4, 30)


        //Rule for previous month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = any(),
                end = any()
            )
        ).thenReturn(flowOf(listOf()))

        //Rule for current month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = eq(currentStart),
                end = eq(currentEnd)
            )
        ).thenReturn(flowOf(listOf(
            //Incomes:
            Transfer(
                transferValue = TransferValue(1500_00, LocalDate.of(2025, 4, 15), true),
                hoursWorked = 0,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = taxesTypeId
            ),
            Transfer(
                transferValue = TransferValue(2000_00, LocalDate.of(2025, 4, 29), true),
                hoursWorked = 0,
                type = insuranceTypeId
            )
        )))


        //When:
        val result: SmallAnalysisResult = useCase.analyzeData(month)


        //Then:
        Assert.assertEquals(currentStart, result.currentMonth.start)
        Assert.assertEquals(currentEnd, result.currentMonth.end)
        Assert.assertEquals(4000.00, result.currentMonth.budget, 0.0)

        Assert.assertEquals(4000.00, result.currentMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(3, result.currentMonth.incomes.typeResults.size)
        Assert.assertEquals(insuranceTypeId, result.currentMonth.incomes.typeResults[0].typeId)
        Assert.assertEquals(2000.00, result.currentMonth.incomes.typeResults[0].sum, 0.0)
        Assert.assertEquals(salaryTypeId, result.currentMonth.incomes.typeResults[1].typeId)
        Assert.assertEquals(1500.00, result.currentMonth.incomes.typeResults[1].sum, 0.0)
        Assert.assertEquals(taxesTypeId, result.currentMonth.incomes.typeResults[2].typeId)
        Assert.assertEquals(500.00, result.currentMonth.incomes.typeResults[2].sum, 0.0)

        Assert.assertEquals(0.00, result.currentMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(0, result.currentMonth.expenses.typeResults.size)
    }


    @Test
    fun `exactly 4 types where last type should get summarized`() = runTest {
        //Given:
        val month: LocalDate = LocalDate.of(2025, 4, 28)
        val currentStart: LocalDate = LocalDate.of(2025, 4, 1)
        val currentEnd: LocalDate = LocalDate.of(2025, 4, 30)


        //Rule for previous month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = any(),
                end = any()
            )
        ).thenReturn(flowOf(listOf()))

        //Rule for current month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = eq(currentStart),
                end = eq(currentEnd)
            )
        ).thenReturn(flowOf(listOf(
            //Incomes:
            Transfer(
                transferValue = TransferValue(1500_00, LocalDate.of(2025, 4, 15), true),
                hoursWorked = 0,
                type = salaryTypeId
            ),
            Transfer(
                transferValue = TransferValue(500_00, LocalDate.of(2025, 4, 4), true),
                hoursWorked = 0,
                type = taxesTypeId
            ),
            Transfer(
                transferValue = TransferValue(2000_00, LocalDate.of(2025, 4, 29), true),
                hoursWorked = 0,
                type = insuranceTypeId
            ),
            Transfer(
                transferValue = TransferValue(1000_00, LocalDate.of(2025, 4, 29), true),
                hoursWorked = 0,
                type = sharesTypeId
            )
        )))


        //When:
        val result: SmallAnalysisResult = useCase.analyzeData(month)


        //Then:
        Assert.assertEquals(currentStart, result.currentMonth.start)
        Assert.assertEquals(currentEnd, result.currentMonth.end)
        Assert.assertEquals(5000.00, result.currentMonth.budget, 0.0)

        Assert.assertEquals(5000.00, result.currentMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(4, result.currentMonth.incomes.typeResults.size)
        Assert.assertEquals(insuranceTypeId, result.currentMonth.incomes.typeResults[0].typeId)
        Assert.assertEquals(2000.00, result.currentMonth.incomes.typeResults[0].sum, 0.0)
        Assert.assertEquals(salaryTypeId, result.currentMonth.incomes.typeResults[1].typeId)
        Assert.assertEquals(1500.00, result.currentMonth.incomes.typeResults[1].sum, 0.0)
        Assert.assertEquals(sharesTypeId, result.currentMonth.incomes.typeResults[2].typeId)
        Assert.assertEquals(1000.00, result.currentMonth.incomes.typeResults[2].sum, 0.0)
        Assert.assertEquals(null, result.currentMonth.incomes.typeResults[3].typeId)
        Assert.assertEquals(500.00, result.currentMonth.incomes.typeResults[3].sum, 0.0)

        Assert.assertEquals(0.00, result.currentMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(0, result.currentMonth.expenses.typeResults.size)
    }



    @Test
    fun `no data should return empty result`() = runTest {
        //Given:
        val month: LocalDate = LocalDate.of(2025, 4, 28)
        val currentStart: LocalDate = LocalDate.of(2025, 4, 1)
        val currentEnd: LocalDate = LocalDate.of(2025, 4, 30)
        val previousStart: LocalDate = LocalDate.of(2025, 3, 1)
        val previousEnd: LocalDate = LocalDate.of(2025, 3, 31)


        //Rule for previous month:
        whenever(
            repository.getAllTransfersInDateRange(
                start = any(),
                end = any()
            )
        ).thenReturn(flowOf(listOf()))


        //When:
        val result: SmallAnalysisResult = useCase.analyzeData(month)


        //Then:

        //Current year:
        Assert.assertEquals(currentStart, result.currentMonth.start)
        Assert.assertEquals(currentEnd, result.currentMonth.end)
        Assert.assertEquals(0.00, result.currentMonth.budget, 0.0)

        Assert.assertEquals(0.00, result.currentMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(0, result.currentMonth.incomes.typeResults.size)

        Assert.assertEquals(0.00, result.currentMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(0, result.currentMonth.expenses.typeResults.size)

        //Previous year:
        Assert.assertEquals(previousStart, result.previousMonth.start)
        Assert.assertEquals(previousEnd, result.previousMonth.end)
        Assert.assertEquals(0.00, result.previousMonth.budget, 0.0)

        Assert.assertEquals(0.00, result.previousMonth.incomes.totalSum, 0.0)
        Assert.assertEquals(0, result.previousMonth.incomes.typeResults.size)

        Assert.assertEquals(0.00, result.previousMonth.expenses.totalSum, 0.0)
        Assert.assertEquals(0, result.previousMonth.expenses.typeResults.size)
    }

}
