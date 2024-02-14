package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.*
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.BinLib.Companion.toByteArray
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * Bitfield mapper implementation
 */
class BitFields(val elements: Array<Pair<String, BitField<Any>>>) : MapType {

    override fun read(buffer: ByteBuffer): Map<String, Any> {
        requireState(buffer.hasRemaining(size())) {
            "BufferUnderflow($buffer) in ${this.signature()} (${buffer.remaining()}/${size()})"
        }
        val result = mutableMapOf<String, Any>()
        var bitOffset = 0
        elements.forEach { (key, bitField) ->
            val array = readArray(buffer, bitField.numBits, bitOffset)
            result[key] = bitField.decode(array)
            bitOffset += bitField.numBits
        }
        buffer.position(buffer.position() + (bitOffset + 7) / 8)
        return result
    }

    override fun write(buffer: ByteBuffer, value: Map<String, Any>): Int {
        val numBits = elements.sumOf { it.second.numBits }
        val numBytes = (numBits + 7) / 8
        requireState(buffer.hasRemaining(numBytes)) { "BufferUnderflow($buffer) in ${this.signature()} (${buffer.remaining()}/$numBytes)" }
        val array = BooleanArray(numBits)
        var bitOffset = 0
        for ((elementKey, bitField) in elements) {
            val elementValue =
                value[elementKey] ?: throw IllegalArgumentException("mandatory parameter '$elementKey' not found")
            val encoded = bitField.encode(elementValue)
            System.arraycopy(encoded, 0, array, bitOffset, encoded.size)
            bitOffset += bitField.numBits
        }
        buffer.put(array.toByteArray())
        return (numBits + 7) / 8
    }

    override fun size(): Int = (elements.sumOf { it.second.numBits } + 7) / 8

    companion object {

        private const val ZERO = 0x00.toByte()

        private val bitMask: ByteArray = ByteArray(8) {
            (0x01 shl it).toByte()
        }

        fun readArray(buffer: ByteBuffer, numBits: Int, startBit: Int = 0): BooleanArray {
            val bPos = buffer.position()
            val ret = BooleanArray(numBits) {
                val k = it + startBit
                buffer[k / 8 + bPos].and(bitMask[k % 8]) != ZERO
            }
            return ret
        }

        @Suppress("UNCHECKED_CAST")
        fun build(elementTypes: List<Pair<String, BitField<*>>>): BitFields =
            BitFields(elementTypes.map { it as Pair<String, BitField<Any>> }.toTypedArray())
    }
}
