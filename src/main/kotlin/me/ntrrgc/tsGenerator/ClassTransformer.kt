package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * A TypeScript generator class transformer.
 *
 * Allows to customize how class properties are transformed from
 * Kotlin to TypeScript.
 */
interface ClassTransformer {

    /**
     * Generates a list with the properties to include in the
     * definition.
     *
     * If it returns null, the value of the next class transformer
     * in the pipeline is used.
     *
     * @param properties Default properties. By default public,
     * non-function properties are chosen.
     * @param klass Class the properties come from.
     */
    fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>>? {
        return null
    }

    /**
     * Returns the property name that will be included in the
     * definition.
     *
     * If it returns null, the value of the next class transformer
     * in the pipeline is used.
     */
    fun transformPropertyName(property: KProperty<*>, klass: KClass<*>): String? {
        return null
    }

    /**
     * Returns the property type that will be included in the
     * definition.
     *
     * If it returns null, the value of the next class transformer
     * in the pipeline is used.
     *
     * If the pipeline ends without the property type being
     * transformed it is handled automatically by TypeScriptGenerator.
     * Classes are visited (so that a definition for the type is
     * created) and its class name is used, with template parameters
     * if necessary.
     */
    fun transformPropertyType(property: KProperty<*>, klass: KClass<*>): String? {
        return null
    }
}