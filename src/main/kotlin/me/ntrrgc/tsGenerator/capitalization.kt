/*
 * Copyright 2017 Alicia Boya García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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