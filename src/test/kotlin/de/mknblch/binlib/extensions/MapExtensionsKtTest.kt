package de.mknblch.binlib.extensions

import org.junit.jupiter.api.Test

class MapExtensionsKtTest {


    @Test
    fun testPutRecursive() {
        val map = mutableMapOf<String, Any>()
        map.putRecursive(
            listOf("a", "b", "c"),
            42
        )
        println(map)
        println(map.getRecursive(listOf("a", "b", "c")))
    }
}