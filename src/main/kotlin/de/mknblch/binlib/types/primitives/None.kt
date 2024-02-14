package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.extensions.toHex
import java.nio.BufferOverflowException
import java.nio.ByteBuffer

/**
 * Expects end of the buffer on read or throws exception and does nothing on write.
 */
object None : BinLib.Type<Any> {
    override fun read(buffer: ByteBuffer): Any {
        BinLib.requireState(!buffer.hasRemaining()) { "Expecting empty buffer but got $buffer | ${buffer.toHex()}" }
        return true
    }

    override fun size(): Int = 0

    override fun write(buffer: ByteBuffer, value: Any): Int = 0

}