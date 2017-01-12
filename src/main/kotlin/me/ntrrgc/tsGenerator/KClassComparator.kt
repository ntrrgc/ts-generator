package me.ntrrgc.tsGenerator

import kotlin.reflect.KClass
import kotlin.reflect.isSubclassOf

/**
 * Used to sort classes so that more specific instances are
 * "less" than more generic ones.
 */
class KClassComparator: Comparator<KClass<*>> {
    override fun compare(o1: KClass<*>, o2: KClass<*>): Int {
        if (o1.isSubclassOf(o2) && o2.isSubclassOf(o1)) {
            return 0 // same class
        } else if (o1.isSubclassOf(o2)) {
            // o1 is derived from o2
            return -1
        } else {
            // o2 is derived from o1
            return 1
        }
    }
}