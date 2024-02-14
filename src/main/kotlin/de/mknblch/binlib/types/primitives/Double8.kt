package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import java.nio.ByteBuffer

// Double
object Double8 : BinLib.Type<Double> {
    override fun read(buffer: ByteBuffer): Double = buffer.getDouble()

    override fun write(buffer: ByteBuffer, value: Double): Int {
        buffer.putDouble(value)
        return Double.SIZE_BYTES
    }

    override fun size(): Int = Double.SIZE_BYTES
}

