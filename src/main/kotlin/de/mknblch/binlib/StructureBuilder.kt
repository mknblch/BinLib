package de.mknblch.binlib

import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.extensions.putRecursive
import de.mknblch.binlib.types.Structure
import java.nio.ByteBuffer
import java.nio.ByteOrder

class StructureBuilder(
    private val structure: Structure,
    private val endiness: ByteOrder = ByteOrder.LITTLE_ENDIAN,
    val allocationSize: Int = 1024
) {

    val arguments = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    private tailrec fun getArgument(keys: List<String>, element: BinLib.Type<*>?): BinLib.Type<Any>? {
        if (null == element) throw IllegalArgumentException("Element ${keys.drop(1)} not found")
        if (keys.isEmpty()) return element as BinLib.Type<Any>
        if (element !is Structure) throw IllegalArgumentException("Element $keys not found")
        return getArgument(keys.drop(1), element.elements.firstOrNull { it.first == keys[0] }?.second)
    }

    fun <V : Any> set(key: String, value: V): StructureBuilder {
        val keys = key.split('.')
        getArgument(keys, structure) ?: throw IllegalArgumentException("Invalid argument! '$key' not found")
        arguments.putRecursive(keys, value)
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
        return array.sliceArray(0..<bytesWritten)
    }

}