package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestEmptyClass : Spek({

    val expectedGeneratedEmpty =
        """
        interface Empty {
        }
        """.trimIndent()

    it("handles empty class") {
        assertGeneratedCode(
            Empty::class,
            setOf(
                expectedGeneratedEmpty
            )
        )
    }

    it("handles class with empty class field") {
        assertGeneratedCode(
            ClassWithEmptyField::class,
            setOf(
                expectedGeneratedEmpty,
                """
                interface ClassWithEmptyField {
                    e: Empty;
                }
                """.trimIndent(),
            )
        )
    }

    it("handles data class with empty class field") {
        assertGeneratedCode(
            InterfaceWithEmptyField::class,
            setOf(
                expectedGeneratedEmpty,
                """
                interface InterfaceWithEmptyField {
                    e: Empty;
                }
                """.trimIndent(),
            )
        )
    }

    it("handles data class with empty class field") {
        assertGeneratedCode(
            DataClassWithEmptyField::class,
            setOf(
                expectedGeneratedEmpty,
                """
                interface DataClassWithEmptyField {
                    e: Empty;
                }
                """.trimIndent(),
            )
        )
    }

}) {
    companion object {

        class Empty
        class ClassWithEmptyField(val e: Empty)
        data class DataClassWithEmptyField(val e: Empty)
        interface InterfaceWithEmptyField {
            val e: Empty
        }

    }
}
