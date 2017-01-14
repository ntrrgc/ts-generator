package me.ntrrgc.tsGenerator

fun parseSnakeCase(name: String) = name.split("_")

fun parseCamelCase(name: String): List<String> {
    return camelCaseToSnakeCase(name)
        .let(::parseSnakeCase)
}

fun camelCaseToSnakeCase(name: String): String {
    // Adapted from http://stackoverflow.com/a/1176023/1777162
    return name
        .replace(Regex("(.)([A-Z][a-z]+)"), { "${it.groupValues[1]}_${it.groupValues[2]}" })
        .replace(Regex("([a-z0-9])([A-Z])"), { "${it.groupValues[1]}_${it.groupValues[2]}"})
        .toLowerCase()
}

fun snakeCaseToCamelCase(name: String): String {
    return parseSnakeCase(name)
        .mapIndexed { i, s ->
            if (i == 0) {
                s
            } else {
                s[0].toUpperCase() + s.substring(1)
            }
        }
        .joinToString("")
}