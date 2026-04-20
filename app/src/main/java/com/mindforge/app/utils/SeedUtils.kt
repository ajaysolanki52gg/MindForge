package com.mindforge.app.utils

object SeedUtils {
    fun seedFor(vararg values: String): Long =
        values.joinToString("|").hashCode().toLong()
}
