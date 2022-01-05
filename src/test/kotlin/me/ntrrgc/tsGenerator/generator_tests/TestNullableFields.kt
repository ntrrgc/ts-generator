package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.VoidType
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestNullableFields : Spek({

    describe("test nullable fields are mapped") {

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
                ),
                voidType = VoidType.UNDEFINED
            )
        }

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
