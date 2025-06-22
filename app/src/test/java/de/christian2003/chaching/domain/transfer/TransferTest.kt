package de.christian2003.chaching.domain.transfer

import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.util.UUID


class TransferTest {

    @Test
    fun CreateTransfer() {
        val transfer = Transfer(
            value = 123456,
            hoursWorked = 160,
            isSalary = true,
            valueDate = LocalDate.now(),
            type = UUID.randomUUID()
        )
    }


    @Test
    fun hashCodeEqualsCorrect() {
        val id = UUID.randomUUID()
        val transfer1 = Transfer(
            value = 123456,
            hoursWorked = 160,
            isSalary = true,
            valueDate = LocalDate.now(),
            type = UUID.randomUUID(),
            id = id
        )
        val transfer2 = Transfer(
            value = 9876,
            hoursWorked = 40,
            isSalary = false,
            valueDate = LocalDate.now().minusDays(1),
            type = UUID.randomUUID(),
            id = id
        )
        Assert.assertEquals(transfer1.hashCode(), transfer2.hashCode())
        Assert.assertEquals(transfer1, transfer2)
    }


    @Test
    fun numberFormatCorrect() {
        val transfer = Transfer(
            value = 123456,
            hoursWorked = 160,
            isSalary = true,
            valueDate = LocalDate.now(),
            type = UUID.randomUUID()
        )
        Assert.assertEquals(transfer.getFormattedValue(), "1,234.56")
    }

}
