/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.calculator

object Calculator {
    fun compute(
        formula: String,
        // TODO NPI Use Variable instead of String for keys ?
        variables: Map<String, Int> = emptyMap(),
    ): Int? = try {
        compute(formula, variables)
    } catch (_: Exception) {
        null
    }

    fun computeOrThrow(
        formula: String,
        variables: Map<String, Int> = emptyMap(),
    ): Int {
        val resolvedFormula = resolveFormula(formula, variables)
        val tokens = requireNotNull(tokenize(resolvedFormula)) {
            "Failed to parse expression: $resolvedFormula"
        }
        val parser = Parser(tokens)
        return requireNotNull(parser.parseExpression()) {
            "Unable to parse expression: $resolvedFormula"
        }
    }
}

private fun resolveFormula(formula: String, variables: Map<String, Int>): String {
    var updated = formula
    variables.forEach { (key, value) ->
        updated = updated.replace(oldValue = key, newValue = value.toString(), ignoreCase = false)
    }
    return updated
}

private fun tokenize(formula: String): List<String> {
    val regex = Regex("""\d+|\+|-|\*|/|\(|\)""")
    val matches = regex.findAll(formula)
    val tokens = matches.map { it.value }.toList()

    // Basic validation: ensure all non-whitespace characters are matched
    val matchedLength = tokens.sumOf { it.length }
    val whitespaceCount = formula.count { it.isWhitespace() }
    if (matchedLength + whitespaceCount != formula.length) {
        error(
            "tokenization failed for formula $formula: \n" +
                "  - matchedLength=$matchedLength, \n" +
                "  - whitespaceCount=$whitespaceCount, \n" +
                "  - formulaLength=${formula.length}"
        )
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
