package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer

// TODO doc
class OptionalValue<T : Any?>(val type: BinLib.Type<T>, val defaultValue: T? = null) : BinLib.Type<T?> {

    override fun read(buffer: ByteBuffer): T? {
        return when {
            type.size() == SIZE_UNDEFINED && buffer.hasRemaining() -> type.read(buffer)
            type.size() <= buffer.remaining() -> type.read(buffer)
            else -> defaultValue
        }
    }

    override fun size(): Int = SIZE_UNDEFINED

    override fun write(buffer: ByteBuffer, value: T?): Int {
        return when {
            value == null && defaultValue != null && buffer.hasRemaining(type.size()) -> type.write(buffer, defaultValue)
            value == null -> 0
            type.size() == SIZE_UNDEFINED && buffer.hasRemaining() -> type.write(buffer, value)
            type.size() <= buffer.remaining() -> type.write(buffer, value)
            else -> 0
        }
    }
}