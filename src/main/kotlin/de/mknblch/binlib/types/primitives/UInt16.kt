package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer


// UInt16
object UInt16 : BinLib.Type<Int> {
    override fun read(buffer: ByteBuffer): Int = buffer.short.toInt() and 0xFFFF

    override fun write(buffer: ByteBuffer, value: Int): Int {
        buffer.putShort(value.toShort())
        return Short.SIZE_BYTES
    }

    override fun size(): Int = Short.SIZE_BYTES
}