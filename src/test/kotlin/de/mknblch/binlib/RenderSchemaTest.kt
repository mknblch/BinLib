package de.mknblch.binlib

import de.mknblch.binlib.BinLib.Companion.array
import de.mknblch.binlib.BinLib.Companion.bitfield
import de.mknblch.binlib.BinLib.Companion.struct
import de.mknblch.binlib.types.OptionalValue
import de.mknblch.binlib.types.bitfields.BInt
import de.mknblch.binlib.types.bitfields.BInt8
import de.mknblch.binlib.types.primitives.Int16
import de.mknblch.binlib.types.primitives.Int32
import de.mknblch.binlib.types.primitives.Int8
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class RenderSchemaTest {

    @Test
    fun testRenderSchema_compact() {
        val inner = struct(
            "bla" to Int16,
        )

        val flags = bitfield(
            "pad" to BInt(3),
            "i8" to BInt8,
            "pad2" to BInt(3),
        )

        val outer = struct(
            "param1" to Int32,
            "param2" to inner,
            "arr" to array(5, Int8),
            "opt" to OptionalValue(Int8, 42),
            "flags" to flags,
        )

        val rendered = outer.renderSchema(pretty = false)

        assertEquals(
            "Struct{ param1=Int32, param2=Struct{ bla=Int16 }, arr=Array[len=5, of=Int8], opt=Optional[of=Int8, default=42], flags=BitField{ pad=i(3), i8=i(8), pad2=i(3) } }",
            rendered
        )

        println(outer.renderSchema(true))
    }

    @Test
    fun testRenderSchema_pretty() {
        val outer = struct(
            "i8" to Int8,
            "inner" to struct(
                "i32" to Int32,
            ),
        )

        val rendered = outer.renderSchema(pretty = true, indent = "  ")

        assertEquals(
            """
            Struct{
              i8=Int8,
              inner=Struct{
                i32=Int32
              }
            }
            """.trimIndent(),
            rendered
        )
        println(outer.renderSchema(true, indent = "  "))
    }
}