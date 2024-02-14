package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer


// Array type
class ArrayType<T : Any>(private val length: Int, private val type: BinLib.Type<T>) : BinLib.Type<List<T?>> {

    override fun read(buffer: ByteBuffer): List<T?> {
        requireState(buffer.hasRemaining(size())) { "BufferUnderflow($buffer) in ${this.signature()} (${buffer.remaining()}/${size()})" }
        val list = mutableListOf<T?>()
        for (i in 0..<length) {
            list.add(type.read(buffer))
        }
        return list
    }

    override fun write(buffer: ByteBuffer, value: List<T?>): Int {
        requireState(buffer.hasRemaining(size())) { "BufferOverflow($buffer) in ${this.signature()} (${buffer.remaining()}/${size()})" }
        return value.indices.sumOf { i ->
            val element = value[i] ?: throw IllegalArgumentException("Element at index '$i' in $this was null")
            type.write(buffer, element)
        }
    }

    override fun size(): Int = if (type.size() == SIZE_UNDEFINED) SIZE_UNDEFINED else type.size() * length
}
