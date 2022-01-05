package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestKeyValueMapFields : Spek({


    it("transforms ClassWithMap") {
        assertGeneratedCode(
            ClassWithMap::class,
            setOf(
                """
                interface ClassWithMap {
                    values: { [key: string]: string };
                }
                """,
            )
        )
    }

    it("transforms ClassWithEnumMap") {
        assertGeneratedCode(
            ClassWithEnumMap::class,
            setOf(
                """
                type Direction = "North" | "West" | "South" | "East";
                """,
                """
                interface ClassWithEnumMap {
                    values: { [key in Direction]: string };
                }
                """,
            )
        )
    }

}) {
    companion object {

        class ClassWithMap(val values: Map<String, String>)

        enum class Direction {
            North,
            West,
            South,
            East
        }

        class ClassWithEnumMap(val values: Map<Direction, String>)

    }
}
