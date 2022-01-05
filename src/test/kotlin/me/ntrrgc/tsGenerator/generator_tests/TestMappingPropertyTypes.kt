package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestMappingPropertyTypes : Spek({

    describe("test mapping property types") {

        it("supports type mapping for basic types") {
            assertGeneratedCode(
                Widget::class,
                setOf(
                    """
                interface Widget {
                    name: CustomString;
                    value: int;
                }
                """
                ), mappings = mapOf(String::class to "CustomString")
            )
        }

        it("supports type mapping for classes") {
            assertGeneratedCode(
                ClassWithDependencies::class,
                setOf(
                    """
                    interface ClassWithDependencies {
                        widget: CustomWidget;
                    }
                """,
                ),
                mappings = mapOf(Widget::class to "CustomWidget")
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

    }
}
