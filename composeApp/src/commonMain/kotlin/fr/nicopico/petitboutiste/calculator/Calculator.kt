/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.calculator

fun compute(
    formula: String,
    variables: Map<String, Int> = emptyMap(),
): Int? {
    val resolvedFormula = resolveFormula(formula, variables)
    val tokens = tokenize(resolvedFormula) ?: return null
    return try {
        val parser = Parser(tokens)
        parser.parseExpression()
    } catch (_: Exception) {
        null
    }
}

fun resolveFormula(formula: String, variables: Map<String, Int>): String {
    var updated = formula
    variables.forEach { (key, value) ->
        updated = updated.replace(oldValue = "[[$key]]", newValue = value.toString(), ignoreCase = false)
    }
    return updated
}

private fun tokenize(formula: String): List<String>? {
    val regex = Regex("""\d+|\+|-|\*|/|\(|\)""")
    val matches = regex.findAll(formula)
    val tokens = matches.map { it.value }.toList()

    // Basic validation: ensure all non-whitespace characters are matched
    val matchedLength = tokens.sumOf { it.length }
    val whitespaceCount = formula.count { it.isWhitespace() }
    if (matchedLength + whitespaceCount != formula.length) {
        return null
    }

    return tokens
}

private class Parser(private val tokens: List<String>) {
    private var pos = 0

    private fun peek(): String? = if (pos < tokens.size) tokens[pos] else null
    private fun consume(): String? = if (pos < tokens.size) tokens[pos++] else null

    fun parseExpression(): Int? {
        var result = parseTerm() ?: return null

        while (peek() == "+" || peek() == "-") {
            val op = consume()
            val right = parseTerm() ?: return null
            result = when (op) {
                "+" -> result + right
                "-" -> result - right
                else -> throw IllegalStateException()
            }
        }
        return result
    }

    private fun parseTerm(): Int? {
        var result = parseFactor() ?: return null

        while (peek() == "*" || peek() == "/") {
            val op = consume()
            val right = parseFactor() ?: return null
            result = when (op) {
                "*" -> result * right
                "/" -> if (right != 0) result / right else return null
                else -> throw IllegalStateException()
            }
        }
        return result
    }

    private fun parseFactor(): Int? {
        val token = consume() ?: return null
        return when {
            token == "(" -> {
                val result = parseExpression()
                if (consume() != ")") return null
                result
            }
            token.all { it.isDigit() } -> token.toInt()
            else -> null
        }
    }
}
