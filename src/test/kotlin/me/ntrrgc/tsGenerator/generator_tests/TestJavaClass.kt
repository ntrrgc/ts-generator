package me.ntrrgc.tsGenerator.generator_tests

import java.beans.Introspector
import java.util.Optional
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.kotlinFunction
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.tests.JavaClass
import me.ntrrgc.tsGenerator.tests.JavaClassWithNonnullAsDefault
import me.ntrrgc.tsGenerator.tests.JavaClassWithNullables
import me.ntrrgc.tsGenerator.tests.JavaClassWithOptional
import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestJavaClass : Spek({

    it("handles JavaClass") {
        assertGeneratedCode(
            JavaClass::class,
            setOf(
                """
                interface JavaClass {
                    name: string;
                    results: int[];
                    multidimensional: string[][];
                    finished: boolean;
                }
                """
            )
        )
    }

    it("handles JavaClassWithNullables") {
        assertGeneratedCode(
            JavaClassWithNullables::class,
            setOf(
                """
                interface JavaClassWithNullables {
                    name: string;
                    results: int[];
                    nextResults: int[] | null;
                }
                """
            )
        )
    }

    it("handles JavaClassWithNonnullAsDefault") {
        assertGeneratedCode(
            JavaClassWithNonnullAsDefault::class,
            setOf(
                """
                interface JavaClassWithNonnullAsDefault {
                    name: string;
                    results: int[];
                    nextResults: int[] | null;
                }
                """
            )
        )
    }

    it("handles JavaClassWithOptional") {
        assertGeneratedCode(
            JavaClassWithOptional::class,
            setOf(
                """
                interface JavaClassWithOptional {
                    name: string;
                    surname: string | null;
                }
                """
            ), classTransformers = listOf(
                object : ClassTransformer {
                    override fun transformPropertyType(
                        type: KType,
                        property: KProperty<*>,
                        klass: KClass<*>
                    ): KType {
                        val bean = Introspector.getBeanInfo(klass.java)
                            .propertyDescriptors
                            .find { it.name == property.name }

                        val getterReturnType = bean?.readMethod?.kotlinFunction?.returnType
                        return if (getterReturnType?.classifier == Optional::class) {
                            val wrappedType = getterReturnType.arguments.first().type!!
                            wrappedType.withNullability(true)
                        } else {
                            type
                        }
                    }
                }
            ))
    }


})
