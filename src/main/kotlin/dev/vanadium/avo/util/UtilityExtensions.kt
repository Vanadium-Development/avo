package dev.vanadium.avo.util

fun <T> List<T>.findFirstDuplicate(): T? {
    val values = mutableListOf<T>()
    forEach {
        if (values.contains(it))
            return it
        values += it
    }
    return null
}

infix fun <T> T?.ifPresent(fn: (it: T) -> Unit) {
    if (this == null)
        return

    fn(this)
}
