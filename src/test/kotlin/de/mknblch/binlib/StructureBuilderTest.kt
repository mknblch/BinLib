package de.mknblch.binlib

import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.struct
import de.mknblch.binlib.types.primitives.Ascii
import de.mknblch.binlib.types.primitives.AsciiDynamic
import de.mknblch.binlib.types.primitives.Int8
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer

class StructureBuilderTest {


    private val dynStringStruct = BinLib.struct(
        "i1" to Int8,
        "sub" to struct("string" to AsciiDynamic()),
        "i2" to Int8,
    )

    private val exactStringStruct = BinLib.struct(
        "i1" to Int8,
        "sub" to struct("string" to Ascii(5)),
        "i2" to Int8,
    )

    @Test
    fun testStructs() {
        assertEquals(SIZE_UNDEFINED, dynStringStruct.size())
        assertEquals(7, exactStringStruct.size())
    }

    @Test
    fun testExactString() {
        val builder = StructureBuilder(exactStringStruct)
        val packet = builder
            .set("i1", 0x0F)
            .set("i2", 0x0F)
            .set("sub.string", "hello")
            .build()
        val map = exactStringStruct.read(ByteBuffer.wrap(packet))
        println(map)
        assertMapEquals(builder.arguments, map)
    }


    @Test
    fun testDynamicString() {
        val builder = StructureBuilder(dynStringStruct)
        val packet = builder
            .set("i1", 0x0F)
            .set("i2", 0x0F)
            .set("sub.string", "hello")
            .build()
        val map = dynStringStruct.read(ByteBuffer.wrap(packet))
        println(map)
        assertMapEquals(builder.arguments, map)
    }

    @Test
    fun testWrongArgument() {

        assertThrows<IllegalArgumentException> {
            StructureBuilder(dynStringStruct)
                .set("hello", "world")
        }
    }

    private fun <K, Any> assertMapEquals(result: Map<K, Any>, expected: Map<K, Any>) {
        assertEquals(expected.size, result.size)
        for (entry in expected) {
            val value = result[entry.key] ?: fail("expected element ${entry.key} not found")
            assertEquals(entry.value, value)
        }
    }
}