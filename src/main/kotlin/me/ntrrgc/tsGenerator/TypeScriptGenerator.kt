package me.ntrrgc.tsGenerator

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.*
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
 * exported and how they names are generated. If several transformers
 *
 * @param defaultTransformer Optional transformer applied to each class
 * when a more specific transformer is not found.
 */
class TypeScriptGenerator(
    rootClasses: Iterable<KClass<*>>,
    private val mappings: Map<KClass<*>, String> = mapOf(),
    private val classTransformers: Map<KClass<*>, ClassTransformer> = mapOf(),
    private val defaultTransformer: ClassTransformer? = null
) {
    private val visitedClasses: MutableSet<KClass<*>> = java.util.HashSet()
    private val generatedDefinitions = mutableListOf<String>()

    init {
        rootClasses.forEach { visitClass(it) }
    }

    companion object {
        private val KotlinAnyOrNull = Any::class.createType(nullable = true)
    }

    private fun visitClass(klass: KClass<*>) {
        if (klass !in visitedClasses) {
            visitedClasses.add(klass)

            generatedDefinitions.add(generateDefinition(klass))
        }
    }

    private fun formatClassType(type: KClass<*>): String {
        return mappings[type] ?: {
            visitClass(type)
            type.simpleName!!
        }()
    }

    private fun formatKType(kType: KType): TypeScriptType {
        val classifier = kType.classifier
        if (classifier is KClass<*>) {
            val existingMapping = mappings[classifier]
            if (existingMapping != null) {
                return TypeScriptType.single(mappings[classifier]!!, false)
            }
        }

        val classifierTsType = when (classifier) {
            String::class -> "string"
            Int::class,
            Long::class,
            Short::class,
            Byte::class -> "int"
            Float::class, Double::class -> "number"
            Any::class -> "any"
            else -> {
                @Suppress("IfThenToElvis")
                if (classifier is KClass<*>) {
                    if (classifier.isSubclassOf(Iterable::class)
                        || classifier.javaObjectType.isArray)
                    {
                        // Use native JS array
                        // Parenthesis are needed to disambiguate complex cases,
                        // e.g. (Pair<string|null, int>|null)[]|null
                        val itemType = formatKType(kType.arguments.single().type ?: KotlinAnyOrNull)
                        "${itemType.formatWithParenthesis()}[]"
                    } else if (classifier.isSubclassOf(Map::class)) {
                        // Use native JS associative object
                        val keyType = formatKType(kType.arguments[0].type ?: KotlinAnyOrNull)
                        val valueType = formatKType(kType.arguments[1].type ?: KotlinAnyOrNull)
                        "{ [key: ${keyType.formatWithoutParenthesis()}]: ${valueType.formatWithoutParenthesis()} }"
                    } else {
                        // Use class name, with or without template parameters
                        formatClassType(classifier) + if (kType.arguments.isNotEmpty()) {
                            "<" + kType.arguments
                                .map { arg -> formatKType(arg.type ?: KotlinAnyOrNull).formatWithoutParenthesis() }
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

        return TypeScriptType.single(classifierTsType, kType.isMarkedNullable)
    }

    private fun generateEnum(klass: KClass<*>): String {
        return "type ${klass.simpleName} = ${klass.java.enumConstants
            .map { constant: Any ->
                constant.toString().toJSString()
            }
            .joinToString(" | ")
        };"
    }

    private fun generateInterface(klass: KClass<*>): String {
        val superclasses = klass.superclasses
            .filterNot { it in setOf(Any::class, java.io.Serializable::class, Comparable::class) }
        val extendsString = if (superclasses.isNotEmpty()) {
            " extends " + superclasses
                .map { formatClassType(it) }
                .joinToString(", ")
        } else ""

        val templateParameters = if (klass.typeParameters.isNotEmpty()) {
            "<" + klass.typeParameters
                .map { typeParameter ->
                    val bounds = typeParameter.upperBounds
                        .filter { it.classifier != Any::class }
                    typeParameter.name + if (bounds.isNotEmpty()) {
                        " extends " + bounds
                            .map { bound ->
                                formatKType(bound).formatWithoutParenthesis()
                            }
                            .joinToString(" & ")
                    } else {
                        ""
                    }
                }
                .joinToString(", ") + ">"
        } else {
            ""
        }

        val transformerPipeline = ClassTransformerPipeline(
            classTransformers
                .toSortedMap(KClassComparator())
                .filter { klass.isSubclassOf(it.key) }
                .values
                .plusElement(defaultTransformer)
                .filterNotNull()
        )

        return "interface ${klass.simpleName}$templateParameters$extendsString {\n" +
            klass.declaredMemberProperties
                .filter { !isFunctionType(it.returnType.javaType) }
                .filter { it.visibility == KVisibility.PUBLIC }
                .let { propertyList ->
                    transformerPipeline.transformPropertyList(propertyList, klass)
                }
                .map { property ->
                    val propertyName = transformerPipeline.transformPropertyName(property.name, property, klass)
                    val propertyType = transformerPipeline.overridePropertyType(property, klass)
                        ?: formatKType(property.returnType).formatWithoutParenthesis()

                    "    $propertyName: $propertyType;\n"
                }
                .joinToString("") +
            "}"
    }

    private fun isFunctionType(javaType: Type): Boolean {
        return javaType is KCallable<*>
            || javaType.typeName.startsWith("kotlin.jvm.functions.")
            || (javaType is ParameterizedType && isFunctionType(javaType.rawType))
    }

    private fun generateDefinition(klass: KClass<*>): String {
        return if (klass.java.isEnum) {
            generateEnum(klass)
        } else {
            generateInterface(klass)
        }
    }

    // Public API:
    val definitionsText: String
        get() = generatedDefinitions.joinToString("\n\n")

    val individualDefinitions: Set<String>
        get() = generatedDefinitions.toSet()
}