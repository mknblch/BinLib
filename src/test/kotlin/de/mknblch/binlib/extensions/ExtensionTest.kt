package de.mknblch.binlib.extensions

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ExtensionTest {

    @Test
    fun testMakeNestedMap() {
        val testee = listOf(
            "a" to 1,
            "b.a" to 2,
            "c.b.a" to 3
        )
        val nested = testee.nest()
        assertEquals(mapOf("a" to 1, "b" to mapOf("a" to 2), "c" to mapOf("b" to mapOf("a" to 3))), nested)
    }

    @Test
    fun testKeyRedefinitionError() {
        val testee = listOf(
            "a" to 1,
            "a.a" to 2,
            "a.a.a" to 3
        )
        assertThrows<IllegalArgumentException> {
            val nested = testee.nest()
        }
    }

}