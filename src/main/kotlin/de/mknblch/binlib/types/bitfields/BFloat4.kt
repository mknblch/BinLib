package de.mknblch.binlib.types.bitfields

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.toBooleanArray
import de.mknblch.binlib.BinLib.Companion.toInt

// TODO
object BFloat4: BinLib.BitField<Float>(Float.SIZE_BITS) {

    override fun decode(booleanArray: BooleanArray): Float = Float.fromBits(booleanArray.toInt())

    override fun encode(value: Any): BooleanArray = (value as Float).toRawBits().toBooleanArray()
}
