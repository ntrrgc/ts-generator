package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Class transformer pipeline.
 *
 * For each method the return value of the first transformer
 * to return not null is used.
 */
internal class ClassTransformerPipeline(val memberTransformers: List<ClassTransformer>): ClassTransformer {

    override fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>>? {
        return memberTransformers.firstNotNull { transformer ->
            transformer.transformPropertyList(properties, klass)
        }
    }

    override fun transformPropertyName(property: KProperty<*>, klass: KClass<*>): String? {
        return memberTransformers.firstNotNull { transformer ->
            transformer.transformPropertyName(property, klass)
        }
    }

    override fun transformPropertyType(property: KProperty<*>, klass: KClass<*>): String? {
        return memberTransformers.firstNotNull { transformer ->
            transformer.transformPropertyType(property, klass)
        }
    }
}