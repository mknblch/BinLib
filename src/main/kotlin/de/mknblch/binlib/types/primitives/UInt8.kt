package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import java.nio.ByteBuffer


// UInt8
object UInt8 : BinLib.Type<Int> {
    override fun read(buffer: ByteBuffer): Int? =
        if (buffer.hasRemaining()) (buffer.get().toInt() and 0xFF) else null

    override fun write(buffer: ByteBuffer, value: Int): Int {
        buffer.put(value.toByte())
        return Byte.SIZE_BYTES
    }

    override fun size(): Int = Byte.SIZE_BYTES
}