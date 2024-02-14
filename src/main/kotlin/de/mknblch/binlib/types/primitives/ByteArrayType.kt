package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import java.nio.ByteBuffer

/**
 * returns data ar raw byte array.
 * The size of the byte array to be read can be fixed or dynamic. If a fixed size is specified,
 * it will read exactly that number of bytes from the buffer. Otherwise, it will read all remaining
 * bytes in the buffer.
 */
class ByteArrayType(val size: Int?) : BinLib.Type<ByteArray> {
    override fun read(buffer: ByteBuffer): ByteArray {
        return ByteArray(size ?: buffer.remaining()).also {
            buffer.get(it)
        }
    }

    override fun write(buffer: ByteBuffer, value: ByteArray): Int {
        buffer.put(value)
        return value.size
    }

    override fun size(): Int = size ?: SIZE_UNDEFINED

}