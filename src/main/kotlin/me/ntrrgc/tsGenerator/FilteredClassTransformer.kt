package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

class FilteredClassTransformer(val wrappedTransformer: ClassTransformer,
                               val filter: (klass: KClass<*>) -> Boolean): ClassTransformer {

    override fun transformPropertyList(properties: List<KProperty<*>>, klass: KClass<*>): List<KProperty<*>> {
        if (filter(klass)) {
            return wrappedTransformer.transformPropertyList(properties, klass)
        } else {
            return properties
        }
    }

    override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
        if (filter(klass)) {
            return super.transformPropertyName(propertyName, property, klass)
        } else {
            return propertyName
        }
    }

    override fun transformPropertyType(type: KType, property: KProperty<*>, klass: KClass<*>): KType {
        if (filter(klass)) {
            return super.transformPropertyType(type, property, klass)
        } else {
            return type
        }
    }
}