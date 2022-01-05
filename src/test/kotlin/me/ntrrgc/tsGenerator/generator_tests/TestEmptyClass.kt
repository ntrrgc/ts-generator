package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestEmptyClass : Spek({
    describe("expect classes with no fields are converted") {
        val expectedGeneratedEmpty =
            """
            interface Empty {
            }
            """

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
                    """,
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
                    """,
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
                    """,
                )
            )
        }
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
