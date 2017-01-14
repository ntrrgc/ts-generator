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

object TypeScriptDefinitionFactory {
    fun fromCode(tsCode: String): TypeScriptDefinition {
        val code = tsCode.trim()

        if (code.startsWith("interface")) {
            return ClassDefinition(code)
        } else if (code.startsWith("type")) {
            return EnumDefinition(code)
        } else {
            throw RuntimeException("Unknown definition type: $code")
        }
    }
}