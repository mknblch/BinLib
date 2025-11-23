package de.mknblch.binlib.decorators

import de.mknblch.binlib.types.primitives.UInt8
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class IntMapperTest {

    @Test
    fun testDecorate() {

        val elements = listOf("a", "abc", "hello world")

        val mapper = IntMapper(UInt8, elements)

        for (element in elements) {
            val buffer = ByteBuffer.wrap(ByteArray(1))
            println("writing $element")
            mapper.write(buffer, element)
            buffer.flip()
            val read = mapper.read(buffer)
            println("read: $read")

        }



    }
}