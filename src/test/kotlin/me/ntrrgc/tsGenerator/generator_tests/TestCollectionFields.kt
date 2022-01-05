package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestCollectionFields : Spek({


    it("handles ClassWithLists") {
        assertGeneratedCode(
            ClassWithLists::class,
            setOf(
                """
                interface ClassWithLists {
                    aList: string[];
                    anArrayList: string[];
                }
                """.trimIndent()
            )
        )
    }

    it("handles ClassWithArray") {
        assertGeneratedCode(
            ClassWithArray::class,
            setOf(
                """
                interface ClassWithArray {
                    items: string[];
                }
                """.trimIndent()
            )
        )
    }
}) {
    companion object {

        class ClassWithLists(
            val aList: List<String>,
            val anArrayList: ArrayList<String>
        )

        class ClassWithArray(
            val items: Array<String>
        )

    }
}
