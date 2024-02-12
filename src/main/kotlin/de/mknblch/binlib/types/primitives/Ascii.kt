package de.mknblch.binlib.types.primitives

import kotlin.text.Charsets.US_ASCII

class Ascii(private val maxLength: Int = 1024): StringDynamic(maxLength, US_ASCII)