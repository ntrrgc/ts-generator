package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass

fun defaultEnumTransformer(klass: KClass<*>, enumValue: Any) = enumValue.toString()