package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestAbstractClass : Spek({

    it("handles AbstractClass") {
        assertGeneratedCode(
            AbstractClass::class,
            setOf(
                """
                interface AbstractClass {
                    concreteProperty: string;
                    abstractProperty: int;
                }
                """
            )
        )
    }

}) {
    companion object {

        abstract class AbstractClass(val concreteProperty: String) {
            abstract val abstractProperty: Int
            abstract fun abstractMethod()
        }

    }
}
