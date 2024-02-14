package de.mknblch.binlib

import de.mknblch.binlib.types.ArrayType
import de.mknblch.binlib.types.BitFields
import de.mknblch.binlib.types.Structure
import de.mknblch.binlib.types.primitives.ByteArrayType
import java.nio.ByteBuffer
import kotlin.experimental.or

class BinLib {

    interface Type<T : Any?> {

        /**
         * read value from the buffer
         * @param buffer
         * @return value parsed from the buffer
         * @throws IllegalArgumentException
         * @throws java.nio.BufferUnderflowException
         */
        fun read(buffer: ByteBuffer): T

        /**
         * write value to the buffer
         * @param buffer
         * @param value the value to write
         * @return number of bytes written
         * @throws java.nio.BufferOverflowException
         */
        fun write(buffer: ByteBuffer, value: T): Int

        /**
         * type size
         * @return size of the type in bytes or -1 if unknown
         */
        fun size(): Int

        fun signature(): String {
            val className = this::class.simpleName ?: "?"
            val typeParams = this::class.typeParameters
            return if (typeParams.isEmpty()) className else
                "$className <${typeParams.joinToString { it.name }}>"
        }

    }

    /**
     * abstract type for bitfields
     */
    abstract class BitField<T: Any> (val numBits: Int) {

        /**
         * decode boolean array into target type
         */
        abstract fun decode(booleanArray: BooleanArray): T

        /**
         * encode value into a boolean array
         */
        abstract fun encode(value: Any): BooleanArray


    }

    /**
     * type decorator
     */
    abstract class TypeDecorator<I: Any, O: Any>(private val parent: Type<I>) : Type<O> {

        override fun read(buffer: ByteBuffer): O = decorateRead(parent.read(buffer))
        override fun write(buffer: ByteBuffer, value: O): Int = parent.write(buffer, decorateWrite(value))
        override fun size(): Int = parent.size()

        /**
         * map result value from parent type into target type
         */
        abstract fun decorateRead(input: I): O

        /**
         * map input value to parent type
         */
        abstract fun decorateWrite(input: O): I

    }

    /**
     * bitfield decorator
     */
    abstract class BitFieldDecorator<I: Any, O: Any>(private val parent: BitField<I>) : BitField<O>(parent.numBits) {

        override fun decode(booleanArray: BooleanArray): O = decorateRead(parent.decode(booleanArray))
        override fun encode(value: Any): BooleanArray = parent.encode(decorateWrite(value))

        /**
         * map result value from parent type into target type
         */
        abstract fun decorateRead(input: I): O

        /**
         * map input value to parent type
         */
        abstract fun decorateWrite(input: Any): I
    }


    companion object {

        fun struct(vararg types: Pair<String, Type<*>>): Structure = Structure.build(types.toList())

        fun bitfield(vararg types: Pair<String, BitField<*>>): BitFields = BitFields.build(types.toList())

        fun <T: Any> array(length: Int, type: Type<T>) = ArrayType(length, type)

        fun byteArray(length: Int? = null) = ByteArrayType(length)

        fun <I: Any, O: Any> BitField<I>.decorate(
            readDecorator: (I) -> O,
            writeDecorator: (O) -> I
        ): BitFieldDecorator<I, O> {
            return object : BitFieldDecorator<I, O>(this) {
                override fun decorateRead(input: I): O = readDecorator(input)

                @Suppress("UNCHECKED_CAST")
                override fun decorateWrite(input: Any): I = writeDecorator(input as O)
            }
        }

        fun <I: Any, O: Any> Type<I>.decorate(
            readDecorator: (I) -> O,
            writeDecorator: (O) -> I
        ): TypeDecorator<I, O> {
            return object : TypeDecorator<I, O>(this) {
                override fun decorateRead(input: I): O = readDecorator(input)
                override fun decorateWrite(input: O): I = writeDecorator(input)
            }
        }

        /**
         * transform a boolean array into a Long using 2-complement
         */
        fun BooleanArray.toSignedLong(): Long {
            var r: Long = 0
            if (this[size - 1]) {
                for (i in indices) if (!this[i]) r = r or (1L shl i)
            } else {
                for (i in indices) if (this[i]) r = r or (1L shl i)
            }
            return if (this[size - 1]) -r - 1 else r
        }

        fun Int.toBooleanArray(size: Int = 32): BooleanArray {
            val result = BooleanArray(size) { i ->
                this shr i and 1 == 1
            }
            return result
        }

        fun Long.toBooleanArray(size: Int = 64): BooleanArray {
            val result = BooleanArray(size) { i ->
                this shr i and 1L == 1L
            }
            return result
        }

        fun BooleanArray.toSignedInt(): Int {
            var r: Int = 0
            if (this[size - 1]) {
                for (i in indices) if (!this[i]) r = r or (1 shl i)
            } else {
                for (i in indices) if (this[i]) r = r or (1 shl i)
            }
            return if (this[size - 1]) -r - 1 else r
        }


        /**
         * transform a boolean array into an UInt
         */
        fun BooleanArray.toUInt(): UInt {
            var n = 0u
            for (b in this) n = n shl 1 or if (b) 1u else 0u
            return n
        }

        /**
         * transform a boolean array to a byte array. pads the
         * array with 0 if it's size it not a multiple of 8
         */
        fun BooleanArray.toByteArray(): ByteArray {
            val byteArraySize = (this.size + 7) / 8
            val byteArray = ByteArray(byteArraySize) { 0 } // Init with 0s
            this.forEachIndexed { index, bit ->
                if (!bit) return@forEachIndexed
                byteArray[index / 8] = byteArray[index / 8] or (1 shl index % 8).toByte()
            }

            return byteArray
        }

        /**
         * byte to boolean array (size 8)
         */
        fun Byte.toBooleanArray(): BooleanArray {
            val intValue = this.toInt()
            val result = BooleanArray(8) { i ->
                intValue shr i and 1 == 1
            }
            return result
        }

        fun requireState(state: Boolean, messageSupplier: () -> String) {
            if (!state) throw IllegalStateException(messageSupplier())
        }

        const val SIZE_UNDEFINED: Int = -1

    }
}