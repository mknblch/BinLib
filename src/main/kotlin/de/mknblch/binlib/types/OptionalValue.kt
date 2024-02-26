package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer

/**
 * read a value from the buffer if either enough data is available or size is undefined and the buffer has at least one
 * byte remaining. read() returns the default value otherwise.
 * Writes value argument if not null, default value otherwise (if not null). Ensures that enough space is available
 * if the type has a predefined size or otherwise at least one byte remaining
 */
class OptionalValue<T : Any?>(val type: BinLib.Type<T>, val defaultValue: T? = null) : BinLib.Type<T?> {

    override fun read(buffer: ByteBuffer): T? {
        return when {
            buffer.hasRemaining(type.size()) -> type.read(buffer)
            else -> defaultValue
        }
    }

    override fun size(): Int = SIZE_UNDEFINED

    override fun write(buffer: ByteBuffer, value: T?): Int {
        val v = value ?: defaultValue
        return when {
            v == null -> 0
            buffer.hasRemaining(type.size()) -> type.write(buffer, v)
            else -> 0
        }
    }
}