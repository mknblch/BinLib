package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import de.mknblch.binlib.extensions.hasRemaining
import de.mknblch.binlib.extensions.toHex
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Fixed size String.
 * Reads a string until null terminator is reached but
 * advances the buffer to length bytes after read.
 * Writes a string into the buffer and fills the rest
 * with null bytes. If the buffer has not enough space
 * for the null terminator it won't be written
 */
open class StringExact(private val length: Int, val charset: Charset = StandardCharsets.US_ASCII) : BinLib.Type<String> {

    override fun read(buffer: ByteBuffer): String {
        val startPosition = buffer.position()
        val nullByteOffset = findNullByteOffset(buffer, startPosition, length) ?: length
        // allocate buffer with actual string size
        val bytes = ByteArray(nullByteOffset)
        // read into bytes
        buffer.get(bytes)
        // increment buffer position
        buffer.position(startPosition + length)
        // transform to string
        return String(bytes, charset)
    }

    override fun write(buffer: ByteBuffer, value: String): Int {
        if (value.length > length) {
            throw IllegalArgumentException("String length(${value.length}) exceeds the specified length of $length")
        }
        val bytes = value.toByteArray(charset)
        if (!buffer.hasRemaining(bytes.size)) throw BufferOverflowException()
        buffer.put(bytes)
        if (bytes.size < length) {
            // Pad the remaining space with zeros if the string is shorter than the specified length
            val padding = ByteArray(length - bytes.size) { 0 }
            buffer.put(padding)
        }
        return length // Always return the specified length, indicating the buffer has advanced by this amount
    }

    override fun size(): Int = SIZE_UNDEFINED


    companion object {

        private const val NULL = 0.toByte()

        /**
         * find next null byte offset from startPosition
         * it is using absolute get so buffer position doesn't change
         * @return position of next null byte or null if not found
         */
        fun findNullByteOffset(buffer: ByteBuffer, startPosition: Int = buffer.position(), maxLength: Int = 0x0FFFF): Int? {
            val len = maxLength.coerceAtMost(buffer.remaining())
            for (i in 0..< len) {
                if (buffer[i + startPosition] == NULL) return i
            }
            return null
        }
    }
}
