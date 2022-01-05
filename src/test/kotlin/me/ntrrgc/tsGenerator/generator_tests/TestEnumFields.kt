package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestEnumFields : Spek({

    describe("expect enum fields are converted") {

        it("handles ClassWithEnum") {
            assertGeneratedCode(
                ClassWithEnum::class,
                setOf(
                    """
                    interface ClassWithEnum {
                        direction: Direction;
                    }
                    """,
                    """
                    type Direction = "North" | "West" | "South" | "East";
                    """,
                )
            )
        }
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
