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

package me.ntrrgc.tsGenerator.tests

import com.winterbe.expekt.should
import kotlin.reflect.KClass
import me.ntrrgc.tsGenerator.ClassTransformer
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import me.ntrrgc.tsGenerator.VoidType

fun assertGeneratedCode(
    klass: KClass<*>,
    expectedOutput: Set<String>,
    mappings: Map<KClass<*>, String> = mapOf(),
    classTransformers: List<ClassTransformer> = listOf(),
    ignoreSuperclasses: Set<KClass<*>> = setOf(),
    voidType: VoidType = VoidType.NULL
) {
    val generator = TypeScriptGenerator(
        listOf(klass),
        mappings,
        classTransformers,
        ignoreSuperclasses,
        intTypeName = "int",
        voidType = voidType
    )

    val expected = expectedOutput
        .map(TypeScriptDefinitionFactory::fromCode)
        .toSet()
    val actual = generator.individualDefinitions
        .map(TypeScriptDefinitionFactory::fromCode)
        .toSet()

    actual.should.equal(expected)
}
