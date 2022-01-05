package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestExtendedClass : Spek({

    describe("test inheritance") {

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

    }

}) {
    companion object {

        open class BaseClass(val a: Int)
        class DerivedClass(val b: List<String>) : BaseClass(4)
    }
}
