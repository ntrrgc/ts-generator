package me.ntrrgc.tsGenerator.generator_tests

import java.time.Instant
import me.ntrrgc.tsGenerator.VoidType
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestClassDependencies : Spek({
    describe("test classes dependent on other classes are all mapped") {
        val widget = """
            interface Widget {
                name: string;
                value: int;
            }
            """

        it("handles ClassWithDependencies") {
            assertGeneratedCode(
                ClassWithDependencies::class,
                setOf(
                    """
                    interface ClassWithDependencies {
                        widget: Widget;
                    }
                    """,
                    widget
                )
            )
        }

        it("handles ClassWithNullables") {
            assertGeneratedCode(
                ClassWithNullables::class,
                setOf(
                    """
                    interface ClassWithNullables {
                        widget: Widget | null;
                    }
                    """,
                    widget
                )
            )
        }

        it("handles ClassWithMixedNullables using mapping") {
            assertGeneratedCode(
                ClassWithMixedNullables::class,
                setOf(
                    """
                    interface ClassWithMixedNullables {
                        count: int;
                        time: string | null;
                    }
                    """,
                ),
                mappings = mapOf(Instant::class to "string")
            )
        }

        it("handles ClassWithMixedNullables using mapping and VoidTypes") {
            assertGeneratedCode(
                ClassWithMixedNullables::class,
                setOf(
                    """
                    interface ClassWithMixedNullables {
                        count: int;
                        time: string | undefined;
                    }
                    """,
                ),
                mappings = mapOf(Instant::class to "string"), voidType = VoidType.UNDEFINED
            )
        }

    }

}) {
    companion object {


        class Widget(
            val name: String,
            val value: Int
        )

        class ClassWithDependencies(
            val widget: Widget
        )

        class ClassWithMixedNullables(
            val count: Int,
            val time: Instant?
        )

        class ClassWithNullables(
            val widget: Widget?
        )

    }
}
