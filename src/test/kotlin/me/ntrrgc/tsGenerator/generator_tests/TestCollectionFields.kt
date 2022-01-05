package me.ntrrgc.tsGenerator.generator_tests

import me.ntrrgc.tsGenerator.tests.assertGeneratedCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestCollectionFields : Spek({
    describe("test collections are converted") {

        it("handles ClassWithLists") {
            assertGeneratedCode(
                ClassWithLists::class,
                setOf(
                    """
                    interface ClassWithLists {
                        aList: string[];
                        anArrayList: string[];
                    }
                    """,
                )
            )
        }
        it("handles ClassWithSets") {
            assertGeneratedCode(
                ClassWithSets::class,
                setOf(
                    """
                    interface ClassWithSets {
                        aSet: string[];
                    }
                    """,
                )
            )
        }

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
    }

}) {
    companion object {

        class ClassWithLists(
            val aList: List<String>,
            val anArrayList: ArrayList<String>
        )

        class ClassWithSets(
            val aSet: Set<String>,
        )

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
