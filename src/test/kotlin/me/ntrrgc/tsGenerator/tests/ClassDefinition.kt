package me.ntrrgc.tsGenerator.tests

class ClassDefinition(tsCode: String): Comparable<ClassDefinition>, TypeScriptDefinition {
    val lines = tsCode.trim()
        .split("\n")
        .map(String::trim)

    val members = lines
        .subList(1, this.lines.size - 1)
        .map { "    " + it }
        .toSet()

    override fun equals(other: Any?): Boolean {
        if (other !is ClassDefinition) {
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
    override fun compareTo(other: ClassDefinition): Int {
        if (this == other) {
            return 0
        } else if (this.lines.first() < other.lines.first()) {
            return -1
        } else {
            return 1
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