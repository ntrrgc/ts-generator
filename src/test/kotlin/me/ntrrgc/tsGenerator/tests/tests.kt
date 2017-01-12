package me.ntrrgc.tsGenerator.tests

import com.winterbe.expekt.should
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun assertGeneratedCode(klass: KClass<*>,
                        expectedOutput: Set<String>,
                        mappings: Map<KClass<*>, String> = mapOf(),
                        classTransformers: Map<KClass<*>, ClassTransformer> = mapOf(),
                        defaultTransformer: ClassTransformer? = null)
{
    val generator = TypeScriptGenerator(listOf(klass), mappings, classTransformers, defaultTransformer)

    val expected = expectedOutput
        .map(TypeScriptDefinitionFactory::fromCode)
        .toSet()
    val actual = generator.individualDefinitions
        .map(TypeScriptDefinitionFactory::fromCode)
        .toSet()

    expected.should.equal(actual)
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

class Tests: Spek({
    it("handles empty class") {
        assertGeneratedCode(Empty::class, setOf("""
interface Empty {
}
"""))
    }

    it("handles classes with a single member") {
        assertGeneratedCode(ClassWithMember::class, setOf("""
interface ClassWithMember {
    a: string;
}
"""))
    }

    it("handles SimpleTypes") {
        assertGeneratedCode(SimpleTypes::class, setOf("""
    interface SimpleTypes {
        aString: string;
        anInt: int;
        aDouble: number;
    }
    """))
    }

    it("handles ClassWithLists") {
        assertGeneratedCode(ClassWithLists::class, setOf("""
    interface ClassWithLists {
        aList: string[];
        anArrayList: string[];
    }
    """))
    }

    it("handles ClassWithArray") {
        assertGeneratedCode(ClassWithArray::class, setOf("""
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
        assertGeneratedCode(ClassWithDependencies::class, setOf("""
    interface ClassWithDependencies {
        widget: Widget;
    }
    """, widget))
    }

    it("handles ClassWithNullables") {
        assertGeneratedCode(ClassWithNullables::class, setOf("""
    interface ClassWithNullables {
        widget: Widget | null;
    }
    """, widget))
    }

    it("handles ClassWithComplexNullables") {
        assertGeneratedCode(ClassWithComplexNullables::class, setOf("""
    interface ClassWithComplexNullables {
        maybeWidgets: (string | null)[] | null;
        maybeWidgetsArray: (string | null)[] | null;
    }
    """))
    }

    it("handles ClassWithNullableList") {
        assertGeneratedCode(ClassWithNullableList::class, setOf("""
    interface ClassWithNullableList {
        strings: string[] | null;
    }
    """))
    }

    it("handles GenericClass") {
        assertGeneratedCode(GenericClass::class, setOf("""
    interface GenericClass<A, B, C extends any[]> {
        a: A;
        b: (B | null)[];
        c: C;
    }
    """))
    }

    it("handles DerivedClass") {
        assertGeneratedCode(DerivedClass::class, setOf("""
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
        assertGeneratedCode(ClassWithMethods::class, setOf("""
    interface ClassWithMethods {
    }
    """))
    }

    it("handles AbstractClass") {
        assertGeneratedCode(AbstractClass::class, setOf("""
    interface AbstractClass {
        concreteProperty: string;
        abstractProperty: int;
    }
    """))
    }

    it("handles ClassWithEnum") {
        assertGeneratedCode(ClassWithEnum::class, setOf("""
    interface ClassWithEnum {
        direction: Direction;
    }
    """, """type Direction = "North" | "West" | "South" | "East";"""))
    }

    it("handles DataClass") {
        assertGeneratedCode(DataClass::class, setOf("""
    interface DataClass {
        prop: string;
    }
    """))
    }

    it("handles ClassWithAny") {
        // Note: in TypeScript any includes null and undefined.
        assertGeneratedCode(ClassWithAny::class, setOf("""
    interface ClassWithAny {
        required: any;
        optional: any;
    }
    """))
    }

    it("supports type mapping for classes") {
        assertGeneratedCode(ClassWithDependencies::class, setOf("""
interface ClassWithDependencies {
    widget: CustomWidget;
}
"""), mappings = mapOf(Widget::class to "CustomWidget"))
    }

    it("supports type mapping for basic types") {
        assertGeneratedCode(DataClass::class, setOf("""
    interface DataClass {
        prop: CustomString;
    }
    """), mappings = mapOf(String::class to "CustomString"))
    }

    it("supports transforming property names") {
        assertGeneratedCode(DataClass::class, setOf("""
    interface DataClass {
        PROP: string;
    }
    """), classTransformers = mapOf(
            DataClass::class to object: ClassTransformer {
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
})