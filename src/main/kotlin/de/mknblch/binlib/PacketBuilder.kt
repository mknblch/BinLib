package de.mknblch.binlib

import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.types.Structure
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PacketBuilder(private val structure: Structure, private val endiness: ByteOrder = ByteOrder.LITTLE_ENDIAN, val allocationSize: Int = 1024) {

    val arguments = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun getArgument(vararg keys: String): Any? {
        var data: Map<String, Any> = arguments
        for (i in keys.indices) {
            val key = keys[i]
            if (i == keys.size - 1) { // last key
                return data[key]
            }
            data = data[key] as? Map<String, Any> ?: throw IllegalArgumentException("Illegal argument at $key")
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun getElement(vararg keys: String): Any? {
        return null
    }

    fun <V: Any> set(key: String, value: V) : PacketBuilder {
        val type = structure.elements.firstOrNull { it.first == key }?.second ?: throw IllegalArgumentException("Invalid argument! '$key' not found")
        arguments[key] = value
        return this
    }

    fun build(): ByteArray {
        val size = structure.size()
        val array = if (size == SIZE_UNDEFINED) ByteArray(allocationSize) else ByteArray(size)
        val buffer = ByteBuffer.wrap(array).also { it.order(endiness) }
        val bytesWritten = structure.write(buffer, arguments)
        requireState(bytesWritten == size || size == SIZE_UNDEFINED) {
            "Written size ($bytesWritten) and calculated size ($size) do not match"
        }

        return array.sliceArray(0 ..< bytesWritten)
    }

}