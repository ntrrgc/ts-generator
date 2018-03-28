package me.ntrrgc.tsGenerator

fun <T,R> Iterable<T>.unfold(f: (Iterable<T>) -> Pair<R?, Iterable<T>>): Iterable<R> {
    return if (iterator().hasNext()) {
        val (first, remaining) = f(this)
        if (first !== null) {
            listOf(first) + remaining.unfold(f)
        } else {
            remaining.unfold(f)
        }
    } else {
        emptyList()
    }
}