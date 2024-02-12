package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.SIZE_UNDEFINED
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Reads a null terminated string and increments the buffers position to the end of the string (including null byte).
 * Throws an exception if maxLength is reached during read.
 */
open class StringDynamic(private val maxLength: Int = Int.MAX_VALUE, val charset: Charset = StandardCharsets.US_ASCII) :
    BinLib.Type<String> {
    override fun read(buffer: ByteBuffer): String {
        val startPosition = buffer.position()
        val nullByteOffset = findNullByteOffset(buffer, startPosition) ?: throw IllegalArgumentException("Null terminator not found")
        val bytes = ByteArray(nullByteOffset)
        buffer.get(bytes)
        if(buffer.get() != NULL) throw IllegalArgumentException("Null terminator not found")
        return String(bytes, charset)
    }

    override fun write(buffer: ByteBuffer, value: String): Int {
        if (value.length >= maxLength) {
            throw IllegalArgumentException("String length exceeds maximum length of ${maxLength - 1}")
        }
        val bytes = value.toByteArray(charset)
        buffer.put(bytes)
        buffer.put(0.toByte()) // Null terminator
        return bytes.size + 1 // Include null terminator in count
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
            for (i in 0..< maxLength) {
                if (buffer[i + startPosition] == NULL) return i
            }
            return null
        }
    }
}
