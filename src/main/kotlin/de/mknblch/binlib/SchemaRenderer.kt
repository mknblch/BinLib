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

    private fun renderType(type: BinLib.Type<*>, level: Int): String {
        val unwrapped = unwrapDelegatedType(type)
        return when (unwrapped) {
            is Structure -> renderStruct(unwrapped, level)
            is BitFields -> renderBitFields(unwrapped, level)
            is ArrayType<*> -> renderArray(unwrapped, level)
            is OptionalValue<*> -> "Optional[of=${renderType(unwrapped.type, level)}, default=${renderDefaultValue(unwrapped.defaultValue)}]"
            is DefaultValue<*> -> "Default[of=${renderType(unwrapped.type, level)}, value=${renderDefaultValue(unwrapped.defaultValue)}]"
            is MandatoryValue<*> -> "Mandatory[of=${renderType(unwrapped.type, level)}]"
            is None -> "None"
            is ByteArrayType -> "Bytes[len=${unwrapped.size ?: "?"}]"
            is Ascii -> "Ascii[len=${unwrapped.length}]"
            is AsciiDynamic -> "AsciiDynamic[maxLen=${renderReflectInt(unwrapped, "maxLength") ?: "?"}]"
            is StringExact -> "StringExact[len=${renderReflectInt(unwrapped, "length") ?: "?"}]"
            is StringDynamic -> "StringDynamic[maxLen=${renderReflectInt(unwrapped, "maxLength") ?: "?"}]"

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

            else -> unwrapped::class.simpleName ?: "Type"
        }
    }

    private fun unwrapDelegatedType(type: BinLib.Type<*>, maxDepth: Int = 8): BinLib.Type<*> {
        var current: BinLib.Type<*> = type
        repeat(maxDepth) {
            val delegate = findKotlinDelegate(current) ?: return current
            if (delegate === current) return current
            current = delegate
        }
        return current
    }

    private fun findKotlinDelegate(type: BinLib.Type<*>): BinLib.Type<*>? = runCatching {
        val cls = type::class.java
        val f = cls.declaredFields.firstOrNull { field ->
            field.name.startsWith("\$\$delegate_") && BinLib.Type::class.java.isAssignableFrom(field.type)
        } ?: return null
        f.isAccessible = true
        f.get(type) as? BinLib.Type<*>
    }.getOrNull()

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
        is BInt -> "i(${bitField.numBits})"
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

    private fun renderReflectInt(target: Any, propertyName: String): Int? = runCatching {
        val f = target::class.java.getDeclaredField(propertyName)
        f.isAccessible = true
        f.get(target) as? Int
    }.getOrNull()
}