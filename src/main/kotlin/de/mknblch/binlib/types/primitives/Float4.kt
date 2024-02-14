package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import java.nio.ByteBuffer

// Float
object Float4 : BinLib.Type<Float> {
    override fun read(buffer: ByteBuffer): Float = buffer.float

    override fun write(buffer: ByteBuffer, value: Float): Int {
        buffer.putFloat(value)
        return 4
    }

    override fun size(): Int = 4
}

