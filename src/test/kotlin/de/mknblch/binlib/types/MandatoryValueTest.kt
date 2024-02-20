package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.Companion.struct
import de.mknblch.binlib.extensions.toHex
import de.mknblch.binlib.types.primitives.Int8
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.ByteBuffer

class MandatoryValueTest {

    @Test
    fun testWrite() {
        val buffer = ByteBuffer.allocate(2)

        val struct = struct(
            "m" to MandatoryValue<Int>(Int8, 42),
            "x" to Int8
        )

        struct.write(
            buffer, mapOf(
                "m" to true,
                "x" to 42
            )
        )

        buffer.flip()
        println(buffer.toHex())
        val map = struct.read(buffer)
        println(map)
        assertEquals(true, map["m"])
        assertEquals(42, map["x"])
    }

    @Test
    fun testFail() {
        val buffer = ByteBuffer.allocate(2)
        val struct1 = struct(
            "m" to MandatoryValue(Int8, 42),
            "x" to Int8
        )
        val struct2 = struct(
            "m" to MandatoryValue(Int8, 23),
            "x" to Int8
        )
        struct1.write(
            buffer, mapOf(
                "m" to true,
                "x" to 42
            )
        )
        buffer.flip()
        assertThrows<IllegalStateException> {
            struct2.read(buffer)
        }
    }
}