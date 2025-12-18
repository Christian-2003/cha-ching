package de.christian2003.chaching.application.analysis

import org.junit.Assert
import org.junit.Test


class CumulatedDataLineGeneratorUnitTest {

    @Test
    fun cumulateValuesNormally() {
        val values: List<Int> = listOf(10, 20, 30, 40)
        val generator = CumulatedDateLineGenerator()
        val result = generator.generateCumulatedDataLine(values)

        Assert.assertEquals(4, result.size)
        Assert.assertEquals(10, result[0])
        Assert.assertEquals(30, result[1])
        Assert.assertEquals(60, result[2])
        Assert.assertEquals(100, result[3])
    }


    @Test
    fun cumulateValuesWithZeroes() {
        val values: List<Int> = listOf(10, 0, 0, 40)
        val generator = CumulatedDateLineGenerator()
        val result = generator.generateCumulatedDataLine(values)

        Assert.assertEquals(4, result.size)
        Assert.assertEquals(10, result[0])
        Assert.assertEquals(10, result[1])
        Assert.assertEquals(10, result[2])
        Assert.assertEquals(50, result[3])
    }


    @Test
    fun cumulateValuesWithEmptyList() {
        val values: List<Int> = listOf()
        val generator = CumulatedDateLineGenerator()
        val result = generator.generateCumulatedDataLine(values)

        Assert.assertEquals(0, result.size)
    }


    @Test
    fun cumulateValuesWithSingleValue() {
        val values: List<Int> = listOf(10)
        val generator = CumulatedDateLineGenerator()
        val result = generator.generateCumulatedDataLine(values)

        Assert.assertEquals(1, result.size)
        Assert.assertEquals(10, result[0])
    }


    @Test
    fun cumulateValuesWithNegativeValues() {
        val values: List<Int> = listOf(10, -10, -10)
        val generator = CumulatedDateLineGenerator()
        val result = generator.generateCumulatedDataLine(values)

        Assert.assertEquals(3, result.size)
        Assert.assertEquals(10, result[0])
        Assert.assertEquals(0, result[1])
        Assert.assertEquals(-10, result[2])
    }

}
