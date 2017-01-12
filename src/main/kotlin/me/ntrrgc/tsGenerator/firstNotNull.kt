package me.ntrrgc.tsGenerator

internal fun <T, R> Iterable<T>.firstNotNull(cb: (value: T) -> R?): R? {
    this.forEach { value ->
        val ret = cb(value)
        if (ret != null) {
            return ret
        }
    }

    // Reached end of collection with no matches
    return null
}