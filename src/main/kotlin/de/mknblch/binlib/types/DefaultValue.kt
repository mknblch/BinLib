package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer


/**
 * defaults to the given value if no argument for this type is provided. does not change read-behaviour
 */
class DefaultValue<T : Any?>(val type: BinLib.Type<T>, val defaultValue: T) : BinLib.Type<T?> {

    override fun read(buffer: ByteBuffer): T? = type.read(buffer)

    override fun size(): Int = type.size()

    override fun write(buffer: ByteBuffer, value: T?): Int {

        val writeValue = value ?: defaultValue

        requireState(buffer.hasRemaining(type.size())) {
            "BufferOverflow! Not enough space to write ${type::class.simpleName} (${type.size()})"
        }

        return type.write(buffer, writeValue)
    }
}