package de.mknblch.binlib.types.bitfields

import de.mknblch.binlib.BinLib

open class BBooleanArray(numBits: Int) : BinLib.BitField<BooleanArray>(numBits) {

    override fun decode(booleanArray: BooleanArray): BooleanArray = booleanArray
    override fun encode(value: Any): BooleanArray = (value as BooleanArray)
}