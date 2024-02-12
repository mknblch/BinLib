package de.mknblch.binlib

import de.mknblch.binlib.BinLib.Companion.bitfield
import de.mknblch.binlib.BinLib.Companion.struct
import de.mknblch.binlib.extensions.flatten
import de.mknblch.binlib.extensions.toHex
import de.mknblch.binlib.types.*
import de.mknblch.binlib.types.bitfields.BInt
import de.mknblch.binlib.types.bitfields.BInt8
import de.mknblch.binlib.types.primitives.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.stream.Stream


class BinLibTest {

    private val int3Struct: Structure<String> = struct(
        "i32" to Int32,
        "i16" to Int16,
        "i8" to Int8,
    )

    private val containerStruct = struct(
        "i8" to Int8,
        "struct" to int3Struct,
    )

    private val dynStringStruct = struct(
        "i1" to Int8,
        "string" to StringDynamic(),
        "i2" to Int8,
    )

    private val exactStringStruct = struct(
        "i1" to Int8,
        "string" to StringExact(5),
        "i2" to Int8,
    )

    private val bitFields = bitfield(
        "pad" to BInt(3),
        "i8" to BInt8,
        "pad2" to BInt(3),
    )

    private val arrayType = struct(
        "array" to ArrayType(5, Int8)
    )

    private val dynamicStringArray = struct(
        "array" to ArrayType(2, Ascii())
    )

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testIntStruct(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(7)
        buffer.order(order)
        int3Struct.write(
            buffer, mapOf(
                "i32" to 0x0FFFFFF1,
                "i16" to 0x0FF1,
                "i8" to 0x01
            )
        )
        buffer.flip()
        val map = int3Struct.read(buffer)
        assertEquals(0x0FFFFFF1, map["i32"])
        assertEquals(0x0FF1, map["i16"])
        assertEquals(0x01, map["i8"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testStructInStruct(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(8)
        buffer.order(order)
        containerStruct.write(
            buffer, mapOf(
                "struct" to mapOf<String, Any>(
                    "i32" to 0x0FFFFFF1,
                    "i16" to 0x0FF1,
                    "i8" to 0x01
                ),
                "i8" to 0x10
            )
        )
        buffer.flip()
        val map = containerStruct.read(buffer).flatten(".")
        assertEquals(0x0FFFFFF1, map["struct.i32"])
        assertEquals(0x0FF1, map["struct.i16"])
        assertEquals(0x01, map["struct.i8"])
        assertEquals(0x10, map["i8"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testEmptyDynamicString(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(3)
        buffer.order(order)
        dynStringStruct.write(
            buffer, mapOf(
                "i1" to 0x0F,
                "string" to "",
                "i2" to 0x0F,
            )
        )
        buffer.flip()
        val map = dynStringStruct.read(buffer)
        assertEquals("", map["string"])
        assertEquals(0x0F, map["i1"])
        assertEquals(0x0F, map["i2"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testDynamicString(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(4)
        buffer.order(order)
        dynStringStruct.write(
            buffer, mapOf(
                "i1" to 0x0F,
                "string" to "!",
                "i2" to 0x0F,
            )
        )
        buffer.flip()
        val map = dynStringStruct.read(buffer)
        assertEquals("!", map["string"])
        assertEquals(0x0F, map["i1"])
        assertEquals(0x0F, map["i2"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testExactString(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(7)
        buffer.order(order)
        exactStringStruct.write(
            buffer, mapOf(
                "i1" to 0x0F,
                "string" to "hello",
                "i2" to 0x0F,
            )
        )
        buffer.flip()
        val map = exactStringStruct.read(buffer)
        assertEquals("hello", map["string"])
        assertEquals(0x0F, map["i1"])
        assertEquals(0x0F, map["i2"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testShortExactString(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(7)
        buffer.order(order)
        exactStringStruct.write(
            buffer, mapOf(
                "i1" to 0x2A,
                "string" to "42",
                "i2" to 0x2A,
            )
        )
        buffer.flip()
        println(buffer.toHex())
        val map = exactStringStruct.read(buffer)
        assertEquals("42", map["string"])
        assertEquals(42, map["i1"])
        assertEquals(42, map["i2"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testBitField(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(2)
        buffer.order(order)
        bitFields.write(
            buffer, mapOf(
                "pad" to -1,
                "i8" to -5,
                "pad2" to 2,
            )
        )
        buffer.flip()

        val map = bitFields.read(buffer)
        assertEquals(-1, map["pad"])
        assertEquals(-5, map["i8"])
        assertEquals(2, map["pad2"])
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testIntArray(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(5)
        buffer.order(order)
        arrayType.write(
            buffer, mapOf(
                "array" to listOf(0, 1, 2, 3, 4),
            )
        )
        buffer.flip()
        val array = arrayType.read(buffer)["array"] as? List<*> ?: fail("array not found")

        assertEquals(5, array.size)
        for (i in array.indices) {
            assertEquals(i, array[i])
        }
    }

    @ParameterizedTest
    @MethodSource("byteOrder")
    fun testStringArray(order: ByteOrder) {
        val buffer = ByteBuffer.allocate(12)
        buffer.order(order)
        dynamicStringArray.write(
            buffer, mapOf(
                "array" to listOf("hello", "world"),
            )
        )
        buffer.flip()
        val array = dynamicStringArray.read(buffer)["array"] as? List<*> ?: fail("array not found")
        assertEquals(2, array.size)
        assertEquals("hello", array[0])
        assertEquals("world", array[1])
    }

    companion object {

        @JvmStatic
        fun byteOrder(): Stream<ByteOrder> {
            return Stream.of(
                ByteOrder.BIG_ENDIAN,
                ByteOrder.LITTLE_ENDIAN,
            )
        }
    }
}