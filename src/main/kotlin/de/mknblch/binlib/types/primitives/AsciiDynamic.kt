package de.mknblch.binlib.types.primitives

import kotlin.text.Charsets.US_ASCII

class AsciiDynamic(maxLength: Int = Int.MAX_VALUE): StringDynamic(maxLength, US_ASCII)