package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Class transformer pipeline.
 *
 * For each method the return value of the first transformer
 * to return not null is used.
 */
internal class ClassTransformerPipeline(val memberTransformers: List<ClassTransformer>): ClassTransformer {

    override fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>> {
        var ret = properties
        memberTransformers.forEach { transformer ->
            ret = transformer.transformPropertyList(ret, klass)
        }
        return ret
    }

    override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
        var ret = propertyName
        memberTransformers.forEach { transformer ->
            ret = transformer.transformPropertyName(ret, property, klass)
        }
        return ret
    }

    override fun transformPropertyType(type: KType, property: KProperty<*>, klass: KClass<*>): KType {
        var ret = type
        memberTransformers.forEach { transformer ->
            ret = transformer.transformPropertyType(ret, property, klass)
        }
        return ret
    }
}