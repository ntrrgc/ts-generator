package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class FilteredClassTransformer(val wrappedTransformer: ClassTransformer,
                               val filter: (klass: KClass<*>) -> Boolean): ClassTransformer {

    override fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>>? {
        if (filter(klass)) {
            return wrappedTransformer.transformPropertyList(properties, klass)
        } else {
            return null
        }
    }

    override fun transformPropertyName(property: KProperty<*>, klass: KClass<*>): String? {
        if (filter(klass)) {
            return super.transformPropertyName(property, klass)
        } else {
            return null
        }
    }

    override fun transformPropertyType(property: KProperty<*>, klass: KClass<*>): String? {
        if (filter(klass)) {
            return super.transformPropertyType(property, klass)
        } else {
            return null
        }
    }
}