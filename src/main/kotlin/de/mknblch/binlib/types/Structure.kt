package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib.*
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.extensions.nest
import de.mknblch.binlib.types.primitives.None
import java.nio.ByteBuffer

// Struct type
open class Structure(val elements: Array<Pair<String, Type<Any>>>) : MapType {

    override fun getKeys(): List<String> = elements.map { it.first }

    override fun read(buffer: ByteBuffer): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        for (i in elements.indices) {
            val (elementKey, elementType) = elements[i]
            // if element size is undefined (-1) it will always be smaller the buffer.remaining() (0)
            requireState (buffer.remaining() >= elementType.size()) {
                "BufferUnderflow($buffer) in Structure while reading '$elementKey' ($elementType) at index $i" }
            if (elementType is None) continue;
            map[elementKey] = elementType.read(buffer)
        }
        return map
    }

    override fun write(buffer: ByteBuffer, value: Map<String, Any>): Int {
        var bytesWritten = 0
        for (i in elements.indices) {
            val (elementKey, elementType) = elements[i]
            // handle none
            if (elementType is None) continue // skip none
            // handle optionals
            when (elementType) {

                // argument optional, skip if neither value nor defaultValue is present
                is OptionalValue<*> -> {
                    (value[elementKey] ?: elementType.defaultValue)?.also {
                        bytesWritten += elementType.write(buffer, it)
                    }
                    continue
                }

                // argument optional, always writes its default value
                is MandatoryValue<*> -> {
                    // always write a mandatory value, even if value is missing in the arguments
                    bytesWritten += elementType.write(buffer, true)
                    continue
                }

                // argument optional but writes its default value if not null
                is DefaultWrite<*> -> {
                    val element = value[elementKey]
                    bytesWritten += if (element == null) {
                        elementType.write(buffer, null)
                    } else {
                        elementType.write(buffer, element)
                    }
                    continue
                }

                // handle rest, throw exception if argument is not provided
                else -> {
                    val element = value[elementKey] ?: throw IllegalArgumentException("Mandatory parameter '$elementKey' for structure $this not found")
                    bytesWritten += elementType.write(buffer, element)
                }
            }
        }
        return bytesWritten
    }

    fun write(buffer: ByteBuffer, values: List<Pair<String, Any>>, delimiter: String = ".") {
        write(buffer, values.nest(delimiter))
    }

    override fun size(): Int {
        var size = 0
        for ((_, type) in elements) {
            val elementSize = type.size()
            if (elementSize == SIZE_UNDEFINED) return SIZE_UNDEFINED
            size += elementSize
        }
        return size
    }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun build(elementTypes: List<Pair<String, Type<*>>>): Structure =
            Structure(elementTypes.map { it as Pair<String, Type<Any>> }.toTypedArray())
    }
}