package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.*
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

// Struct type
class Structure<K : Any>(private val elements: Array<Pair<K, Type<Any>>>) : Type<Map<K, Any>> {

    override fun read(buffer: ByteBuffer): Map<K, Any> {
        val map = mutableMapOf<K, Any>()
        for (i in elements.indices) {
            val (elementKey, elementType) = elements[i]
            // if element size is undefined (-1) it will always be smaller the buffer.remaining() (0)
            if (buffer.remaining() < elementType.size()) {
                throw BufferUnderflowException()
            } else {
                val element = elementType.read(buffer) ?: continue
                map[elementKey] = element
            }
        }
        return map
    }

    override fun write(buffer: ByteBuffer, value: Map<K, Any>): Int {
        var bytesWritten = 0
        for (i in elements.indices) {
            val (elementKey, elementType) = elements[i]
            val element =
                value[elementKey] ?: throw IllegalArgumentException("mandatory parameter '$elementKey' not found")
            bytesWritten += elementType.write(buffer, element)
        }
        return bytesWritten
    }

    override fun size(): Int = SIZE_UNDEFINED

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun <K : Any> build(elementTypes: List<Pair<K, Type<*>>>): Structure<K> =
            Structure(elementTypes.map { it as Pair<K, Type<Any>> }.toTypedArray())
    }
}