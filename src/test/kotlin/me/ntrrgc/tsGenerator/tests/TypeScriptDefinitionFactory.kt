package me.ntrrgc.tsGenerator.tests

object TypeScriptDefinitionFactory {
    fun fromCode(tsCode: String): TypeScriptDefinition {
        val code = tsCode.trim()

        if (code.startsWith("interface")) {
            return ClassDefinition(code)
        } else if (code.startsWith("type")) {
            return EnumDefinition(code)
        } else {
            throw RuntimeException("Unknown definition type: $code")
        }
    }
}