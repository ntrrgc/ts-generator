package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestPrimitiveFields : Spek({

    it("handles classes with a single member") {
        assertGeneratedCode(
            ClassWithMember::class,
            setOf(
                """
interface ClassWithMember {
    a: string;
}
"""
            )
        )
    }

    it("handles SimpleTypes") {
        assertGeneratedCode(
            SimpleTypes::class,
            setOf(
                """
    interface SimpleTypes {
        aString: string;
        anInt: int;
        aDouble: number;
    }
    """
            )
        )
    }

    it("handles ClassWithAny") {
        // Note: in TypeScript any includes null and undefined.
        assertGeneratedCode(
            ClassWithAny::class,
            setOf(
                """
    interface ClassWithAny {
        required: any;
        optional: any;
    }
    """
            )
        )
    }

    it("handles DataClass") {
        assertGeneratedCode(
            DataClass::class,
            setOf(
                """
    interface DataClass {
        prop: string;
    }
    """
            )
        )
    }

}) {
    companion object {

        class ClassWithMember(val a: String)
        class SimpleTypes(
            val aString: String,
            var anInt: Int,
            val aDouble: Double,
            private val privateMember: String
        )


        data class DataClass(val prop: String)
        class ClassWithAny(val required: Any, val optional: Any?)

    }
}
