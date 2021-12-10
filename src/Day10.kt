fun main() {

    val closingOf = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )
    val openingChars = closingOf.keys
    val closingChars = closingOf.values

    fun Char.isOpening() = openingChars.contains(this)
    fun Char.isClosing() = closingChars.contains(this)

    // PART 1 *****************************************************************************************************

    fun findFirstIllegalChar(line: String): Char? {
        val stack = mutableListOf<Char>()

        for (c in line.toCharArray()) {
            if (c.isOpening()) {
                closingOf[c]?.let { stack.add(it) }
            } else if (c.isClosing()) {
                if (c == stack.last()) {
                    stack.removeLast()
                } else {
                    return c
                }
            }
        }
        return null
    }

    val pointsForChars = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    fun part1(input: List<String>): Int {
        return input
            .mapNotNull { line -> findFirstIllegalChar(line) }
            .groupingBy { it }.eachCount()
            .mapValues { it.value * (pointsForChars[it.key] ?: 0) }
            .values
            .sum()
    }

    // PART 2 *****************************************************************************************************
    fun findIncompleteLines(line: String): String? {
        val stack = mutableListOf<Char>()

        for (c in line.toCharArray()) {
            if (c.isOpening()) {
                closingOf[c]?.let { stack.add(it) }
            } else if (c.isClosing()) {
                if (c == stack.last()) {
                    stack.removeLast()
                } else {
                    return null // illegal character
                }
            }
        }
        if (stack.isEmpty()) {
            return null // correct line
        }
        val closing = stack.reversed().joinToString(separator = "")
        println("Line: $line -> $closing")
        return closing
    }

    val pointsForChars2 = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )

    fun scoreClosing(closing: String): Long {
        return closing.toCharArray().fold(0L) { acc, c ->
            acc * 5 + (pointsForChars2[c] ?: 0)
        }
    }

    fun part2(input: List<String>): Long {
        val linesScores = input
            .mapNotNull { line -> findIncompleteLines(line) }
            .map { scoreClosing(it) }
        println("Lines scores: $linesScores")
        return linesScores.sorted()[linesScores.size/2]
    }


    val testInput = readDay10Input("Day10_test")
    val input = readDay10Input("Day10")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 26397)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 288957L)
    println("Part2: " + part2(input))
}

private fun readDay10Input(name: String): List<String> {
    return readInput(name).map { it.trim() }
}
