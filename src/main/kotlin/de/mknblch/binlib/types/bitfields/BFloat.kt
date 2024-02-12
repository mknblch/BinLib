package de.mknblch.binlib.types.bitfields

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.toBooleanArray
import de.mknblch.binlib.BinLib.Companion.toUInt

// TODO
open class BFloat(numBits: Int) : BinLib.BitField<Float>(numBits) {

    override fun decode(booleanArray: BooleanArray): Float = Float.fromBits(booleanArray.toUInt().toInt())

    override fun encode(value: Any): BooleanArray = (value as Float).toRawBits().toBooleanArray(numBits)
}
