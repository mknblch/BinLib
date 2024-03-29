package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer

// Int16
object Int16 : BinLib.Type<Int> {
    override fun read(buffer: ByteBuffer): Int = buffer.short.toInt()

    override fun write(buffer: ByteBuffer, value: Int): Int {
        buffer.putShort(value.toShort())
        return Short.SIZE_BYTES
    }

    override fun size(): Int = Short.SIZE_BYTES
}