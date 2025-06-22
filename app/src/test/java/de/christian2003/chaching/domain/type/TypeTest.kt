package de.christian2003.chaching.domain.type

import org.junit.Assert
import org.junit.Test
import java.util.UUID


class TypeTest {

    @Test
    fun CreateType() {
        Type(
            name = "Hello, World!",
            icon = TypeIcon.CURRENCY
        )
    }


    @Test
    fun HashCodeEqualsCorrect() {
        val id = UUID.randomUUID()
        val type1 = Type(
            name = "Hello, World!",
            icon = TypeIcon.CURRENCY,
            id = id
        )
        val type2 = Type(
            name = "ABCDEF",
            icon = TypeIcon.COIN,
            id = id
        )
        Assert.assertEquals(type1.hashCode(), type2.hashCode())
        Assert.assertEquals(type1, type2)
    }

}
