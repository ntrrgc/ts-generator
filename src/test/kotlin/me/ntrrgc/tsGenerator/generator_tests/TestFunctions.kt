package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestFunctions : Spek({

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
