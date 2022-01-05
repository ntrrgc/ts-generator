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

class ClassDefinitionComparator(
    tsCode: String
) : Comparable<ClassDefinitionComparator>, TypeScriptDefinition {

    private val lines = tsCode.trim()
        .split("\n")
        .map(String::trim)

    private val members = lines
        .subList(1, this.lines.size - 1)
        .map { "    $it" }
        .toSet()

    override fun equals(other: Any?): Boolean {
        if (other !is ClassDefinitionComparator) {
            return false
        }

        if (this.lines.first() != other.lines.first()) {
            return false
        }

        if (this.lines.last() != other.lines.last()) {
            return false
        }

        return this.members == other.members
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ClassDefinitionComparator): Int {
        return if (this == other) {
            0
        } else if (this.lines.first() < other.lines.first()) {
            -1
        } else {
            1
        }
    }

    override fun toString(): String {
        return (listOf(lines.first()) + members.sorted() + lines.last())
            .joinToString("\n")
    }

    override fun hashCode(): Int {
        return this.lines.hashCode()
    }
}
