package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestArrayFields : Spek({

    it("handles ClassWithArray") {
        assertGeneratedCode(
            ClassWithArray::class,
            setOf(
                """
                interface ClassWithArray {
                    items: string[];
                }
                """,
            )
        )
    }

    it("handles ClassWithIntArray") {
        assertGeneratedCode(
            ClassWithIntArray::class,
            setOf(
                """
                interface ClassWithIntArray {
                    items: int[];
                }
                """,
            )
        )
    }

    it("handles ClassWithShortArray") {
        assertGeneratedCode(
            ClassWithShortArray::class,
            setOf(
                """
                interface ClassWithShortArray {
                    items: int[];
                }
                """,
            )
        )
    }

    it("handles ClassWithByteArray") {
        assertGeneratedCode(
            ClassWithByteArray::class,
            setOf(
                """
                interface ClassWithByteArray {
                    items: int[];
                }
                """,
            )
        )
    }

    it("handles ClassWithCharArray") {
        assertGeneratedCode(
            ClassWithCharArray::class,
            setOf(
                """
                interface ClassWithCharArray {
                    items: string[];
                }
                """,
            )
        )
    }

    it("handles ClassWithLongArray") {
        assertGeneratedCode(
            ClassWithLongArray::class,
            setOf(
                """
                interface ClassWithLongArray {
                    items: int[];
                }
                """,
            )
        )
    }

    it("handles ClassWithFloatArray") {
        assertGeneratedCode(
            ClassWithFloatArray::class,
            setOf(
                """
                interface ClassWithFloatArray {
                    items: number[];
                }
                """,
            )
        )
    }

    it("handles ClassWithDoubleArray") {
        assertGeneratedCode(
            ClassWithDoubleArray::class,
            setOf(
                """
                interface ClassWithDoubleArray {
                    items: number[];
                }
                """,
            )
        )
    }

}) {
    companion object {

        class ClassWithArray(val items: Array<String>)
        class ClassWithIntArray(val items: IntArray)
        class ClassWithShortArray(val items: ShortArray)
        class ClassWithByteArray(val items: ByteArray)
        class ClassWithCharArray(val items: CharArray)
        class ClassWithLongArray(val items: LongArray)
        class ClassWithFloatArray(val items: FloatArray)
        class ClassWithDoubleArray(val items: DoubleArray)

    }
}
