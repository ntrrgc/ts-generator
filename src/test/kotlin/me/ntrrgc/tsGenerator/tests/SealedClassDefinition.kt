package me.ntrrgc.tsGenerator.tests

import java.util.SortedSet

data class SealedClassDefinition(
        val name: String,
        val classes: SortedSet<ClassDefinition>,
        val union: SortedSet<String>
): TypeScriptDefinition, Comparable<SealedClassDefinition> {

    private val eqHashList = listOf(classes, name, union)

    override fun equals(other: Any?): Boolean =
            if (other !is SealedClassDefinition) false
            else {
                eqHashList == other.eqHashList
            }

    override fun hashCode(): Int = eqHashList.hashCode()

    override fun compareTo(other: SealedClassDefinition): Int {
        classes.size.compareTo(other.classes.size).let {
            if (it != 0) return@compareTo it
        }

        classes.zip(other.classes).asSequence()
                .map { (c1, c2) -> c1.compareTo(c2) }
                .firstOrNull { it != 0 }
                .let {
                    if (it != null) return@compareTo it
                }

        name.compareTo(other.name)
                .let {
                    if (it != 0) return@compareTo it
                }

        union.size.compareTo(other.union.size).let {
            if (it != 0) return@compareTo it
        }

        union.zip(other.union).asSequence()
                .map { (c1, c2) -> c1.compareTo(c2) }
                .firstOrNull { it != 0 }
                .let {
                    if (it != null) return@compareTo it
                }

        return 0
    }

}