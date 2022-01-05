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

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

class FilteredClassTransformer(
    private val wrappedTransformer: ClassTransformer,
    val filter: (klass: KClass<*>) -> Boolean
) : ClassTransformer {

    override fun transformPropertyList(
        properties: List<KProperty<*>>,
        klass: KClass<*>
    ): List<KProperty<*>> {
        return if (filter(klass)) {
            wrappedTransformer.transformPropertyList(properties, klass)
        } else {
            properties
        }
    }

    override fun transformPropertyName(
        propertyName: String,
        property: KProperty<*>,
        klass: KClass<*>
    ): String {
        return if (filter(klass)) {
            wrappedTransformer.transformPropertyName(propertyName, property, klass)
        } else {
            propertyName
        }
    }

    override fun transformPropertyType(
        type: KType,
        property: KProperty<*>,
        klass: KClass<*>
    ): KType {
        return if (filter(klass)) {
            wrappedTransformer.transformPropertyType(type, property, klass)
        } else {
            type
        }
    }
}
