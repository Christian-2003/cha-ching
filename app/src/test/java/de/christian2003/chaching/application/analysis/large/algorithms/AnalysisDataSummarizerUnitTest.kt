package de.christian2003.chaching.application.analysis.large.algorithms

import de.christian2003.chaching.application.analysis.large.dto.SummarizerGroupedTypeResult
import de.christian2003.chaching.application.services.NormalizedDateConverterService
import de.christian2003.chaching.domain.analysis.extensive.AnalysisPrecision
import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.transfer.TransferValue
import de.christian2003.chaching.domain.type.Type
import de.christian2003.chaching.domain.type.TypeIcon
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.util.UUID


class AnalysisDataSummarizerUnitTest {

    private val salaryType: Type = Type("Salary", TypeIcon.CURRENCY)
    private val shareType: Type = Type("Share Dividend", TypeIcon.CURRENCY)
    private val taxesType: Type = Type("Taxes", TypeIcon.CURRENCY)
    private val insuranceType: Type = Type("Insurance", TypeIcon.CURRENCY)

    private val types: List<Type> = listOf(salaryType, shareType, taxesType, insuranceType)

    private val normalizedDateConverterService = NormalizedDateConverterService()


    @Test
    fun monthlySummaryWithData() {
        val transfers: List<Transfer> = listOf(
            //Sep
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 9, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(3000, LocalDate.of(2025, 9, 21), false),
                hoursWorked = 0,
                type = taxesType.id
            ),
            Transfer(
                transferValue = TransferValue(5000, LocalDate.of(2025, 9, 2), true),
                hoursWorked = 140,
                type = salaryType.id
            ),
            //Feb
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 2, 4), true),
                hoursWorked = 140,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(3000, LocalDate.of(2025, 2, 12), true),
                hoursWorked = 50,
                type = salaryType.id
            ),
            //Aug
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 8, 19), false),
                hoursWorked = 0,
                type = taxesType.id
            ),
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 8, 19), false),
                hoursWorked = 0,
                type = insuranceType.id
            )
        )

        val summarizer = AnalysisDataSummarizer(
            precision = AnalysisPrecision.Month,
            start = LocalDate.of(2025, 1, 15),
            end = LocalDate.of(2025, 12, 1),
            normalizedDateConverterService = normalizedDateConverterService
        )

        val result: Map<UUID, List<SummarizerGroupedTypeResult>> = summarizer.summarizeData(transfers, types)

        //Test whether all types are present in result:
        Assert.assertEquals(4, result.size)

        //For each type, test whether all normalized dates are present:
        Assert.assertEquals(12, result[salaryType.id]!!.size)
        Assert.assertEquals(12, result[shareType.id]!!.size)
        Assert.assertEquals(12, result[taxesType.id]!!.size)
        Assert.assertEquals(12, result[insuranceType.id]!!.size)

        //Test salary type result:
        val salaryResult: List<SummarizerGroupedTypeResult> = result[salaryType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), salaryResult[0].date)

        Assert.assertEquals(0, salaryResult[0].incomes.sum)
        Assert.assertEquals(0, salaryResult[0].incomes.count)
        Assert.assertEquals(0, salaryResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[0].expenses.sum)
        Assert.assertEquals(0, salaryResult[0].expenses.count)
        Assert.assertEquals(0, salaryResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), salaryResult[1].date)
        Assert.assertEquals(4500, salaryResult[1].incomes.sum)
        Assert.assertEquals(2, salaryResult[1].incomes.count)
        Assert.assertEquals(190, salaryResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[1].expenses.sum)
        Assert.assertEquals(0, salaryResult[1].expenses.count)
        Assert.assertEquals(0, salaryResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), salaryResult[2].date)
        Assert.assertEquals(0, salaryResult[2].incomes.sum)
        Assert.assertEquals(0, salaryResult[2].incomes.count)
        Assert.assertEquals(0, salaryResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[2].expenses.sum)
        Assert.assertEquals(0, salaryResult[2].expenses.count)
        Assert.assertEquals(0, salaryResult[2].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 4, 1), salaryResult[3].date)
        Assert.assertEquals(0, salaryResult[3].incomes.sum)
        Assert.assertEquals(0, salaryResult[3].incomes.count)
        Assert.assertEquals(0, salaryResult[3].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[3].expenses.sum)
        Assert.assertEquals(0, salaryResult[3].expenses.count)
        Assert.assertEquals(0, salaryResult[3].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 5, 1), salaryResult[4].date)
        Assert.assertEquals(0, salaryResult[4].incomes.sum)
        Assert.assertEquals(0, salaryResult[4].incomes.count)
        Assert.assertEquals(0, salaryResult[4].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[4].expenses.sum)
        Assert.assertEquals(0, salaryResult[4].expenses.count)
        Assert.assertEquals(0, salaryResult[4].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 6, 1), salaryResult[5].date)
        Assert.assertEquals(0, salaryResult[5].incomes.sum)
        Assert.assertEquals(0, salaryResult[5].incomes.count)
        Assert.assertEquals(0, salaryResult[5].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[5].expenses.sum)
        Assert.assertEquals(0, salaryResult[5].expenses.count)
        Assert.assertEquals(0, salaryResult[5].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 7, 1), salaryResult[6].date)
        Assert.assertEquals(0, salaryResult[6].incomes.sum)
        Assert.assertEquals(0, salaryResult[6].incomes.count)
        Assert.assertEquals(0, salaryResult[6].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[6].expenses.sum)
        Assert.assertEquals(0, salaryResult[6].expenses.count)
        Assert.assertEquals(0, salaryResult[6].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 8, 1), salaryResult[7].date)
        Assert.assertEquals(0, salaryResult[7].incomes.sum)
        Assert.assertEquals(0, salaryResult[7].incomes.count)
        Assert.assertEquals(0, salaryResult[7].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[7].expenses.sum)
        Assert.assertEquals(0, salaryResult[7].expenses.count)
        Assert.assertEquals(0, salaryResult[7].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 9, 1), salaryResult[8].date)
        Assert.assertEquals(6500, salaryResult[8].incomes.sum)
        Assert.assertEquals(2, salaryResult[8].incomes.count)
        Assert.assertEquals(140, salaryResult[8].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[8].expenses.sum)
        Assert.assertEquals(0, salaryResult[8].expenses.count)
        Assert.assertEquals(0, salaryResult[8].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 10, 1), salaryResult[9].date)
        Assert.assertEquals(0, salaryResult[9].incomes.sum)
        Assert.assertEquals(0, salaryResult[9].incomes.count)
        Assert.assertEquals(0, salaryResult[9].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[9].expenses.sum)
        Assert.assertEquals(0, salaryResult[9].expenses.count)
        Assert.assertEquals(0, salaryResult[9].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 11, 1), salaryResult[10].date)
        Assert.assertEquals(0, salaryResult[10].incomes.sum)
        Assert.assertEquals(0, salaryResult[10].incomes.count)
        Assert.assertEquals(0, salaryResult[10].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[10].expenses.sum)
        Assert.assertEquals(0, salaryResult[10].expenses.count)
        Assert.assertEquals(0, salaryResult[10].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 12, 1), salaryResult[11].date)
        Assert.assertEquals(0, salaryResult[11].incomes.sum)
        Assert.assertEquals(0, salaryResult[11].incomes.count)
        Assert.assertEquals(0, salaryResult[11].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[11].expenses.sum)
        Assert.assertEquals(0, salaryResult[11].expenses.count)
        Assert.assertEquals(0, salaryResult[11].expenses.hoursWorked)


        //Test share type result:
        val shareResult: List<SummarizerGroupedTypeResult> = result[shareType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), shareResult[0].date)

        Assert.assertEquals(0, shareResult[0].incomes.sum)
        Assert.assertEquals(0, shareResult[0].incomes.count)
        Assert.assertEquals(0, shareResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[0].expenses.sum)
        Assert.assertEquals(0, shareResult[0].expenses.count)
        Assert.assertEquals(0, shareResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), shareResult[1].date)
        Assert.assertEquals(0, shareResult[1].incomes.sum)
        Assert.assertEquals(0, shareResult[1].incomes.count)
        Assert.assertEquals(0, shareResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[1].expenses.sum)
        Assert.assertEquals(0, shareResult[1].expenses.count)
        Assert.assertEquals(0, shareResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), shareResult[2].date)
        Assert.assertEquals(0, shareResult[2].incomes.sum)
        Assert.assertEquals(0, shareResult[2].incomes.count)
        Assert.assertEquals(0, shareResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[2].expenses.sum)
        Assert.assertEquals(0, shareResult[2].expenses.count)
        Assert.assertEquals(0, shareResult[2].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 4, 1), shareResult[3].date)
        Assert.assertEquals(0, shareResult[3].incomes.sum)
        Assert.assertEquals(0, shareResult[3].incomes.count)
        Assert.assertEquals(0, shareResult[3].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[3].expenses.sum)
        Assert.assertEquals(0, shareResult[3].expenses.count)
        Assert.assertEquals(0, shareResult[3].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 5, 1), shareResult[4].date)
        Assert.assertEquals(0, shareResult[4].incomes.sum)
        Assert.assertEquals(0, shareResult[4].incomes.count)
        Assert.assertEquals(0, shareResult[4].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[4].expenses.sum)
        Assert.assertEquals(0, shareResult[4].expenses.count)
        Assert.assertEquals(0, shareResult[4].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 6, 1), shareResult[5].date)
        Assert.assertEquals(0, shareResult[5].incomes.sum)
        Assert.assertEquals(0, shareResult[5].incomes.count)
        Assert.assertEquals(0, shareResult[5].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[5].expenses.sum)
        Assert.assertEquals(0, shareResult[5].expenses.count)
        Assert.assertEquals(0, shareResult[5].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 7, 1), shareResult[6].date)
        Assert.assertEquals(0, shareResult[6].incomes.sum)
        Assert.assertEquals(0, shareResult[6].incomes.count)
        Assert.assertEquals(0, shareResult[6].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[6].expenses.sum)
        Assert.assertEquals(0, shareResult[6].expenses.count)
        Assert.assertEquals(0, shareResult[6].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 8, 1), shareResult[7].date)
        Assert.assertEquals(0, shareResult[7].incomes.sum)
        Assert.assertEquals(0, shareResult[7].incomes.count)
        Assert.assertEquals(0, shareResult[7].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[7].expenses.sum)
        Assert.assertEquals(0, shareResult[7].expenses.count)
        Assert.assertEquals(0, shareResult[7].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 9, 1), shareResult[8].date)
        Assert.assertEquals(0, shareResult[8].incomes.sum)
        Assert.assertEquals(0, shareResult[8].incomes.count)
        Assert.assertEquals(0, shareResult[8].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[8].expenses.sum)
        Assert.assertEquals(0, shareResult[8].expenses.count)
        Assert.assertEquals(0, shareResult[8].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 10, 1), shareResult[9].date)
        Assert.assertEquals(0, shareResult[9].incomes.sum)
        Assert.assertEquals(0, shareResult[9].incomes.count)
        Assert.assertEquals(0, shareResult[9].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[9].expenses.sum)
        Assert.assertEquals(0, shareResult[9].expenses.count)
        Assert.assertEquals(0, shareResult[9].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 11, 1), shareResult[10].date)
        Assert.assertEquals(0, shareResult[10].incomes.sum)
        Assert.assertEquals(0, shareResult[10].incomes.count)
        Assert.assertEquals(0, shareResult[10].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[10].expenses.sum)
        Assert.assertEquals(0, shareResult[10].expenses.count)
        Assert.assertEquals(0, shareResult[10].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 12, 1), shareResult[11].date)
        Assert.assertEquals(0, shareResult[11].incomes.sum)
        Assert.assertEquals(0, shareResult[11].incomes.count)
        Assert.assertEquals(0, shareResult[11].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[11].expenses.sum)
        Assert.assertEquals(0, shareResult[11].expenses.count)
        Assert.assertEquals(0, shareResult[11].expenses.hoursWorked)


        //Test taxes type result:
        val taxesResult: List<SummarizerGroupedTypeResult> = result[taxesType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), taxesResult[0].date)

        Assert.assertEquals(0, taxesResult[0].incomes.sum)
        Assert.assertEquals(0, taxesResult[0].incomes.count)
        Assert.assertEquals(0, taxesResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[0].expenses.sum)
        Assert.assertEquals(0, taxesResult[0].expenses.count)
        Assert.assertEquals(0, taxesResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), taxesResult[1].date)
        Assert.assertEquals(0, taxesResult[1].incomes.sum)
        Assert.assertEquals(0, taxesResult[1].incomes.count)
        Assert.assertEquals(0, taxesResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[1].expenses.sum)
        Assert.assertEquals(0, taxesResult[1].expenses.count)
        Assert.assertEquals(0, taxesResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), taxesResult[2].date)
        Assert.assertEquals(0, taxesResult[2].incomes.sum)
        Assert.assertEquals(0, taxesResult[2].incomes.count)
        Assert.assertEquals(0, taxesResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[2].expenses.sum)
        Assert.assertEquals(0, taxesResult[2].expenses.count)
        Assert.assertEquals(0, taxesResult[2].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 4, 1), taxesResult[3].date)
        Assert.assertEquals(0, taxesResult[3].incomes.sum)
        Assert.assertEquals(0, taxesResult[3].incomes.count)
        Assert.assertEquals(0, taxesResult[3].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[3].expenses.sum)
        Assert.assertEquals(0, taxesResult[3].expenses.count)
        Assert.assertEquals(0, taxesResult[3].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 5, 1), taxesResult[4].date)
        Assert.assertEquals(0, taxesResult[4].incomes.sum)
        Assert.assertEquals(0, taxesResult[4].incomes.count)
        Assert.assertEquals(0, taxesResult[4].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[4].expenses.sum)
        Assert.assertEquals(0, taxesResult[4].expenses.count)
        Assert.assertEquals(0, taxesResult[4].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 6, 1), taxesResult[5].date)
        Assert.assertEquals(0, taxesResult[5].incomes.sum)
        Assert.assertEquals(0, taxesResult[5].incomes.count)
        Assert.assertEquals(0, taxesResult[5].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[5].expenses.sum)
        Assert.assertEquals(0, taxesResult[5].expenses.count)
        Assert.assertEquals(0, taxesResult[5].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 7, 1), taxesResult[6].date)
        Assert.assertEquals(0, taxesResult[6].incomes.sum)
        Assert.assertEquals(0, taxesResult[6].incomes.count)
        Assert.assertEquals(0, taxesResult[6].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[6].expenses.sum)
        Assert.assertEquals(0, taxesResult[6].expenses.count)
        Assert.assertEquals(0, taxesResult[6].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 8, 1), taxesResult[7].date)
        Assert.assertEquals(0, taxesResult[7].incomes.sum)
        Assert.assertEquals(0, taxesResult[7].incomes.count)
        Assert.assertEquals(0, taxesResult[7].incomes.hoursWorked)
        Assert.assertEquals(1500, taxesResult[7].expenses.sum)
        Assert.assertEquals(1, taxesResult[7].expenses.count)
        Assert.assertEquals(0, taxesResult[7].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 9, 1), taxesResult[8].date)
        Assert.assertEquals(0, taxesResult[8].incomes.sum)
        Assert.assertEquals(0, taxesResult[8].incomes.count)
        Assert.assertEquals(0, taxesResult[8].incomes.hoursWorked)
        Assert.assertEquals(3000, taxesResult[8].expenses.sum)
        Assert.assertEquals(1, taxesResult[8].expenses.count)
        Assert.assertEquals(0, taxesResult[8].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 10, 1), taxesResult[9].date)
        Assert.assertEquals(0, taxesResult[9].incomes.sum)
        Assert.assertEquals(0, taxesResult[9].incomes.count)
        Assert.assertEquals(0, taxesResult[9].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[9].expenses.sum)
        Assert.assertEquals(0, taxesResult[9].expenses.count)
        Assert.assertEquals(0, taxesResult[9].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 11, 1), taxesResult[10].date)
        Assert.assertEquals(0, taxesResult[10].incomes.sum)
        Assert.assertEquals(0, taxesResult[10].incomes.count)
        Assert.assertEquals(0, taxesResult[10].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[10].expenses.sum)
        Assert.assertEquals(0, taxesResult[10].expenses.count)
        Assert.assertEquals(0, taxesResult[10].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 12, 1), taxesResult[11].date)
        Assert.assertEquals(0, taxesResult[11].incomes.sum)
        Assert.assertEquals(0, taxesResult[11].incomes.count)
        Assert.assertEquals(0, taxesResult[11].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[11].expenses.sum)
        Assert.assertEquals(0, taxesResult[11].expenses.count)
        Assert.assertEquals(0, taxesResult[11].expenses.hoursWorked)



        //Test salary type result:
        val insuranceResult: List<SummarizerGroupedTypeResult> = result[insuranceType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), insuranceResult[0].date)

        Assert.assertEquals(0, insuranceResult[0].incomes.sum)
        Assert.assertEquals(0, insuranceResult[0].incomes.count)
        Assert.assertEquals(0, insuranceResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[0].expenses.sum)
        Assert.assertEquals(0, insuranceResult[0].expenses.count)
        Assert.assertEquals(0, insuranceResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), insuranceResult[1].date)
        Assert.assertEquals(0, insuranceResult[1].incomes.sum)
        Assert.assertEquals(0, insuranceResult[1].incomes.count)
        Assert.assertEquals(0, insuranceResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[1].expenses.sum)
        Assert.assertEquals(0, insuranceResult[1].expenses.count)
        Assert.assertEquals(0, insuranceResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), insuranceResult[2].date)
        Assert.assertEquals(0, insuranceResult[2].incomes.sum)
        Assert.assertEquals(0, insuranceResult[2].incomes.count)
        Assert.assertEquals(0, insuranceResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[2].expenses.sum)
        Assert.assertEquals(0, insuranceResult[2].expenses.count)
        Assert.assertEquals(0, insuranceResult[2].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 4, 1), insuranceResult[3].date)
        Assert.assertEquals(0, insuranceResult[3].incomes.sum)
        Assert.assertEquals(0, insuranceResult[3].incomes.count)
        Assert.assertEquals(0, insuranceResult[3].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[3].expenses.sum)
        Assert.assertEquals(0, insuranceResult[3].expenses.count)
        Assert.assertEquals(0, insuranceResult[3].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 5, 1), insuranceResult[4].date)
        Assert.assertEquals(0, insuranceResult[4].incomes.sum)
        Assert.assertEquals(0, insuranceResult[4].incomes.count)
        Assert.assertEquals(0, insuranceResult[4].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[4].expenses.sum)
        Assert.assertEquals(0, insuranceResult[4].expenses.count)
        Assert.assertEquals(0, insuranceResult[4].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 6, 1), insuranceResult[5].date)
        Assert.assertEquals(0, insuranceResult[5].incomes.sum)
        Assert.assertEquals(0, insuranceResult[5].incomes.count)
        Assert.assertEquals(0, insuranceResult[5].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[5].expenses.sum)
        Assert.assertEquals(0, insuranceResult[5].expenses.count)
        Assert.assertEquals(0, insuranceResult[5].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 7, 1), insuranceResult[6].date)
        Assert.assertEquals(0, insuranceResult[6].incomes.sum)
        Assert.assertEquals(0, insuranceResult[6].incomes.count)
        Assert.assertEquals(0, insuranceResult[6].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[6].expenses.sum)
        Assert.assertEquals(0, insuranceResult[6].expenses.count)
        Assert.assertEquals(0, insuranceResult[6].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 8, 1), insuranceResult[7].date)
        Assert.assertEquals(0, insuranceResult[7].incomes.sum)
        Assert.assertEquals(0, insuranceResult[7].incomes.count)
        Assert.assertEquals(0, insuranceResult[7].incomes.hoursWorked)
        Assert.assertEquals(1500, insuranceResult[7].expenses.sum)
        Assert.assertEquals(1, insuranceResult[7].expenses.count)
        Assert.assertEquals(0, insuranceResult[7].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 9, 1), insuranceResult[8].date)
        Assert.assertEquals(0, insuranceResult[8].incomes.sum)
        Assert.assertEquals(0, insuranceResult[8].incomes.count)
        Assert.assertEquals(0, insuranceResult[8].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[8].expenses.sum)
        Assert.assertEquals(0, insuranceResult[8].expenses.count)
        Assert.assertEquals(0, insuranceResult[8].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 10, 1), insuranceResult[9].date)
        Assert.assertEquals(0, insuranceResult[9].incomes.sum)
        Assert.assertEquals(0, insuranceResult[9].incomes.count)
        Assert.assertEquals(0, insuranceResult[9].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[9].expenses.sum)
        Assert.assertEquals(0, insuranceResult[9].expenses.count)
        Assert.assertEquals(0, insuranceResult[9].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 11, 1), insuranceResult[10].date)
        Assert.assertEquals(0, insuranceResult[10].incomes.sum)
        Assert.assertEquals(0, insuranceResult[10].incomes.count)
        Assert.assertEquals(0, insuranceResult[10].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[10].expenses.sum)
        Assert.assertEquals(0, insuranceResult[10].expenses.count)
        Assert.assertEquals(0, insuranceResult[10].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 12, 1), insuranceResult[11].date)
        Assert.assertEquals(0, insuranceResult[11].incomes.sum)
        Assert.assertEquals(0, insuranceResult[11].incomes.count)
        Assert.assertEquals(0, insuranceResult[11].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[11].expenses.sum)
        Assert.assertEquals(0, insuranceResult[11].expenses.count)
        Assert.assertEquals(0, insuranceResult[11].expenses.hoursWorked)
    }


    @Test
    fun quarterlySummaryWithData() {
        val transfers: List<Transfer> = listOf(
            //Q1
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 1, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(3000, LocalDate.of(2025, 2, 21), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(5000, LocalDate.of(2025, 2, 2), true),
                hoursWorked = 140,
                type = salaryType.id
            ),
            //Q4
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 12, 4), true),
                hoursWorked = 140,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(3000, LocalDate.of(2025, 11, 12), true),
                hoursWorked = 50,
                type = salaryType.id
            ),
            //Q3
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 8, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 9, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            )
        )

        val summarizer = AnalysisDataSummarizer(
            precision = AnalysisPrecision.Quarter,
            start = LocalDate.of(2025, 1, 15),
            end = LocalDate.of(2025, 12, 1),
            normalizedDateConverterService = normalizedDateConverterService
        )

        val result: Map<UUID, List<SummarizerGroupedTypeResult>> = summarizer.summarizeData(transfers, types)

        //Test whether all types are present in result:
        Assert.assertEquals(4, result.size)

        //For each type, test whether all normalized dates are present:
        Assert.assertEquals(4, result[salaryType.id]!!.size)
        Assert.assertEquals(4, result[shareType.id]!!.size)
        Assert.assertEquals(4, result[taxesType.id]!!.size)
        Assert.assertEquals(4, result[insuranceType.id]!!.size)

        //Test salary type:
        val salaryResult: List<SummarizerGroupedTypeResult> = result[salaryType.id]!!

        Assert.assertEquals(LocalDate.of(2025, 1, 1), salaryResult[0].date)
        Assert.assertEquals(9500, salaryResult[0].incomes.sum)
        Assert.assertEquals(3, salaryResult[0].incomes.count)
        Assert.assertEquals(140, salaryResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[0].expenses.sum)
        Assert.assertEquals(0, salaryResult[0].expenses.count)
        Assert.assertEquals(0, salaryResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 4, 1), salaryResult[1].date)
        Assert.assertEquals(0, salaryResult[1].incomes.sum)
        Assert.assertEquals(0, salaryResult[1].incomes.count)
        Assert.assertEquals(0, salaryResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[1].expenses.sum)
        Assert.assertEquals(0, salaryResult[1].expenses.count)
        Assert.assertEquals(0, salaryResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 7, 1), salaryResult[2].date)
        Assert.assertEquals(3000, salaryResult[2].incomes.sum)
        Assert.assertEquals(2, salaryResult[2].incomes.count)
        Assert.assertEquals(0, salaryResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[2].expenses.sum)
        Assert.assertEquals(0, salaryResult[2].expenses.count)
        Assert.assertEquals(0, salaryResult[2].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 10, 1), salaryResult[3].date)
        Assert.assertEquals(4500, salaryResult[3].incomes.sum)
        Assert.assertEquals(2, salaryResult[3].incomes.count)
        Assert.assertEquals(190, salaryResult[3].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[3].expenses.sum)
        Assert.assertEquals(0, salaryResult[3].expenses.count)
        Assert.assertEquals(0, salaryResult[3].expenses.hoursWorked)
    }


    @Test
    fun yearlySummaryWithData() {
        val transfers: List<Transfer> = listOf(
            //2023
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2023, 1, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(3000, LocalDate.of(2023, 2, 21), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(5000, LocalDate.of(2023, 2, 2), true),
                hoursWorked = 140,
                type = salaryType.id
            ),
            //2026
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2026, 12, 4), true),
                hoursWorked = 140,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(3000, LocalDate.of(2026, 11, 12), true),
                hoursWorked = 50,
                type = salaryType.id
            ),
            //2025
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 8, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            ),
            Transfer(
                transferValue = TransferValue(1500, LocalDate.of(2025, 9, 19), true),
                hoursWorked = 0,
                type = salaryType.id
            )
        )

        val summarizer = AnalysisDataSummarizer(
            precision = AnalysisPrecision.Year,
            start = LocalDate.of(2023, 1, 1),
            end = LocalDate.of(2026, 12, 31),
            normalizedDateConverterService = normalizedDateConverterService
        )

        val result: Map<UUID, List<SummarizerGroupedTypeResult>> = summarizer.summarizeData(transfers, types)

        //Test whether all types are present in result:
        Assert.assertEquals(4, result.size)

        //For each type, test whether all normalized dates are present:
        Assert.assertEquals(4, result[salaryType.id]!!.size)
        Assert.assertEquals(4, result[shareType.id]!!.size)
        Assert.assertEquals(4, result[taxesType.id]!!.size)
        Assert.assertEquals(4, result[insuranceType.id]!!.size)

        //Test salary type:
        val salaryResult: List<SummarizerGroupedTypeResult> = result[salaryType.id]!!

        Assert.assertEquals(LocalDate.of(2023, 1, 1), salaryResult[0].date)
        Assert.assertEquals(9500, salaryResult[0].incomes.sum)
        Assert.assertEquals(3, salaryResult[0].incomes.count)
        Assert.assertEquals(140, salaryResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[0].expenses.sum)
        Assert.assertEquals(0, salaryResult[0].expenses.count)
        Assert.assertEquals(0, salaryResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2024, 1, 1), salaryResult[1].date)
        Assert.assertEquals(0, salaryResult[1].incomes.sum)
        Assert.assertEquals(0, salaryResult[1].incomes.count)
        Assert.assertEquals(0, salaryResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[1].expenses.sum)
        Assert.assertEquals(0, salaryResult[1].expenses.count)
        Assert.assertEquals(0, salaryResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 1, 1), salaryResult[2].date)
        Assert.assertEquals(3000, salaryResult[2].incomes.sum)
        Assert.assertEquals(2, salaryResult[2].incomes.count)
        Assert.assertEquals(0, salaryResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[2].expenses.sum)
        Assert.assertEquals(0, salaryResult[2].expenses.count)
        Assert.assertEquals(0, salaryResult[2].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2026, 1, 1), salaryResult[3].date)
        Assert.assertEquals(4500, salaryResult[3].incomes.sum)
        Assert.assertEquals(2, salaryResult[3].incomes.count)
        Assert.assertEquals(190, salaryResult[3].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[3].expenses.sum)
        Assert.assertEquals(0, salaryResult[3].expenses.count)
        Assert.assertEquals(0, salaryResult[3].expenses.hoursWorked)
    }


    @Test
    fun summaryWithoutData() {
        val transfers: List<Transfer> = listOf()

        val summarizer = AnalysisDataSummarizer(
            precision = AnalysisPrecision.Month,
            start = LocalDate.of(2025, 1, 1),
            end = LocalDate.of(2025, 3, 1),
            normalizedDateConverterService = normalizedDateConverterService
        )

        val result: Map<UUID, List<SummarizerGroupedTypeResult>> = summarizer.summarizeData(transfers, types)

        //Test whether all types are present in result:
        Assert.assertEquals(4, result.size)

        //For each type, test whether all normalized dates are present:
        Assert.assertEquals(3, result[salaryType.id]!!.size)
        Assert.assertEquals(3, result[shareType.id]!!.size)
        Assert.assertEquals(3, result[taxesType.id]!!.size)
        Assert.assertEquals(3, result[insuranceType.id]!!.size)



        //Test salary type result:
        val salaryResult: List<SummarizerGroupedTypeResult> = result[salaryType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), salaryResult[0].date)

        Assert.assertEquals(0, salaryResult[0].incomes.sum)
        Assert.assertEquals(0, salaryResult[0].incomes.count)
        Assert.assertEquals(0, salaryResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[0].expenses.sum)
        Assert.assertEquals(0, salaryResult[0].expenses.count)
        Assert.assertEquals(0, salaryResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), salaryResult[1].date)
        Assert.assertEquals(0, salaryResult[1].incomes.sum)
        Assert.assertEquals(0, salaryResult[1].incomes.count)
        Assert.assertEquals(0, salaryResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[1].expenses.sum)
        Assert.assertEquals(0, salaryResult[1].expenses.count)
        Assert.assertEquals(0, salaryResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), salaryResult[2].date)
        Assert.assertEquals(0, salaryResult[2].incomes.sum)
        Assert.assertEquals(0, salaryResult[2].incomes.count)
        Assert.assertEquals(0, salaryResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, salaryResult[2].expenses.sum)
        Assert.assertEquals(0, salaryResult[2].expenses.count)
        Assert.assertEquals(0, salaryResult[2].expenses.hoursWorked)


        //Test share type result:
        val shareResult: List<SummarizerGroupedTypeResult> = result[shareType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), shareResult[0].date)

        Assert.assertEquals(0, shareResult[0].incomes.sum)
        Assert.assertEquals(0, shareResult[0].incomes.count)
        Assert.assertEquals(0, shareResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[0].expenses.sum)
        Assert.assertEquals(0, shareResult[0].expenses.count)
        Assert.assertEquals(0, shareResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), shareResult[1].date)
        Assert.assertEquals(0, shareResult[1].incomes.sum)
        Assert.assertEquals(0, shareResult[1].incomes.count)
        Assert.assertEquals(0, shareResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[1].expenses.sum)
        Assert.assertEquals(0, shareResult[1].expenses.count)
        Assert.assertEquals(0, shareResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), shareResult[2].date)
        Assert.assertEquals(0, shareResult[2].incomes.sum)
        Assert.assertEquals(0, shareResult[2].incomes.count)
        Assert.assertEquals(0, shareResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, shareResult[2].expenses.sum)
        Assert.assertEquals(0, shareResult[2].expenses.count)
        Assert.assertEquals(0, shareResult[2].expenses.hoursWorked)


        //Test taxes type result:
        val taxesResult: List<SummarizerGroupedTypeResult> = result[taxesType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), taxesResult[0].date)

        Assert.assertEquals(0, taxesResult[0].incomes.sum)
        Assert.assertEquals(0, taxesResult[0].incomes.count)
        Assert.assertEquals(0, taxesResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[0].expenses.sum)
        Assert.assertEquals(0, taxesResult[0].expenses.count)
        Assert.assertEquals(0, taxesResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), taxesResult[1].date)
        Assert.assertEquals(0, taxesResult[1].incomes.sum)
        Assert.assertEquals(0, taxesResult[1].incomes.count)
        Assert.assertEquals(0, taxesResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[1].expenses.sum)
        Assert.assertEquals(0, taxesResult[1].expenses.count)
        Assert.assertEquals(0, taxesResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), taxesResult[2].date)
        Assert.assertEquals(0, taxesResult[2].incomes.sum)
        Assert.assertEquals(0, taxesResult[2].incomes.count)
        Assert.assertEquals(0, taxesResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, taxesResult[2].expenses.sum)
        Assert.assertEquals(0, taxesResult[2].expenses.count)
        Assert.assertEquals(0, taxesResult[2].expenses.hoursWorked)


        //Test salary type result:
        val insuranceResult: List<SummarizerGroupedTypeResult> = result[insuranceType.id]!!
        Assert.assertEquals(LocalDate.of(2025, 1, 1), insuranceResult[0].date)

        Assert.assertEquals(0, insuranceResult[0].incomes.sum)
        Assert.assertEquals(0, insuranceResult[0].incomes.count)
        Assert.assertEquals(0, insuranceResult[0].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[0].expenses.sum)
        Assert.assertEquals(0, insuranceResult[0].expenses.count)
        Assert.assertEquals(0, insuranceResult[0].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 2, 1), insuranceResult[1].date)
        Assert.assertEquals(0, insuranceResult[1].incomes.sum)
        Assert.assertEquals(0, insuranceResult[1].incomes.count)
        Assert.assertEquals(0, insuranceResult[1].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[1].expenses.sum)
        Assert.assertEquals(0, insuranceResult[1].expenses.count)
        Assert.assertEquals(0, insuranceResult[1].expenses.hoursWorked)

        Assert.assertEquals(LocalDate.of(2025, 3, 1), insuranceResult[2].date)
        Assert.assertEquals(0, insuranceResult[2].incomes.sum)
        Assert.assertEquals(0, insuranceResult[2].incomes.count)
        Assert.assertEquals(0, insuranceResult[2].incomes.hoursWorked)
        Assert.assertEquals(0, insuranceResult[2].expenses.sum)
        Assert.assertEquals(0, insuranceResult[2].expenses.count)
        Assert.assertEquals(0, insuranceResult[2].expenses.hoursWorked)
    }

}
