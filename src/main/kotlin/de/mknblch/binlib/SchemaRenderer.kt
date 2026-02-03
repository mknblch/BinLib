package de.mknblch.binlib

import de.mknblch.binlib.types.*
import de.mknblch.binlib.types.bitfields.*
import de.mknblch.binlib.types.primitives.*

/**
 * Render a BinLib type into a textual, map-like schema DSL.
 *
 */
fun BinLib.Type<*>.renderSchema(
    pretty: Boolean = false,
    indent: String = "  "
): String = SchemaRenderer(pretty, indent).renderType(this)

private class SchemaRenderer(
    private val pretty: Boolean,
    private val indentUnit: String
) {
    fun renderType(type: BinLib.Type<*>): String = renderType(type, level = 0)

    private fun renderType(type: BinLib.Type<*>, level: Int): String = when (type) {
        is Structure -> renderStruct(type, level)
        is BitFields -> renderBitFields(type, level)
        is ArrayType<*> -> renderArray(type, level)
        is OptionalValue<*> -> "Optional[of=${renderType(type.type, level)}, default=${renderDefaultValue(type.defaultValue)}]"
        is DefaultValue<*> -> "Default[of=${renderType(type.type, level)}, value=${renderDefaultValue(type.defaultValue)}]"
        is MandatoryValue<*> -> {
            // mandatoryValue is private in your type; we can still show the wrapper.
            "Mandatory[of=${renderType(type.type, level)}]"
        }
        is None -> "None"
        is ByteArrayType -> "Bytes[len=${type.size ?: "?"}]"
        is Ascii -> "Ascii[len=${type.length}]"
        is AsciiDynamic -> "AsciiDynamic[maxLen=${renderReflectInt(type, "maxLength") ?: "?"}]"
        is StringExact -> "StringExact[len=${renderReflectInt(type, "length") ?: "?"}]"
        is StringDynamic -> "StringDynamic[maxLen=${renderReflectInt(type, "maxLength") ?: "?"}]"

        // primitives
        Int8 -> "Int8"
        Int16 -> "Int16"
        Int32 -> "Int32"
        UInt8 -> "UInt8"
        UInt16 -> "UInt16"
        UInt32 -> "UInt32"
        Float4 -> "Float4"
        Double4 -> "Double4"
        Double8 -> "Double8"

        else -> type::class.simpleName ?: "Type"
    }

    private fun renderStruct(struct: Structure, level: Int): String {
        if (struct.elements.isEmpty()) return "Struct{}"

        val sep = if (pretty) ",\n" else ", "
        val open = "Struct{"
        val close = if (pretty) "\n${indent(level)}}"
        else " }"

        val innerIndent = if (pretty) indent(level + 1) else ""
        val entries = struct.elements.joinToString(sep) { (name, t) ->
            val rendered = renderType(t, level + 1)
            if (pretty) "$innerIndent$name=$rendered" else "$name=$rendered"
        }

        return if (pretty) "$open\n$entries$close" else "$open $entries$close"
    }

    private fun renderArray(array: ArrayType<*>, level: Int): String {
        val len = array.length?.toString() ?: "?"
        val of = renderType(array.type, level)
        return "Array[len=$len, of=$of]"
    }

    private fun renderBitFields(bitFields: BitFields, level: Int): String {
        if (bitFields.elements.isEmpty()) return "BitField{}"

        val sep = if (pretty) ",\n" else ", "
        val open = "BitField{"
        val close = if (pretty) "\n${indent(level)}}"
        else " }"

        val innerIndent = if (pretty) indent(level + 1) else ""
        val entries = bitFields.elements.joinToString(sep) { (name, bf) ->
            val rendered = renderBitFieldType(bf)
            if (pretty) "$innerIndent$name=$rendered" else "$name=$rendered"
        }

        return if (pretty) "$open\n$entries$close" else "$open $entries$close"
    }

    private fun renderBitFieldType(bitField: BinLib.BitField<Any>): String = when (bitField) {
        is BInt -> "i(${bitField.numBits})"           // signed, best-effort
        BInt8 -> "i(8)"
        BInt16 -> "i(16)"
        BInt32 -> "i(32)"
        is BBooleanArray -> "bits(${bitField.numBits})"
        BFloat4 -> "f(32)"
        BDouble8 -> "d(64)"
        else -> "bit(${bitField.numBits})"
    }

    private fun renderDefaultValue(v: Any?): String = when (v) {
        null -> "null"
        is String -> "\"${v.replace("\"", "\\\"")}\""
        is Char -> "'$v'"
        else -> v.toString()
    }

    private fun indent(level: Int): String = indentUnit.repeat(level)

    /**
     * Best-effort reflection helper to extract private ctor params (optional).
     * If you don't want reflection at all, you can delete this and always emit '?'.
     */
    private fun renderReflectInt(target: Any, propertyName: String): Int? = runCatching {
        val f = target::class.java.getDeclaredField(propertyName)
        f.isAccessible = true
        f.get(target) as? Int
    }.getOrNull()
}