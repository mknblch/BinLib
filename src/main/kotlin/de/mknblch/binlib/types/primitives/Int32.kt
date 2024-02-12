package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer

// Int32
object Int32 : BinLib.Type<Int> {
    override fun read(buffer: ByteBuffer): Int? = if (buffer.hasRemaining(Int.SIZE_BYTES)) buffer.int else null
    override fun write(buffer: ByteBuffer, value: Int): Int {
        buffer.putInt(value)
        return Int.SIZE_BYTES
    }

    override fun size(): Int = Int.SIZE_BYTES
}
