package me.ntrrgc.tsGenerator.generator_tests

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestTransformPropertyTypes : Spek({

    it("supports transforming types") {
        assertGeneratedCode(
            DataClass::class,
            setOf(
                """
                interface DataClass {
                    prop: int | null;
                }
                """.trimIndent(),
            ), classTransformers = listOf(
                object : ClassTransformer {
                    override fun transformPropertyType(
                        type: KType,
                        property: KProperty<*>,
                        klass: KClass<*>
                    ): KType {
                        return if (klass == DataClass::class && property.name == "prop") {
                            Int::class.createType(nullable = true)
                        } else {
                            type
                        }
                    }
                }
            ))
    }

}) {
    companion object {

        data class DataClass(val prop: String)
    }
}
