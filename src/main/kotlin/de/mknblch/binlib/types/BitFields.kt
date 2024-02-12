package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.BitField
import de.mknblch.binlib.BinLib.Companion.toByteArray
import de.mknblch.binlib.BinLib.Type
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * Bitfield mapper implementation
 */
class BitFields<K : Any>(private val elements: Array<Pair<K, BitField<Any>>>) : Type<Map<K, Any>> {

    private fun readArray(buffer: ByteBuffer, numBits: Int, startBit: Int = 0): BooleanArray {
        val bPos = buffer.position()
        val ret = BooleanArray(numBits) {
            val k = it + startBit
            buffer[k / 8 + bPos].and(bitMask[k % 8]) != ZERO
        }
        return ret
    }

    override fun read(buffer: ByteBuffer): Map<K, Any> {
        val result = mutableMapOf<K, Any>()
        var bitOffset = 0
        elements.forEach { (key, bitField) ->
            val array = readArray(buffer, bitField.numBits, bitOffset)
            result[key] = bitField.decode(array)
            bitOffset += bitField.numBits
        }
        buffer.position(buffer.position() + (bitOffset + 8 - 1) / 8)
        return result
    }

    override fun write(buffer: ByteBuffer, value: Map<K, Any>): Int {
        val maxBits = elements.sumOf { it.second.numBits }
        val array = BooleanArray(maxBits)
        var bitOffset = 0
        for ((elementKey, bitField) in elements) {
            val elementValue =
                value[elementKey] ?: throw IllegalArgumentException("mandatory parameter '$elementKey' not found")
            val encoded = bitField.encode(elementValue)
            System.arraycopy(encoded, 0, array, bitOffset, encoded.size)
            bitOffset += bitField.numBits
        }
        buffer.put(array.toByteArray())
        return (maxBits + 8 - 1) / 8
    }

    override fun size(): Int = (elements.sumOf { it.second.numBits } + 8 - 1) / 8

    companion object {

        private const val ZERO = 0x00.toByte()

        private val bitMask: ByteArray = ByteArray(8) {
            (0x01 shl it).toByte()
        }

        @Suppress("UNCHECKED_CAST")
        fun <K : Any> build(elementTypes: List<Pair<K, BitField<*>>>): BitFields<K> =
            BitFields(elementTypes.map { it as Pair<K, BitField<Any>> }.toTypedArray())
    }
}
