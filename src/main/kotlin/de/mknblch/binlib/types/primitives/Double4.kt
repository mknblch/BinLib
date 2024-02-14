package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import java.nio.ByteBuffer

// Double -> Float
object Double4 : BinLib.Type<Double> {
    override fun read(buffer: ByteBuffer): Double = buffer.float.toDouble()

    override fun write(buffer: ByteBuffer, value: Double): Int {
        buffer.putFloat(value.toFloat())
        return 4
    }

    override fun size(): Int = 4
}

