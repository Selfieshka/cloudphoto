package ru.itis.cloudphoto.util

import java.util.Arrays

object Utf8ByteComparator : Comparator<String> {

    override fun compare(a: String, b: String): Int =
        Arrays.compareUnsigned(a.toByteArray(Charsets.UTF_8), b.toByteArray(Charsets.UTF_8))
}
