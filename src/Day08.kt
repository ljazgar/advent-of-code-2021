fun main() {

    fun part1(input: List<Entry>): Int {
        println("Input: $input")
        return input
            .flatMap { it.outputs }
            .filter { it.length == 2 || it.length == 4 || it.length == 3 || it.length == 7 }
            .count()
    }

    fun outputHash(output: String): Int {
        return output.toCharArray().map { it.minus('A') }.reduce { acc, i -> acc * i }
    }

    fun intersection(pattern1: String, pattern2: String): String {
        val pattern2Chars = pattern2.toCharArray()
        return pattern1.toCharArray().filter { pattern2Chars.contains(it) }.joinToString(separator = "")
    }

    fun decodeSignalPatterns(signalPatterns: List<String>): Map<Int, Int> {
        val one = signalPatterns.find { it.length == 2 } ?: error("No 1")
        val four = signalPatterns.find { it.length == 4 } ?: error("No 4")
        val seven = signalPatterns.find { it.length == 3 } ?: error("No 7")
        val eight = signalPatterns.find { it.length == 7 } ?: error("No 8")

        val digits235 = signalPatterns.filter { it.length == 5 }
        val digits069 = signalPatterns.filter { it.length == 6 }

        val three = digits235.find { one.toCharArray().all { c -> it.contains(c) } } ?: error("No 3")
        val digits25 = digits235.filterNot { it == three }
        val five = digits25.find { intersection(it, four).length == 3 } ?: error("No 5")
        val two = digits25.find { intersection(it, four).length == 2 } ?: error("No 2")

        val six = digits069.find { intersection(it, one).length == 1 } ?: error("No 6")
        val digits09 = digits069.filterNot { it == six }
        val nine = digits09.find { intersection(it, four).length == 4 } ?: error("No 9")
        val zero = digits09.find { intersection(it, four).length == 3 } ?: error("No 9")

        val result = mapOf(
            outputHash(zero) to 0,
            outputHash(one) to 1,
            outputHash(two) to 2,
            outputHash(three) to 3,
            outputHash(four) to 4,
            outputHash(five) to 5,
            outputHash(six) to 6,
            outputHash(seven) to 7,
            outputHash(eight) to 8,
            outputHash(nine) to 9,
        )
        return result
    }

    fun decode(entry: Entry): String {
        val decodedSignals = decodeSignalPatterns(entry.signalPatterns)
        return entry.outputs
            .map { decodedSignals[outputHash(it)].toString() }
            .joinToString(separator = "")
    }

    fun part2(input: List<Entry>): Int {
        println("Input: $input")
        return input
            .map { entry -> decode(entry) }
            .map { output4Digits -> output4Digits.toInt() }
            .sum()
    }

    val testInput = readDay08Input("Day08_test")
    val input = readDay08Input("Day08")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 26)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 61229)
    println("Part2: " + part2(input)) //
}

data class Entry(val signalPatterns: List<String>, val outputs: List<String>)

private fun readDay08Input(name: String): List<Entry> {
    return readInput(name)
        .map { line -> line.split('|')
            .let { (signalPatternsPart, outputsPart) ->
                Entry(signalPatternsPart.splitToWords(), outputsPart.splitToWords())
            }
        }
}
