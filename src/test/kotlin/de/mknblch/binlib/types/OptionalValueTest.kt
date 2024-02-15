package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.struct
import de.mknblch.binlib.extensions.toHex
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

    @Test
    fun testEmptyOpt() {
        val buffer = ByteBuffer.allocate(0)
        val map = struct("empty" to OptionalValue(Int8)).read(buffer)
        assertNull(map["empty"])
    }

    @Test
    fun testReadDefault() {
        val buffer = ByteBuffer.allocate(0)
        val v =  OptionalValue(Int8, 42).read(buffer)
        assertEquals(42, v)
        println(v)
    }

    @Test
    fun testWriteDefault() {
        val buffer = ByteBuffer.allocate(1)
        val s = OptionalValue(Int8, 42).write(buffer, null)
        assertEquals(1, s)
        buffer.flip()
        assertEquals(42, Int8.read(buffer))
    }

    @Test
    fun testReadDefaultStruct() {

        val buffer = ByteBuffer.allocate(0)
        val optStruct = struct(
            "opt" to OptionalValue(Int8, 42)
        )
        assertEquals(42, optStruct.read(buffer)["opt"])

    }

    @Test
    fun testWriteDefaultStruct() {

        val buffer = ByteBuffer.allocate(0)
        val optStruct = struct(
            "opt" to OptionalValue(Int8, 42)
        )
        assertEquals(0, optStruct.write(buffer, emptyMap()))
        assertEquals(42, optStruct.read(buffer)["opt"])

    }

    @Test
    fun testEmptyArrayOfOpts() {
        val optStruct = OptionalValue(BinLib.array(4, Int8), listOf(1, 2, 3, 4))
        val buffer = ByteBuffer.allocate(3) // not enough space to write 4xInt8
        assertEquals(0, optStruct.write(buffer, null))
    }

    @Test
    fun testArrayOfOpts() {
        val optStruct = OptionalValue(BinLib.array(4, Int8), listOf(1, 2, 3, 4))
        val buffer = ByteBuffer.allocate(4)
        assertEquals(4, optStruct.write(buffer, null))
        buffer.flip()
        assertEquals(listOf(1, 2, 3, 4), optStruct.read(buffer))
    }
}