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
import kotlin.reflect.full.isSubclassOf

/**
 * Used to sort classes so that more specific instances are
 * "less" than more generic ones.
 */
class KClassComparator : Comparator<KClass<*>> {
    override fun compare(o1: KClass<*>, o2: KClass<*>): Int {
        return if (o1.isSubclassOf(o2) && o2.isSubclassOf(o1)) {
            0 // same class
        } else if (o1.isSubclassOf(o2)) {
            // o1 is derived from o2
            -1
        } else {
            // o2 is derived from o1
            1
        }
    }
}
