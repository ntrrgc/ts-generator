package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.VoidType
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestNullableFields : Spek({

    it("handles ClassWithNullableList") {
        assertGeneratedCode(
            ClassWithNullableList::class,
            setOf(
                """
                interface ClassWithNullableList {
                    strings: string[] | null;
                }
                """
            )
        )
    }

    it("handles ClassWithComplexNullables") {
        assertGeneratedCode(
            ClassWithComplexNullables::class,
            setOf(
                """
                interface ClassWithComplexNullables {
                    maybeWidgets: (string | null)[] | null;
                    maybeWidgetsArray: (string | null)[] | null;
                }
                """
            )
        )
    }

    it("handles ClassWithComplexNullables when serializing as undefined") {
        assertGeneratedCode(
            ClassWithComplexNullables::class,
            setOf(
                """
                interface ClassWithComplexNullables {
                    maybeWidgets: (string | undefined)[] | undefined;
                    maybeWidgetsArray: (string | undefined)[] | undefined;
                }
                """
            ), voidType = VoidType.UNDEFINED
        )
    }
}) {
    companion object {

        class ClassWithNullableList(
            val strings: List<String>?
        )

        class ClassWithComplexNullables(
            val maybeWidgets: List<String?>?,
            val maybeWidgetsArray: Array<String?>?
        )

    }
}
