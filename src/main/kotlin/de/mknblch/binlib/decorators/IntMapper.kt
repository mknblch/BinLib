package de.mknblch.binlib.decorators

import de.mknblch.binlib.BinLib

open class IntMapper<O: Any>(parent: BinLib.Type<Int>, private val mapping: List<O?>) : BinLib.TypeDecorator<Int, O>(parent) {

    override fun decorateRead(input: Int): O = input.let { mapping[input] } ?: throw IllegalArgumentException("element $input not found")

    override fun decorateWrite(input: O): Int {
        val index = mapping.indexOf(input)
        if(index == -1) throw IllegalArgumentException("Mandatory index $input in $this not found")
        return index
    }

}