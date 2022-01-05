package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TestEnumFields : Spek({

    it("handles ClassWithEnum") {
        assertGeneratedCode(
            ClassWithEnum::class,
            setOf(
                """
                interface ClassWithEnum {
                    direction: Direction;
                }
                """.trimIndent(),
                """
                type Direction = "North" | "West" | "South" | "East";
                """.trimIndent()
            )
        )
    }

}) {
    companion object {

        enum class Direction {
            North,
            West,
            South,
            East
        }

        class ClassWithEnum(val direction: Direction)

    }
}
