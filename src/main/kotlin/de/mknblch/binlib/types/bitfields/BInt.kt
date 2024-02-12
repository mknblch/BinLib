package de.mknblch.binlib.types.bitfields

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.BinLib.Companion.toBooleanArray
import de.mknblch.binlib.BinLib.Companion.toInt

open class BInt(numBits: Int) : BinLib.BitField<Int>(numBits) {

    override fun decode(booleanArray: BooleanArray): Int = booleanArray.toInt()
    override fun encode(value: Any): BooleanArray = (value as Int).toBooleanArray(numBits)
}

