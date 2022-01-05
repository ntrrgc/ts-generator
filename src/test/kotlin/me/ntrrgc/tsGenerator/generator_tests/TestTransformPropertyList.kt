package me.ntrrgc.tsGenerator.generator_tests

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestTransformPropertyList : Spek({


    it("supports filtering properties") {
        assertGeneratedCode(
            SimpleTypes::class,
            setOf(
                """
                interface SimpleTypes {
                    aString: string;
                    aDouble: number;
                }
                """
            ), classTransformers = listOf(
                object : ClassTransformer {
                    override fun transformPropertyList(
                        properties: List<KProperty<*>>,
                        klass: KClass<*>
                    ): List<KProperty<*>> {
                        return properties.filter { it.name != "anInt" }
                    }
                }
            )
        )
    }

}) {
    companion object {

        class SimpleTypes(
            val aString: String,
            var anInt: Int,
            val aDouble: Double,
            private val privateMember: String
        )

        abstract class AbstractClass(val concreteProperty: String) {
            abstract val abstractProperty: Int
            abstract fun abstractMethod()
        }

    }
}
