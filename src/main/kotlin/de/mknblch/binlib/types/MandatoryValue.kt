package de.mknblch.binlib.types

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.BinLib.Companion.requireState
import de.mknblch.binlib.extensions.hasRemaining
import java.nio.ByteBuffer


/**
 * Provides a way to validate incoming binary data while parsing it. An exception is thrown if mandatoryValue is not
 * equal to the parsed value. Always writes the mandatory value without evaluating the actual boolean argument. Yet
 * a boolean value has to be supplied
 */
class MandatoryValue<T : Any?>(val type: BinLib.Type<T>, private val mandatoryValue: T) : BinLib.Type<Boolean> {

    override fun read(buffer: ByteBuffer): Boolean {
        requireState(buffer.hasRemaining(type.size())) { "BufferUnderflow! Not enough data to read ${type::class.simpleName} (${type.size()})" }
        val read = type.read(buffer)
        requireState (read == mandatoryValue) {
            "Failed to retrieve mandatory value '$mandatoryValue' by ${type::class.simpleName} (${type.size()}) got '$read' instead"
        }
        return true
    }

    override fun size(): Int = type.size()

    override fun write(buffer: ByteBuffer, value: Boolean): Int {
        requireState(buffer.hasRemaining(type.size())) {
            "BufferOverflow! Not enough space to write ${type::class.simpleName} (${type.size()})"
        }
        return type.write(buffer, mandatoryValue)
    }
}