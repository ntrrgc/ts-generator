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

package me.ntrrgc.tsGenerator

import org.reflections.Reflections
import java.beans.Introspector
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaType

/**
 * TypeScript definition generator.
 *
 * Generates the content of a TypeScript definition file (.d.ts) that
 * covers a set of Kotlin and Java classes.
 *
 * This is useful when data classes are serialized to JSON and
 * handled in a JS or TypeScript web frontend.
 *
 * Supports:
 *  * Primitive types, with explicit int
 *  * Kotlin and Java classes
 *  * Data classes
 *  * Enums
 *  * Any type
 *  * Generic classes, without type erasure
 *  * Generic constraints
 *  * Class inheritance
 *  * Abstract classes
 *  * Lists as JS arrays
 *  * Maps as JS objects
 *  * Null safety, even inside composite types
 *  * Java beans
 *  * Mapping types
 *  * Customizing class definitions via transformers
 *  * Parenthesis are placed only when they are needed to disambiguate
 *
 * @constructor
 *
 * @param rootClasses Initial classes to traverse. Enough definitions
 * will be created to cover them and the types of their properties
 * (and so on recursively).
 *
 * @param mappings Allows to map some JVM types with JS/TS types. This
 * can be used e.g. to map LocalDateTime to JS Date.
 *
 * @param classTransformers Special transformers for certain subclasses.
 * They allow to filter out some classes, customize what methods are
 * exported, how they names are generated and what types are generated.
 *
 * @param ignoreSuperclasses Classes and interfaces specified here will
 * not be emitted when they are used as superclasses or implemented
 * interfaces of a class.
 *
 * @param intTypeName Defines the name integer numbers will be emitted as.
 * By default it's number, but can be changed to int if the TypeScript
 * version used supports it or the user wants to be extra explicit.
 */
