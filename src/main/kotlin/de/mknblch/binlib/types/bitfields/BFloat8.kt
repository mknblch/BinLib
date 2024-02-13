package de.mknblch.binlib.types.bitfields

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.toBooleanArray
import de.mknblch.binlib.BinLib.Companion.toLong

// TODO
object BFloat8 : BinLib.BitField<Double>(Double.SIZE_BITS) {

    override fun decode(booleanArray: BooleanArray): Double = Double.fromBits(booleanArray.toLong())

    override fun encode(value: Any): BooleanArray = (value as Double).toRawBits().toBooleanArray()
}
