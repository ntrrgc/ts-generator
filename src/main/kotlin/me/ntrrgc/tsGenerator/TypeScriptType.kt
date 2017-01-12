package me.ntrrgc.tsGenerator

internal class TypeScriptType private constructor(val types: List<String>) {
    companion object {
        fun single(type: String, nullable: Boolean): TypeScriptType {
            return TypeScriptType(listOf(type)).let {
                if (nullable) {
                    it or TypeScriptType(listOf("null"))
                } else {
                    it
                }
            }
        }

        fun union(types: List<String>): TypeScriptType {
            return TypeScriptType(types)
        }
    }

    infix fun or(other: TypeScriptType): TypeScriptType {
        val combinedTypes = (this.types + other.types).distinct()

        return TypeScriptType(if ("any" in combinedTypes) {
            listOf("any")
        } else {
            combinedTypes
        })
    }

    fun formatWithParenthesis(): String {
        if (types.size == 1) {
            return types.single()
        } else {
            return "(" + this.formatWithoutParenthesis() + ")"
        }
    }

    fun formatWithoutParenthesis(): String {
        return types.joinToString(" | ")
    }
}