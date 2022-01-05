package me.ntrrgc.tsGenerator.generator_tests

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.onlyOnSubclassesOf
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestTransformPropertyNames : Spek({

    it("supports transforming property names") {
        assertGeneratedCode(
            DataClass::class, setOf(
                """
            interface DataClass {
                PROP: string;
            }
            """.trimIndent(),
            ), classTransformers = listOf(
                uppercasePropertyNameTransformer
            )
        )
    }

    it("supports transforming only some classes") {
        assertGeneratedCode(
            ClassWithDependencies::class, setOf(
                """
                interface ClassWithDependencies {
                    widget: Widget;
                }
                """,
                """
                interface Widget {
                    NAME: string;
                    VALUE: int;
                }
                """,
            ), classTransformers = listOf(
                uppercasePropertyNameTransformer.onlyOnSubclassesOf(Widget::class)
            )
        )
    }

    it("supports filtering subclasses") {
        assertGeneratedCode(
            DerivedClass::class, setOf(
                """
                interface DerivedClass extends BaseClass {
                    B: string[];
                }
                """,
                """
                interface BaseClass {
                    A: int;
                }
                """,
            ), classTransformers = listOf(
                uppercasePropertyNameTransformer.onlyOnSubclassesOf(BaseClass::class)
            )
        )
    }

    it("uses all transformers in pipeline") {
        assertGeneratedCode(SimpleTypes::class, setOf(
            """
                interface SimpleTypes {
                    aString12: string;
                    aDouble12: number;
                    anInt12: int;
                }
                """
        ), classTransformers = listOf(object : ClassTransformer {
            override fun transformPropertyName(
                propertyName: String, property: KProperty<*>, klass: KClass<*>
            ): String {
                return propertyName + "1"
            }
        }, object : ClassTransformer {}, object : ClassTransformer {
            override fun transformPropertyName(
                propertyName: String, property: KProperty<*>, klass: KClass<*>
            ): String {
                return propertyName + "2"
            }
        }))
    }

}) {
    companion object {

        val uppercasePropertyNameTransformer = object : ClassTransformer {
            /**
             * Returns the property name that will be included in the
             * definition.
             *
             * If it returns null, the value of the next class transformer
             * in the pipeline is used.
             */
            override fun transformPropertyName(
                propertyName: String, property: KProperty<*>, klass: KClass<*>
            ): String {
                return propertyName.uppercase()
            }
        }

        open class BaseClass(val a: Int)
        class DerivedClass(val b: List<String>) : BaseClass(4)

        class Widget(
            val name: String, val value: Int
        )

        class ClassWithDependencies(
            val widget: Widget
        )

        data class DataClass(val prop: String)
        class SimpleTypes(
            val aString: String,
            var anInt: Int,
            val aDouble: Double,
            private val privateMember: String
        )

    }
}
