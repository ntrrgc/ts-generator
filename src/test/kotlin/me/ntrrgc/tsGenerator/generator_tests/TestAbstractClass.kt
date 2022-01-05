package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestAbstractClass : Spek({

    describe("expect abstract classes are converted") {

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

    }
}) {
    companion object {

        abstract class AbstractClass(val concreteProperty: String) {
            abstract val abstractProperty: Int
            abstract fun abstractMethod()
        }

    }
}
