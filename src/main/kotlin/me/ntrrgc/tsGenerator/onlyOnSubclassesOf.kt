package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.isSubclassOf

fun ClassTransformer.onlyOnSubclassesOf(klass: KClass<*>): FilteredClassTransformer {
    return FilteredClassTransformer(this, { it.isSubclassOf(klass) })
}