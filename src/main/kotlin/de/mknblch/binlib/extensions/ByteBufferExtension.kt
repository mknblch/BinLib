package de.mknblch.binlib.extensions

import java.nio.ByteBuffer

/**
 * @return true if at least size bytes are available, false otherwise
 */
fun ByteBuffer.hasRemaining(size: Int): Boolean = this.remaining() >= size