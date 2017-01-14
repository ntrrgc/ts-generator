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

internal class TypeScriptType private constructor(val types: List<String>) {
    companion object {
        fun single(type: String, nullable: Boolean): TypeScriptType {
            return TypeScriptType(listOf(type)).let {
                if (nullable) {
                    it or TypeScriptType(listOf("null"))
                } else {
                    it
                }
            }
        }

        fun union(types: List<String>): TypeScriptType {
            return TypeScriptType(types)
        }
    }

    infix fun or(other: TypeScriptType): TypeScriptType {
        val combinedTypes = (this.types + other.types).distinct()

        return TypeScriptType(if ("any" in combinedTypes) {
            listOf("any")
        } else {
            combinedTypes
        })
    }

    fun formatWithParenthesis(): String {
        if (types.size == 1) {
            return types.single()
        } else {
            return "(" + this.formatWithoutParenthesis() + ")"
        }
    }

    fun formatWithoutParenthesis(): String {
        return types.joinToString(" | ")
    }
}