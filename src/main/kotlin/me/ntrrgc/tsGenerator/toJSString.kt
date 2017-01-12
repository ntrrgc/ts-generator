package me.ntrrgc.tsGenerator

internal fun String.toJSString(): String {
    return "\"${this
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\t", "\\t")
        .replace("\"", "\\\"")
    }\""
}