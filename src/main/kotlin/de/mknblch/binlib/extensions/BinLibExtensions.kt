package de.mknblch.binlib.extensions

import java.nio.ByteBuffer

/**
 * @return true if at least size bytes are available, false otherwise
 * true for values <0 as well
 */
fun ByteBuffer.hasRemaining(size: Int): Boolean {
    val remaining = this.remaining()
    return remaining > 0 && remaining >= size
}

/**
 * flatten a map by remapping keys of sub maps
 */
fun Map<String, Any>.flatten(keyMerger: (String, String) -> String, recursive: Boolean = true): Map<String, Any> {
    val result = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun flattenHelper(currentMap: Map<String, Any>, prefix: String?, recursive: Boolean) {
        for ((key, value) in currentMap) {
            val newKey = if (prefix != null) keyMerger(prefix, key) else key
            if (value is Map<*, *> && recursive) {
                flattenHelper(value as Map<String, Any>, newKey, recursive)
            } else {
                result[newKey] = value
            }
        }
    }

    flattenHelper(this, null, recursive)
    return result
}

/**
 * flatten a Map<String, Any> by merging keys using "." as default delimiter
 */
fun Map<String, Any>.flatten(delimiter: String = ".", recursive: Boolean = true) = this.flatten({ a, b -> "$a$delimiter$b" }, recursive)

/**
 * set a value recursively. if multiple keys are specified a map
 * is created (if not existing) for each key before the last one
 */
@Suppress("UNCHECKED_CAST")
fun <K : Any> MutableMap<K, Any>.putRecursive(keys: List<K>, value: Any): Any? {
    if (keys.size == 1)  return this.put(keys[0], value)
    return (computeIfAbsent(keys[0]) {
        mutableMapOf<K, Any>()
    } as MutableMap<K, Any>).putRecursive(keys.drop(1), value)
}

/**
 * gets a value recursively from a maps within maps
 */
@Suppress("UNCHECKED_CAST")
fun <K: Any?> Map<K, Any>.getRecursive(keys: List<K>): Any? {
    if (keys.size == 1) return this[keys[0]]
    return (this[keys[0]] as Map<K, Any>).getRecursive(keys.drop(1))
}

/**
 * return hex representation or null
 */
fun Byte?.toHex(): String = this?.let { "%02x".format(it) } ?: "null"
fun Byte?.toBits(): String = this?.toUByte()?.toString(2)?.padStart(8, '0') ?: "null"
fun UByte?.toHex(): String = this?.toString(16) ?: "null"
fun Int?.toHex(): String = this?.toString(16) ?: "null"
fun Int?.toBits(): String = this?.toString(2)?.padStart(32, '0') ?: "null"
fun UInt?.toHex(): String = this?.toString(16) ?: "null"

/**
 * renders a byte array to a hex string
 */
fun ByteArray.toHex(limit: Int = Int.MAX_VALUE, truncated: String = ".. (${this.size - limit} more)"): String =
    joinToString(
        separator = " ",
        limit = limit,
        truncated = truncated
    ) {
        "%02x".format(it, it)
    }

/**
 * render a byte buffer to a hex string without changing the buffer position
 */
fun ByteBuffer.toHex(limit: Int = Int.MAX_VALUE, truncated: String = ".. (${this.limit() - limit} more)"): String =
    (position()..< limit()).map { get(it) }.joinToString(
        separator = " ",
        limit = limit,
        truncated = truncated
    ) {
        "%02x".format(it, it)
    }

/**
 * transform a hex representation in a string into an actual byteArray
 * example: "00 01 02 0F AA ff"
 */
fun String.hexToByteArray(): ByteArray {
    return this.trim().split(" ").mapNotNull {
        if (it.isBlank()) return@mapNotNull null
        it.toInt(16).toByte()
    }.toByteArray()
}
