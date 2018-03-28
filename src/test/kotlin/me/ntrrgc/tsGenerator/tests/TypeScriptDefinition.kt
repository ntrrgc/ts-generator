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

package me.ntrrgc.tsGenerator.tests

import me.ntrrgc.tsGenerator.unfold

interface TypeScriptDefinition {

    companion object {
        operator fun invoke(tsCode: String): TypeScriptDefinition {
            val typedefs = tsCode.lines().unfold { lines ->
                fun Iterable<String>.indexOfTypeDeclaration(): Int = indexOfFirst { it.trim().startsWith("interface") || it.trim().startsWith("type") }
                        .takeIf { it >= 0 } ?: count()

                val restOfLines = lines.drop(lines.indexOfTypeDeclaration())
                val classDefLineCount = 1 + restOfLines.drop(1).indexOfTypeDeclaration()
                Pair(
                        restOfLines.take(classDefLineCount).filter { it.isNotBlank() },
                        restOfLines.drop(classDefLineCount))
            }
            val classes = typedefs.mapNotNull { code ->
                if (code.firstOrNull()?.trim()?.startsWith("interface") == true) {
                    ClassDefinition(code.joinToString("\n"))
                } else {
                    null
                }
            }.toSortedSet()

            val unionDefs = typedefs
                    .filter { code ->
                        code.firstOrNull()?.trim()?.startsWith("type") == true
                    }
                    .map { code -> code.joinToString("\n").trim() }
                    .map { unionDef ->
                        val (_, name, _, def) = unionDef.split(Regex("\\s+"), limit = 4)
                        val union = def.trimStart('=', ' ', '\n', '(')
                                .trimEnd(' ', '\n', ')', ';')
                                .split(Regex("\\s*\\|\\s*"))
                                .toSortedSet()
                        name to union
                    }

            return if (classes.size == 1 && unionDefs.isEmpty()) {
                classes.single()
            } else if (classes.isEmpty() && unionDefs.size == 1 && unionDefs.single().second.all { it.startsWith('"') && it.endsWith('"') }) {
                EnumDefinition(unionDefs.single().first, unionDefs.single().second)
            } else if (unionDefs.size == 1 && unionDefs.single().second.none { it.startsWith('"') && it.endsWith('"') }) {
                SealedClassDefinition(unionDefs.single().first, classes, unionDefs.single().second)
            } else {
                throw RuntimeException("Unknown definition type: ${tsCode.trim()}")
            }
        }
    }

}
