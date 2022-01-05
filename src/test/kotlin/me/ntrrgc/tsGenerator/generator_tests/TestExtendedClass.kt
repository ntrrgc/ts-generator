package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestExtendedClass : Spek({


    it("handles DerivedClass") {
        assertGeneratedCode(
            DerivedClass::class,
            setOf(
                """
                interface DerivedClass extends BaseClass {
                    b: string[];
                }
                """,
                """
                interface BaseClass {
                    a: int;
                }
                """
            )
        )
    }


}) {
    companion object {

        open class BaseClass(val a: Int)
        class DerivedClass(val b: List<String>) : BaseClass(4)
    }
}