class TypeScriptGenerator(
        private val rootClasses: Iterable<KClass<*>>,
        private val mappings: Map<KClass<*>, String> = mapOf(),
        classTransformers: List<ClassTransformer> = listOf(),
        ignoreSuperclasses: Set<KClass<*>> = setOf(),
        private val intTypeName: String = "number",
        private val voidType: VoidType = VoidType.NULL
) {
    private val pipeline = ClassTransformerPipeline(classTransformers)
    private val ignoredSuperclasses = setOf(
            Any::class,
            java.io.Serializable::class,
            Comparable::class
    ).plus(ignoreSuperclasses)

    private val generatedDefinitions = mapClasses { klass -> generateDefinition(klass) }
    private val generatedInstanceChecks = mapClasses { klass -> generateInstanceChecks(klass) }

    private interface VisitorContext {
        fun visitClass(klass: KClass<*>)
        fun formatClassType(type: KClass<*>): String
        fun formatKType(kType: KType): TypeScriptType
    }

    private fun <T> mapClasses(f: VisitorContext.(KClass<*>) -> T): Iterable<T> {
        val classToValue: MutableMap<KClass<*>,T> = linkedMapOf()

        val visitorContext = object: VisitorContext {
            override fun visitClass(klass: KClass<*>) {
                classToValue.computeIfAbsent(klass) { f(it) }
            }

            override fun formatClassType(type: KClass<*>): String {
                visitClass(type)
                return type.simpleName!!
            }

            override fun formatKType(kType: KType): TypeScriptType {
                val classifier = kType.classifier
                if (classifier is KClass<*>) {
                    val existingMapping = mappings[classifier]
                    if (existingMapping != null) {
                        return TypeScriptType.single(mappings[classifier]!!, false, voidType)
                    }
                }

                val classifierTsType = when (classifier) {
                    Boolean::class -> "boolean"
                    String::class, Char::class -> "string"
                    Int::class,
                    Long::class,
                    Short::class,
                    Byte::class -> intTypeName
                    Float::class, Double::class -> "number"
                    Any::class -> "any"
                    else -> {
                        @Suppress("IfThenToElvis")
                        if (classifier is KClass<*>) {
                            if (classifier.isSubclassOf(Iterable::class)
                                    || classifier.javaObjectType.isArray) {
                                // Use native JS array
                                // Parenthesis are needed to disambiguate complex cases,
                                // e.g. (Pair<string|null, int>|null)[]|null
                                val itemType = when (kType.classifier) {
                                // Native Java arrays... unfortunately simple array types like these
                                // are not mapped automatically into kotlin.Array<T> by kotlin-reflect :(
                                    IntArray::class -> Int::class.createType(nullable = false)
                                    ShortArray::class -> Short::class.createType(nullable = false)
                                    ByteArray::class -> Byte::class.createType(nullable = false)
                                    CharArray::class -> Char::class.createType(nullable = false)
                                    LongArray::class -> Long::class.createType(nullable = false)
                                    FloatArray::class -> Float::class.createType(nullable = false)
                                    DoubleArray::class -> Double::class.createType(nullable = false)

                                // Class container types (they use generics)
                                    else -> kType.arguments.single().type ?: KotlinAnyOrNull
                                }
                                "${formatKType(itemType).formatWithParenthesis()}[]"
                            } else if (classifier.isSubclassOf(Map::class)) {
                                // Use native JS associative object
                                val keyType = formatKType(kType.arguments[0].type ?: KotlinAnyOrNull)
                                val valueType = formatKType(kType.arguments[1].type ?: KotlinAnyOrNull)
                                "{ [key: ${keyType.formatWithoutParenthesis()}]: ${valueType.formatWithoutParenthesis()} }"
                            } else {
                                // Use class name, with or without template parameters
                                formatClassType(classifier) + if (kType.arguments.isNotEmpty()) {
                                    "<" + kType.arguments
                                            .map { arg ->
                                                formatKType(arg.type ?: KotlinAnyOrNull).formatWithoutParenthesis()
                                            }
                                            .joinToString(", ") + ">"
                                } else ""
                            }
                        } else if (classifier is KTypeParameter) {
                            classifier.name
                        } else {
                            "UNKNOWN" // giving up
                        }
                    }
                }

                return TypeScriptType.single(classifierTsType, kType.isMarkedNullable, voidType)
            }
        }

        rootClasses.forEach { visitorContext.visitClass(it) }

        return classToValue.values
    }

    companion object {
        private val KotlinAnyOrNull = Any::class.createType(nullable = true)

        fun isJavaBeanProperty(kProperty: KProperty<*>, klass: KClass<*>): Boolean {
            val beanInfo = Introspector.getBeanInfo(klass.java)
            return beanInfo.propertyDescriptors
                    .any { bean -> bean.name == kProperty.name }
        }

        private val MapTypeRegex = Regex("^\\{ \\[key: (.*?)]: (.*?) }$")
    }

    private fun generateEnum(klass: KClass<*>): String {
        return "type ${klass.simpleName} = ${klass.java.enumConstants
                .map { constant: Any ->
                    constant.toString().toJSString()
                }
                .joinToString(" | ")
        };"
    }

    private fun VisitorContext.getInterfacePropertyDefinitions(klass: KClass<*>): String =
            getInterfaceProperties(klass)
                    .map { (propertyName, formattedPropertyType) -> "    $propertyName: $formattedPropertyType;\n" }
                    .joinToString("")

    private fun VisitorContext.getInterfaceProperties(klass: KClass<*>): Map<String,String> = klass.declaredMemberProperties
            .filter { !isFunctionType(it.returnType.javaType) }
            .filter {
                it.visibility == KVisibility.PUBLIC || isJavaBeanProperty(it, klass)
            }
            .let { propertyList ->
                pipeline.transformPropertyList(propertyList, klass)
            }
            .map { property ->
                val propertyName = pipeline.transformPropertyName(property.name, property, klass)
                val propertyType = pipeline.transformPropertyType(property.returnType, property, klass)

                val formattedPropertyType = formatKType(propertyType).formatWithoutParenthesis()
                propertyName to formattedPropertyType
            }
            .toMap()

    private fun VisitorContext.getTemplateParameters(klass: KClass<*>): String = if (klass.typeParameters.isNotEmpty()) {
        "<" + klass.typeParameters.joinToString(", ") { typeParameter ->
            val bounds = typeParameter.upperBounds
                    .filter { it.classifier != Any::class }
            typeParameter.name + if (bounds.isNotEmpty()) {
                " extends " + bounds.joinToString(" & ") { bound ->
                    formatKType(bound).formatWithoutParenthesis()
                }
            } else {
                ""
            }
        } + ">"
    } else {
        ""
    }

    private fun VisitorContext.generateSealedClass(sealedClass: KClass<*>): String {
        val subclasses
                = Reflections(sealedClass.java.`package`.name).getSubTypesOf(sealedClass.java)
                    .map { it.kotlin }
                    .filter { it.declaredMemberProperties.isNotEmpty() }

        val interfaces = subclasses.joinToString("\n\n") { t -> generateInterface(t, sealedClass) }
                .let {
                    if (it.isNotBlank()) "\n\n$it"
                    else ""
                }

        val union = subclasses.joinToString(" | ") { "${it.simpleName}" }
                .let {
                    if (it.isNotBlank()) "(\n    $it\n);"
                    else ""
                }


        val interfaceProperties = getInterfacePropertyDefinitions(sealedClass).let {
            if (it.isNotBlank())
                if(union.isBlank()) "{\n$it}"
                else "{\n$it} & "
            else
                if(union.isBlank()) "{}"
                else ""
        }

        val typeKeyword =
                if (union.isNotBlank()) "type"
                else "interface"
        val typeEq =
                if (typeKeyword == "type") "= "
                else ""

        return "$typeKeyword ${sealedClass.simpleName}${getTemplateParameters(sealedClass)} $typeEq$interfaceProperties$union$interfaces"
    }

    private fun VisitorContext.generateSealedClassInstanceCheckers(sealedClass: KClass<*>): String {
        val subclasses
                = Reflections(sealedClass.java.`package`.name).getSubTypesOf(sealedClass.java)
                    .map { it.kotlin }
                    .filter { it.declaredMemberProperties.isNotEmpty() }

        val subclassInstanceCheckers =
                subclasses.map { generateInstanceChecker(it) }
                        .filter { it.isNotBlank() }
                        .joinToString("\n\n")
                        .let {
                            if (it.isNotBlank()) "\n\n$it"
                            else ""
                        }

        val fieldChecks = getFieldChecks(sealedClass)
        val subclassChecks = subclasses.joinToString(" || ") { "is${it.simpleName}(obj)" }
        val body = if(fieldChecks.isNotBlank()) {
            if(subclassChecks.isNotBlank()) "$fieldChecks && ($subclassChecks)"
            else fieldChecks
        } else {
            if(subclassChecks.isNotBlank()) subclassChecks
            else ""
        }
        val instanceChecker = body.let {
            if (it.isNotBlank()) "\n\nexport function is${sealedClass.simpleName}(obj: any): obj is ${sealedClass.simpleName} {\n    return $it;\n}"
            else ""
        }

        return "$instanceChecker$subclassInstanceCheckers"
    }

    private fun VisitorContext.getTypeCheck(expr: String, tsTypeName: String, identifier: String = expr): String {
        val nestedExpr: String by lazy {
            when {
                identifier == "it" -> "it1"
                identifier.startsWith("it") -> "it" + (identifier.substring(2).toInt() + 1)
                else -> "it"
            }
        }

        return when {
            setOf("number", "string", "boolean", "any").contains(tsTypeName) -> "!!$expr"
            tsTypeName.endsWith("[]") -> "!!$expr && $expr.every(($nestedExpr: any) => ${getTypeCheck(nestedExpr, tsTypeName.substring(0,tsTypeName.length - 2))})"
            MapTypeRegex.matches(tsTypeName) -> {
                val match = MapTypeRegex.matchEntire(tsTypeName)!!
                "!!$expr && Object.keys($expr).every(($nestedExpr: ${match.groupValues[1]}) => ${getTypeCheck("$expr[$nestedExpr]", match.groupValues[2], nestedExpr)})"
            }
            else -> "is$tsTypeName($expr)"
        }
    }

    private fun VisitorContext.getFieldChecks(klass: KClass<*>, sealedClass: KClass<*>? = null): String {
        val properties = getInterfaceProperties(klass) + (sealedClass?.let { getInterfaceProperties(it) } ?: emptyMap())

        return properties
                .filter { (_, typeName) ->
                    !with(typeName) { contains("undefined") || contains("null") }
                }
                .map { (fieldName, typeName) ->
                    getTypeCheck("obj.$fieldName", typeName)
                }
                .joinToString(" && ")
    }

    private fun VisitorContext.generateInstanceChecker(klass: KClass<*>, sealedClass: KClass<*>? = null): String {
        val fieldChecks = getFieldChecks(klass, sealedClass)
        return if(fieldChecks.isNotBlank())
            "export function is${klass.simpleName}(obj: any): obj is ${klass.simpleName} {\n    return $fieldChecks;\n}"
        else ""
    }

    private fun VisitorContext.generateInterface(klass: KClass<*>, sealedClass: KClass<*>? = null): String {
        val superclasses = (klass.superclasses
                .filterNot { it in ignoredSuperclasses || it == sealedClass }) +
            (sealedClass?.superclasses
                ?.filterNot { it in ignoredSuperclasses || it == sealedClass }
                ?: emptyList())

        val extendsString = if (superclasses.isNotEmpty()) {
            " extends " + superclasses.joinToString(", ") { formatClassType(it) }
        } else ""

        return "interface ${klass.simpleName}${getTemplateParameters(klass)}$extendsString {\n${getInterfacePropertyDefinitions(klass)}}"
    }

    private fun isFunctionType(javaType: Type): Boolean {
        return javaType is KCallable<*>
                || javaType.typeName.startsWith("kotlin.jvm.functions.")
                || (javaType is ParameterizedType && isFunctionType(javaType.rawType))
    }

    private fun VisitorContext.generateDefinition(klass: KClass<*>): String {
        return when {
            klass.java.isEnum -> generateEnum(klass)
            klass.isSealed -> generateSealedClass(klass)
            else -> generateInterface(klass)
        }
    }

    private fun VisitorContext.generateInstanceChecks(klass: KClass<*>): String {
        return when {
            klass.java.isEnum -> ""
            klass.isSealed -> generateSealedClassInstanceCheckers(klass)
            else -> generateInstanceChecker(klass)
        }
    }

    // Public API:
    val definitionsText: String
        get() = generatedDefinitions.joinToString("\n\n")

    val individualDefinitions: Set<String>
        get() = generatedDefinitions.toSet()

    val instanceChecksText: String
        get() = generatedInstanceChecks.joinToString("\n\n")

    val individualInstanceChecks: Set<String>
        get() = generatedInstanceChecks.toSet()
}