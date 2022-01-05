package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestGenericClass : Spek({

    describe("test generic classes") {

        it("handles GenericClass") {
            assertGeneratedCode(
                GenericClass::class,
                setOf(
                    """
                    interface GenericClass<A, B, C extends any[]> {
                        a: A;
                        b: (B | null)[];
                        c: C;
                    }
                    """
                )
            )
        }

        it("handles GenericDerivedClass") {
            assertGeneratedCode(
                GenericDerivedClass::class,
                setOf(
                    """
                    interface GenericClass<A, B, C extends any[]> {
                        a: A;
                        b: (B | null)[];
                        c: C;
                    }
                    """,
                    """
                    interface Empty {
                    }
                    """,
                    """
                    interface GenericDerivedClass<B> extends GenericClass<Empty, B, string[]> {
                    }
                    """
                )
            )
        }
    }

}) {
    companion object {
        class Empty

        open class GenericClass<A, out B, out C : List<Any>>(
            val a: A,
            val b: List<B?>,
            val c: C,
            private val privateMember: A
        )

        class GenericDerivedClass<B>(a: Empty, b: List<B?>, c: ArrayList<String>) :
            GenericClass<Empty, B, ArrayList<String>>(a, b, c, a)

    }
}
