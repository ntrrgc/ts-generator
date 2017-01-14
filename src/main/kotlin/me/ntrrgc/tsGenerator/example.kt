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

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.createType

enum class Rarity(val abbreviation: String) {
    Normal("N"),
    Rare("R"),
    SuperRare("SR"),
}

data class Card(
    val ref: String,
    val rarity: Rarity,
    val name: String,
    val description: String,
    val command: String?,
    val playCard: (() -> Unit)?
) {
    val generatedTitleLine = "*$name* [$rarity]"
}

data class Inventory(
    val cards: List<Card> = listOf()
)

data class Player(
    val name: String,
    val inventory: Inventory = Inventory(),
    val achievementsProgress: List<AchievementCompletionState> = listOf(),
    val notices: List<Notice> = listOf()
)

data class Notice(
    val dateTime: LocalDateTime,
    val text: String
)

data class Achievement(
    val ref: String,
    val title: String,
    val description: String,
    val measuredProperty: (player: Player) -> Int,
    val neededValue: Int
)

data class AchievementCompletionState(
    val achievementRef: String,
    val reachedValue: Int
)

open class BaseClass(val a: Int)

class DerivedClass(val b: List<String>): BaseClass(4)

class ContrivedExample<A, out B, out C: List<Any>>(
    private val a: A,
    val b: B,
    val c: C,
    val listOfPairs: List<Pair<Int, B>>)

data class CardRepository(
    val cardsByRef: Map<String, Card>)

//data class Thing(
//    val wow: Map<String, Miau<LocalDateTime, String, List<Card>>>,
//    val weird: List<Pair<String?, Int>?>?,
//    val gender: Gender?,
//    val derived: DerivedClass
//)

fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            AchievementCompletionState::class
        ),
        classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(propertyName: String, property: KProperty<*>, klass: KClass<*>): String {
                    if (propertyName == "achievementRef") {
                        return "achievement"
                    } else {
                        return propertyName
                    }
                }

                override fun transformPropertyType(type: KType, property: KProperty<*>, klass: KClass<*>): KType {
                    // Note: property is the actual property from the class
                    // (unless replaced in transformPropertyList()), so
                    // it maintains the original property name declared
                    // in the code.
                    if (property.name == "achievementRef") {
                        return Achievement::class.createType(nullable = false)
                    } else {
                        return type
                    }
                }
            }
        )
    ).definitionsText)
    return

    println(TypeScriptGenerator(
        rootClasses = setOf(
//            Player::class
//            Thing::class,
//            CardRepository::class
            AchievementCompletionState::class
//            Achievement::class,
//            Card::class,
//            Candy::class
        ),
        mappings = mapOf(
            LocalDateTime::class to "Date",
            LocalDate::class to "Date"
        ),
        classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(
                    propertyName: String,
                    property: KProperty<*>,
                    klass: KClass<*>): String
                {
                    return camelCaseToSnakeCase(propertyName)
                }
            }
        )
//        classTransformers = mapOf(BaseClass::class to object :ClassTransformer {
//            override fun transformPropertyName(property: KProperty<*>, klass: KClass<*>): String? {
//                return property.name.toUpperCase()
//            }
//        }),
//        defaultTransformer = object: ClassTransformer {
//            override fun transformPropertyName(property: KProperty<*>, klass: KClass<*>): String? {
//                return "_" + property.name
//            }
//        }
    ).definitionsText)
}