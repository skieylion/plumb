package io.github.skieylion.plumb.report

import kotlin.math.abs

internal object Json {

    private const val MAX_EXACT_INTEGER = 9_007_199_254_740_992.0
    private const val FIRST_PRINTABLE = ' '

    fun obj(members: List<Pair<String, String>>): String =
        members.joinToString(separator = ",", prefix = "{", postfix = "}") { (name, value) -> "${str(name)}:$value" }

    fun obj(vararg members: Pair<String, String>): String = obj(members.asList())

    fun arr(items: List<String>): String = items.joinToString(separator = ",", prefix = "[", postfix = "]")

    fun str(value: String): String = buildString {
        append('"')
        value.forEach { appendEscaped(it) }
        append('"')
    }

    fun num(value: Int): String = value.toString()

    fun num(value: Double): String {
        require(value.isFinite()) { "JSON has no representation for $value" }
        val isExactInteger = value % 1.0 == 0.0 && abs(value) < MAX_EXACT_INTEGER
        return if (isExactInteger) value.toLong().toString() else value.toString()
    }

    private fun StringBuilder.appendEscaped(char: Char) {
        when (char) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> if (char < FIRST_PRINTABLE) append("\\u%04x".format(char.code)) else append(char)
        }
    }
}
