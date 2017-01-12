package me.ntrrgc.tsGenerator

import java.time.LocalDate
import java.time.LocalDateTime

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
//            Thing::class,
            BaseClass::class
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