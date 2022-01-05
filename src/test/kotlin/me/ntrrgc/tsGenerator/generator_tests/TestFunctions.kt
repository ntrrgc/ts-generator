package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestFunctions : Spek({

    describe("test class functions are ignored") {

        it("expect methods on classes are ignored") {
            assertGeneratedCode(
                ClassWithMethods::class,
                setOf(
                    """
                    interface ClassWithMethods {
                    }
                    """
                )
            )
        }

        it("expect methods on interfaces are ignored") {
            assertGeneratedCode(
                InterfaceWithMethods::class,
                setOf(
                    """
                    interface InterfaceWithMethods {
                    }
                    """
                )
            )
        }

    }

}) {
    companion object {

        class ClassWithMethods(val propertyMethod: () -> Int) {
            fun regularMethod() = 4
        }

        interface InterfaceWithMethods {
            val propertyMethod: () -> Int
            fun regularMethod() = 4
        }

    }
}
