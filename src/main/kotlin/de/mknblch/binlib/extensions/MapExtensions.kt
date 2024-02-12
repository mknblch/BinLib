package de.mknblch.binlib.extensions

fun <T> Map<T, Any>.flatten(keyMerger: (T, T) -> T, recursive: Boolean = true): Map<T, Any> {
    val result = mutableMapOf<T, Any>()

    fun flattenHelper(currentMap: Map<T, Any>, prefix: T?, recursive: Boolean) {
        for ((key, value) in currentMap) {
            val newKey = if (prefix != null) keyMerger(prefix, key) else key
            if (value is Map<*, *> && recursive) {
                @Suppress("UNCHECKED_CAST")
                flattenHelper(value as Map<T, Any>, newKey, recursive)
            } else {
                result[newKey] = value
            }
        }
    }

    flattenHelper(this, null, recursive)
    return result
}

fun Map<String, Any>.flatten(delimiter: String = ".", recursive: Boolean = true) = this.flatten({ a, b -> "$a$delimiter$b" }, recursive)
