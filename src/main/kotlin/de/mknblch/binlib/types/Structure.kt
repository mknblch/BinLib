package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.*
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.extensions.hasRemaining
import de.mknblch.binlib.types.primitives.None
import java.nio.ByteBuffer

// Struct type
open class Structure(private val elements: Array<Pair<String, Type<Any>>>) : Type<Map<String, Any>> {

    override fun read(buffer: ByteBuffer): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        readStructure(buffer, elements) { k, t, v ->
            if (t is None) return@readStructure
            map[k] = v
        }
        return map
    }

    override fun write(buffer: ByteBuffer, value: Map<String, Any>): Int {
        var bytesWritten = 0
        for (i in elements.indices) {
            val (elementKey, elementType) = elements[i]
            if (elementType is None) continue // skip none
            requireState(buffer.hasRemaining(elementType.size())) { "BufferOverflow($buffer) in ${this.signature()} (${buffer.remaining()}/${size()})" }
            val element = value[elementKey] ?: throw IllegalArgumentException("Mandatory parameter '$elementKey' for structure $this not found")
            bytesWritten += elementType.write(buffer, element)
        }
        return bytesWritten
    }

    override fun size(): Int = SIZE_UNDEFINED

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun build(elementTypes: List<Pair<String, Type<*>>>): Structure =
            Structure(elementTypes.map { it as Pair<String, Type<Any>> }.toTypedArray())

        inline fun readStructure(buffer: ByteBuffer, elements: Array<Pair<String, Type<Any>>>, receiver: (String, Type<Any>, Any) -> Unit) {
            for (i in elements.indices) {
                val (elementKey, elementType) = elements[i]
                // if element size is undefined (-1) it will always be smaller the buffer.remaining() (0)
                requireState (buffer.remaining() >= elementType.size()) {
                    "BufferUnderflow($buffer) in Structure while reading '$elementKey' ($elementType) at index $i" }
                receiver(elementKey, elementType, elementType.read(buffer))
            }
        }
    }
}