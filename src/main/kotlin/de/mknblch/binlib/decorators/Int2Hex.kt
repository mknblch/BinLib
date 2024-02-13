package de.mknblch.binlib.decorators

import de.mknblch.binlib.BinLib

class Int2Hex(parent: BinLib.Type<Int>) : BinLib.TypeDecorator<Int, String>(parent) {

    override fun decorateRead(input: Int): String = input.toString(16)

    override fun decorateWrite(input: String): Int = Integer.parseInt(input, 16)

}
