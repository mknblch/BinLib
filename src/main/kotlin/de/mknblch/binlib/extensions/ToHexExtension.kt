package de.mknblch.binlib.extensions

import java.nio.ByteBuffer

fun Byte?.toHex(): String = this?.let { "%02x".format(it) } ?: "null"

fun Byte?.toBits(): String = this?.toUByte()?.toString(2)?.padStart(8, '0') ?: "null"

fun UByte?.toHex(): String = this?.toString(16) ?: "null"

fun Int?.toHex(): String = this?.toString(16) ?: "null"

fun Int?.toBits(): String = this?.toString(2)?.padStart(32, '0') ?: "null"

fun UInt?.toHex(): String = this?.toString(16) ?: "null"

fun ByteArray.toHex(limit: Int = Int.MAX_VALUE, truncated: String = ".. (${this.size - limit} more)"): String =
    joinToString(
        separator = " ",
        limit = limit,
        truncated = truncated
    ) {
        "%02x".format(it, it)
    }


fun ByteBuffer.toHex(limit: Int = Int.MAX_VALUE, truncated: String = ".. (${this.limit() - limit} more)"): String =
    (position()..< limit()).map { get(it) }.joinToString(
        separator = " ",
        limit = limit,
        truncated = truncated
    ) {
        "%02x".format(it, it)
    }


fun String.hexToByteArray(): ByteArray {
    return this.trim().split(" ").mapNotNull {
        if (it.isBlank()) return@mapNotNull null
        it.toInt(16).toByte()
    }.toByteArray()
}
