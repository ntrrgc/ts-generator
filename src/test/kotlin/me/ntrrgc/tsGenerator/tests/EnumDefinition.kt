package me.ntrrgc.tsGenerator.tests

class EnumDefinition(val code: String): TypeScriptDefinition {
    override fun toString(): String {
        return code
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EnumDefinition) {
            return false
        }

        return this.code == other.code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}