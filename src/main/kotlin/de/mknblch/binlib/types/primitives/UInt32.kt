package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer

// UInt32
object UInt32 : BinLib.Type<Long> {
    override fun read(buffer: ByteBuffer): Long = buffer.int.toLong() and 0xFFFFFFFFL

    override fun write(buffer: ByteBuffer, value: Long): Int {
        buffer.putInt(value.toInt())
        return Int.SIZE_BYTES
    }

    override fun size(): Int = Int.SIZE_BYTES
}