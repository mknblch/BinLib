package de.mknblch.binlib.types.primitives

import de.mknblch.binlib.BinLib
import kotlin.text.Charsets.US_ASCII

class Ascii(val length: Int): StringExact(length, US_ASCII) {

    override fun size(): Int = length
}
