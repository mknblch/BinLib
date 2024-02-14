package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.Companion.struct
import de.mknblch.binlib.types.primitives.Int8
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class OptionalValueTest {

    val optionalValueStruct = struct(
        "i8" to Int8,
        "opt" to OptionalValue(Int8)
    )

    @Test
    fun testWithOpt() {
        val buffer = ByteBuffer.allocate(2)
        optionalValueStruct.write(buffer, mapOf(
            "i8" to 23,
            "opt" to 42
        ))
        buffer.flip()
        val map = optionalValueStruct.read(buffer)
        assertEquals(23, map["i8"])
        assertEquals(42, map["opt"])
    }

    @Test
    fun testNoOpt() {
        val buffer = ByteBuffer.allocate(1)
        optionalValueStruct.write(buffer, mapOf(
            "i8" to 23,
        ))
        buffer.flip()
        val map = optionalValueStruct.read(buffer)
        assertEquals(23, map["i8"])
        assertNull(map["opt"])
    }
}