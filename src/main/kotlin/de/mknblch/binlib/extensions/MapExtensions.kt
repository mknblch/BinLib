package de.mknblch.binlib.extensions

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
