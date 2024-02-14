package de.mknblch.binlib.types.primitives

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class UIntTest {

    @Test
    fun testInt8() {
        val buffer = ByteBuffer.allocate(1)
        (0 .. 255).forEach { i ->
            UInt8.write(buffer, i)
            buffer.flip()
            assertEquals(i, UInt8.read(buffer))
            buffer.flip()
        }
    }

    @Test
    fun testUInt16() {
        val buffer = ByteBuffer.allocate(2)
        UInt16.write(buffer, UShort.MIN_VALUE.toInt())
        buffer.flip()
        assertEquals(UShort.MIN_VALUE.toInt(), UInt16.read(buffer))
        buffer.flip()
        UInt16.write(buffer, UShort.MAX_VALUE.toInt())
        buffer.flip()
        assertEquals(UShort.MAX_VALUE.toInt(), UInt16.read(buffer))
        buffer.flip()
        UInt16.write(buffer, 0)
        buffer.flip()
        assertEquals(0, UInt16.read(buffer))
    }

    @Test
    fun testUInt32() {
        val buffer = ByteBuffer.allocate(4)
        UInt32.write(buffer, UInt.MIN_VALUE.toLong())
        buffer.flip()
        assertEquals(UInt.MIN_VALUE.toLong(), UInt32.read(buffer))
        buffer.flip()
        UInt32.write(buffer, UInt.MAX_VALUE.toLong())
        buffer.flip()
        assertEquals(UInt.MAX_VALUE.toLong(), UInt32.read(buffer))
        buffer.flip()
        UInt32.write(buffer, 0L)
        buffer.flip()
        assertEquals(0L, UInt32.read(buffer))
    }
}