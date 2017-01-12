package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

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
     * @param properties Property list from previous stage in the pipeline,
     * by default the public, non-function properties are chosen.
     * @param klass Class the properties come from.
     */
    fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>> {
        return properties
    }

    /**
     * Returns the property name that will be included in the
     * definition.
     *
     * If it returns null, the value of the next class transformer
     * in the pipeline is used.
     *
     * @param propertyName Property name generated in previous
     * transformers in the pipeline, by default the original property
     * name.
     * @param property The actual property of the class.
     * @param klass Class the property comes from.
     */
    fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
        return propertyName
    }

    /**
     * Returns the property type that will be processed and included
     * in the definition.
     *
     * @param type Type coming from previous stages of the pipeline,
     * by default the actual type of the property.
     * @param property The actual property of the class.
     * @param klass Class the property comes from.
     */
    fun transformPropertyType(type: KType, property: KProperty<*>, klass: KClass<*>): KType {
        return type
    }
}