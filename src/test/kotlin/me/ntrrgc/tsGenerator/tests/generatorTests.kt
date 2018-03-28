/*
 * Copyright 2017 Alicia Boya García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ntrrgc.tsGenerator.tests

import com.winterbe.expekt.should
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import me.ntrrgc.tsGenerator.VoidType
import me.ntrrgc.tsGenerator.onlyOnSubclassesOf
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.beans.Introspector
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.kotlinFunction

inline fun <reified T: Any> assertGeneratedCode(
                        expectedOutput: Set<String>,
                        mappings: Map<KClass<*>, String> = mapOf(),
                        classTransformers: List<ClassTransformer> = listOf(),
                        ignoreSuperclasses: Set<KClass<*>> = setOf(),
                        voidType: VoidType = VoidType.NULL)
{
    val generator = TypeScriptGenerator(listOf(T::class), mappings, classTransformers,
        ignoreSuperclasses, intTypeName = "int", voidType = voidType)

    val expected = expectedOutput
        .map(TypeScriptDefinition.Companion::invoke)
        .toSet()
    val actual = generator.individualDefinitions
        .map(TypeScriptDefinition.Companion::invoke)
        .toSet()

    actual.should.equal(expected)
}

class Empty
class ClassWithMember(val a: String)
class SimpleTypes(
    val aString: String,
    var anInt: Int,
    val aDouble: Double,
    private val privateMember: String
)
class ClassWithLists(
    val aList: List<String>,
    val anArrayList: ArrayList<String>
)
class ClassWithArray(
    val items: Array<String>
)
class Widget(
    val name: String,
    val value: Int
)
class ClassWithDependencies(
    val widget: Widget
)
class ClassWithNullables(
    val widget: Widget?
)
class ClassWithComplexNullables(
    val maybeWidgets: List<String?>?,
    val maybeWidgetsArray: Array<String?>?
)
class ClassWithNullableList(
    val strings: List<String>?
)
class GenericClass<A, out B, out C: List<Any>>(
    val a: A,
    val b: List<B?>,
    val c: C,
    private val privateMember: A
)
open class BaseClass(val a: Int)
class DerivedClass(val b: List<String>): BaseClass(4)
class ClassWithMethods(val propertyMethod: () -> Int) {
    fun regularMethod() = 4
}
abstract class AbstractClass(val concreteProperty: String) {
    abstract val abstractProperty: Int
    abstract fun abstractMethod()
}
enum class Direction {
    North,
    West,
    South,
    East
}
class ClassWithEnum(val direction: Direction)
data class DataClass(val prop: String)
class ClassWithAny(val required: Any, val optional: Any?)

sealed class SealedClassOutside
data class SealedClassOutside1(val value: String): SealedClassOutside()
data class SealedClassOutside2(val otherValue: Int): SealedClassOutside()

sealed class SealedClass {
    data class In1(val value: String): SealedClass()
    data class In2(val otherValue: Int): SealedClass()
}

class Tests: Spek({
    it("handles empty class") {
        assertGeneratedCode<Empty>(setOf("""
interface Empty {
}
"""))
    }

    it("handles classes with a single member") {
        assertGeneratedCode<ClassWithMember>(setOf("""
interface ClassWithMember {
    a: string;
}
"""))
    }

    it("handles SimpleTypes") {
        assertGeneratedCode<SimpleTypes>(setOf("""
    interface SimpleTypes {
        aString: string;
        anInt: int;
        aDouble: number;
    }
    """))
    }

    it("handles ClassWithLists") {
        assertGeneratedCode<ClassWithLists>(setOf("""
    interface ClassWithLists {
        aList: string[];
        anArrayList: string[];
    }
    """))
    }

    it("handles ClassWithArray") {
        assertGeneratedCode<ClassWithArray>(setOf("""
    interface ClassWithArray {
        items: string[];
    }
    """))
    }

    val widget = """
    interface Widget {
        name: string;
        value: int;
    }
    """

    it("handles ClassWithDependencies") {
        assertGeneratedCode<ClassWithDependencies>(setOf("""
    interface ClassWithDependencies {
        widget: Widget;
    }
    """, widget))
    }

    it("handles ClassWithNullables") {
        assertGeneratedCode<ClassWithNullables>(setOf("""
    interface ClassWithNullables {
        widget: Widget | null;
    }
    """, widget))
    }

    it("handles ClassWithComplexNullables") {
        assertGeneratedCode<ClassWithComplexNullables>(setOf("""
    interface ClassWithComplexNullables {
        maybeWidgets: (string | null)[] | null;
        maybeWidgetsArray: (string | null)[] | null;
    }
    """))
    }

    it("handles ClassWithNullableList") {
        assertGeneratedCode<ClassWithNullableList>(setOf("""
    interface ClassWithNullableList {
        strings: string[] | null;
    }
    """))
    }

    it("handles GenericClass") {
        assertGeneratedCode<GenericClass<*,*,*>>(setOf("""
    interface GenericClass<A, B, C extends any[]> {
        a: A;
        b: (B | null)[];
        c: C;
    }
    """))
    }

    it("handles DerivedClass") {
        assertGeneratedCode<DerivedClass>(setOf("""
    interface DerivedClass extends BaseClass {
        b: string[];
    }
    """, """
    interface BaseClass {
        a: int;
    }
    """))
    }

    it("handles ClassWithMethods") {
        assertGeneratedCode<ClassWithMethods>(setOf("""
    interface ClassWithMethods {
    }
    """))
    }

    it("handles AbstractClass") {
        assertGeneratedCode<AbstractClass>(setOf("""
    interface AbstractClass {
        concreteProperty: string;
        abstractProperty: int;
    }
    """))
    }

    it("handles ClassWithEnum") {
        assertGeneratedCode<ClassWithEnum>(setOf("""
    interface ClassWithEnum {
        direction: Direction;
    }
    """, """type Direction = "North" | "West" | "South" | "East";"""))
    }

    it("handles DataClass") {
        assertGeneratedCode<DataClass>(setOf("""
    interface DataClass {
        prop: string;
    }
    """))
    }

    it("handles ClassWithAny") {
        // Note: in TypeScript any includes null and undefined.
        assertGeneratedCode<ClassWithAny>(setOf("""
    interface ClassWithAny {
        required: any;
        optional: any;
    }
    """))
    }

    it("supports type mapping for classes") {
        assertGeneratedCode<ClassWithDependencies>(setOf("""
interface ClassWithDependencies {
    widget: CustomWidget;
}
"""), mappings = mapOf(Widget::class to "CustomWidget"))
    }

    it("supports type mapping for basic types") {
        assertGeneratedCode<DataClass>(setOf("""
    interface DataClass {
        prop: CustomString;
    }
    """), mappings = mapOf(String::class to "CustomString"))
    }

    it("supports transforming property names") {
        assertGeneratedCode<DataClass>(setOf("""
    interface DataClass {
        PROP: string;
    }
    """), classTransformers = listOf(
            object: ClassTransformer {
                /**
                 * Returns the property name that will be included in the
                 * definition.
                 *
                 * If it returns null, the value of the next class transformer
                 * in the pipeline is used.
                 */
                override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
                    return propertyName.toUpperCase()
                }
            }
        ))
    }

    it("supports transforming only some classes") {
        assertGeneratedCode<ClassWithDependencies>(setOf("""
interface ClassWithDependencies {
    widget: Widget;
}
""", """
interface Widget {
    NAME: string;
    VALUE: int;
}
"""), classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
                    return propertyName.toUpperCase()
                }
            }.onlyOnSubclassesOf(Widget::class)
        ))
    }

    it("supports transforming types") {
        assertGeneratedCode<DataClass>(setOf("""
    interface DataClass {
        prop: int | null;
    }
    """), classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyType(type: KType, property: KProperty<*>, klass: KClass<*>): KType {
                    if (klass == DataClass::class && property.name == "prop") {
                        return Int::class.createType(nullable = true)
                    } else {
                        return type
                    }
                }
            }
        ))
    }

    it("supports filtering properties") {
        assertGeneratedCode<SimpleTypes>(setOf("""
    interface SimpleTypes {
        aString: string;
        aDouble: number;
    }
    """), classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>> {
                    return properties.filter { it.name != "anInt" }
                }
            }
        ))
    }

    it("supports filtering subclasses") {
        assertGeneratedCode<DerivedClass>(setOf("""
    interface DerivedClass extends BaseClass {
        B: string[];
    }
    """, """
    interface BaseClass {
        A: int;
    }
    """), classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
                    return propertyName.toUpperCase()
                }
            }.onlyOnSubclassesOf(BaseClass::class)
        ))
    }

    it("uses all transformers in pipeline") {
        assertGeneratedCode<SimpleTypes>(setOf("""
    interface SimpleTypes {
        aString12: string;
        aDouble12: number;
        anInt12: int;
    }
    """), classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
                    return propertyName + "1"
                }
            },
            object : ClassTransformer {
            },
            object : ClassTransformer {
                override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
                    return propertyName + "2"
                }
            }
        ))
    }

    it("handles JavaClass") {
        assertGeneratedCode<JavaClass>(setOf("""
    interface JavaClass {
        name: string;
        results: int[];
        multidimensional: string[][];
        finished: boolean;
    }
    """))
    }

    it("handles JavaClassWithNullables") {
        assertGeneratedCode<JavaClassWithNullables>(setOf("""
    interface JavaClassWithNullables {
        name: string;
        results: int[];
        nextResults: int[] | null;
    }
    """))
    }

    it("handles JavaClassWithNonnullAsDefault") {
        assertGeneratedCode<JavaClassWithNonnullAsDefault>(setOf("""
    interface JavaClassWithNonnullAsDefault {
        name: string;
        results: int[];
        nextResults: int[] | null;
    }
    """))
    }

    it("handles JavaClassWithOptional") {
        assertGeneratedCode<JavaClassWithOptional>(setOf("""
    interface JavaClassWithOptional {
        name: string;
        surname: string | null;
    }
    """), classTransformers = listOf(
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
                    if (getterReturnType?.classifier == Optional::class) {
                        val wrappedType = getterReturnType.arguments.first().type!!
                        return wrappedType.withNullability(true)
                    } else {
                        return type
                    }
                }
            }
        ))
    }

    it("handles ClassWithComplexNullables when serializing as undefined") {
        assertGeneratedCode<ClassWithComplexNullables>(setOf("""
    interface ClassWithComplexNullables {
        maybeWidgets: (string | undefined)[] | undefined;
        maybeWidgetsArray: (string | undefined)[] | undefined;
    }
    """), voidType = VoidType.UNDEFINED)
    }


    it("handles SealedClassOutside") {
        assertGeneratedCode<SealedClassOutside>(setOf("""
    interface SealedClassOutside1 {
        value: string;
    }

    interface SealedClassOutside2 {
        otherValue: int;
    }

    type SealedClassOutside = (SealedClassOutside1 | SealedClassOutside2);
    """), voidType = VoidType.UNDEFINED)
    }

    it("handles SealedClass") {
        assertGeneratedCode<SealedClass>(setOf("""
    interface In1 {
        value: string;
    }

    interface In2 {
        otherValue: int;
    }

    type SealedClass = (In1 | In2);
    """), voidType = VoidType.UNDEFINED)
    }

})