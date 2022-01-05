package me.ntrrgc.tsGenerator.generator_tests

import java.time.Instant
import me.ntrrgc.tsGenerator.VoidType
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestClassDependencies : Spek({

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
                """.trimIndent(),
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
                """.trimIndent(),
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
                """.trimIndent(),
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
                """.trimIndent(),
            ),
            mappings = mapOf(Instant::class to "string"), voidType = VoidType.UNDEFINED
        )
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
