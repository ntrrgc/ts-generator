package me.ntrrgc.tsGenerator

import java.time.LocalDate
import java.time.LocalDateTime

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

class Miau<A, out B, out C: List<Any>>(private val a: A, val b: B, val c: C)

//data class Thing(
//    val wow: Map<String, Miau<LocalDateTime, String, List<Card>>>,
//    val weird: List<Pair<String?, Int>?>?,
//    val gender: Gender?,
//    val derived: DerivedClass
//)

fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            Player::class
//            Thing::class,
//            BaseClass::class
//            Player::class,
//            Achievement::class,
//            Card::class,
//            Candy::class
        ),
        mappings = mapOf(
            LocalDateTime::class to "Date",
            LocalDate::class to "Date"
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