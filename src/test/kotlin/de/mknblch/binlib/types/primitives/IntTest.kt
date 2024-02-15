package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib.Companion.decorate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class IntTest {

    @Test
    fun testInt8() {
        val buffer = ByteBuffer.allocate(1)
        (-127 .. 127).forEach { i ->
            Int8.write(buffer, i)
            buffer.flip()
            assertEquals(i, Int8.read(buffer))
            buffer.flip()
        }
    }

    @Test
    fun testInt16() {
        val buffer = ByteBuffer.allocate(2)
        Int16.write(buffer, Short.MIN_VALUE.toInt())
        buffer.flip()
        assertEquals(Short.MIN_VALUE.toInt(), Int16.read(buffer))
        buffer.flip()
        Int16.write(buffer, Short.MAX_VALUE.toInt())
        buffer.flip()
        assertEquals(Short.MAX_VALUE.toInt(), Int16.read(buffer))
        buffer.flip()
        Int16.write(buffer, 0)
        buffer.flip()
        assertEquals(0, Int16.read(buffer))
    }

    @Test
    fun testInt32() {
        val buffer = ByteBuffer.allocate(4)
        Int32.write(buffer, Int.MIN_VALUE)
        buffer.flip()
        assertEquals(Int.MIN_VALUE, Int32.read(buffer))
        buffer.flip()
        Int32.write(buffer, Int.MAX_VALUE)
        buffer.flip()
        assertEquals(Int.MAX_VALUE, Int32.read(buffer))
        buffer.flip()
        Int32.write(buffer, 0)
        buffer.flip()
        assertEquals(0, Int32.read(buffer))
    }
}