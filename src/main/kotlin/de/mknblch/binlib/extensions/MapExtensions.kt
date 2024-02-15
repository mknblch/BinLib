package de.mknblch.binlib.extensions

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.types.Structure

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

fun Map<String, Any>.flatten(delimiter: String = ".", recursive: Boolean = true) = this.flatten({ a, b -> "$a$delimiter$b" }, recursive)

@Suppress("UNCHECKED_CAST")
fun <K : Any> MutableMap<K, Any>.putRecursive(keys: List<K>, value: Any): Any? {
    if (keys.size == 1)  return this.put(keys[0], value)
    return (computeIfAbsent(keys[0]) {
        mutableMapOf<K, Any>()
    } as MutableMap<K, Any>).putRecursive(keys.drop(1), value)
}

@Suppress("UNCHECKED_CAST")
fun <K: Any?> Map<K, Any>.getRecursive(keys: List<K>): Any? {
    if (keys.size == 1) return this[keys[0]]
    return (this[keys[0]] as Map<K, Any>).getRecursive(keys.drop(1))
}
