package de.mknblch.binlib.decorators

import de.mknblch.binlib.BinLib

class IntMapper<O: Any>(parent: BinLib.Type<Int>, private val mapping: List<O?>) : BinLib.TypeDecorator<Int, O>(parent) {

    override fun decorateRead(input: Int?): O? = input?.let { mapping.getOrNull(input) }

    override fun decorateWrite(input: O): Int = mapping.indexOf(input)

}