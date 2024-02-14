package de.mknblch.binlib.decorators

import de.mknblch.binlib.BinLib
import de.mknblch.binlib.types.primitives.Int32

class Int2Hex(parent: BinLib.Type<Int> = Int32) : BinLib.TypeDecorator<Int, String>(parent) {

    override fun decorateRead(input: Int): String = input.toString(16)

    override fun decorateWrite(input: String): Int = Integer.parseInt(input, 16)

}
