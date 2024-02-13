package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import java.nio.BufferOverflowException
import java.nio.ByteBuffer

class None : BinLib.Type<Any?> {
    override fun read(buffer: ByteBuffer): Any? {
        if (buffer.hasRemaining()) throw BufferOverflowException()
        return null
    }

    override fun size(): Int = 0

    override fun write(buffer: ByteBuffer, value: Any?): Int = 0

}