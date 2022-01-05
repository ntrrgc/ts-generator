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

/**
 * Class transformer pipeline.
 *
 * For each method the return value of the first transformer
 * to return not null is used.
 */
internal class ClassTransformerPipeline(private val memberTransformers: List<ClassTransformer>) :
    ClassTransformer {

    override fun transformPropertyList(
        properties: List<KProperty<*>>,
        klass: KClass<*>
    ): List<KProperty<*>> {
        var ret = properties
        memberTransformers.forEach { transformer ->
            ret = transformer.transformPropertyList(ret, klass)
        }
        return ret
    }

    override fun transformPropertyName(
        propertyName: String,
        property: KProperty<*>,
        klass: KClass<*>
    ): String {
        var ret = propertyName
        memberTransformers.forEach { transformer ->
            ret = transformer.transformPropertyName(ret, property, klass)
        }
        return ret
    }

    override fun transformPropertyType(
        type: KType,
        property: KProperty<*>,
        klass: KClass<*>
    ): KType {
        var ret = type
        memberTransformers.forEach { transformer ->
            ret = transformer.transformPropertyType(ret, property, klass)
        }
        return ret
    }
}
