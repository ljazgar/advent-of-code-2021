fun main() {

    // PART 1 ******************************************************************************
    fun step(input: String, rules: Map<String, Char>): String {
        val resultBuffer= StringBuffer()
        for (i in input.indices) {
            if (i > 0) {
                val pair = input.substring(i-1, i+1)
                rules[pair]?.let {
                    resultBuffer.append( it)
                }
            }
            resultBuffer.append(input[i])
        }
        return resultBuffer.toString()
    }

    fun part1(input: Day14Input): Int {
        var s = input.template
        for (i in 1..10) {
            s = step(s, input.rules)
            println("After step $i: $s")
        }

        val counts = s.toCharArray().toList().groupingBy { it }.eachCount().map { it.value }.toList()
        val min = counts.minOf { it }
        val max = counts.maxOf { it }

        return max - min
    }

    // PART 2 ******************************************************************************

    fun stringToPairs(s: String): List<String> {
        val result = mutableListOf<String>()
        for (i in 1 until s.length) {
            val pair = s.substring(i-1, i+1)
            result.add(pair)
        }
        return result
    }

    fun <T> MutableMap<T, Long>.addToValue(key: T, value: Long) {
        this[key] = (this[key] ?: 0L) + value
    }

    fun step2(state: PolymerState, rules: Map<String, Char>): PolymerState {
        val newPairsCount = state.pairsCounts.toMutableMap()
        val newCharsCount = state.charsCounts.toMutableMap()

        rules.forEach { (pair, charToInsert) ->
            val pairCount = state.pairsCounts[pair] ?: 0L
            if (pairCount > 0) {
                newCharsCount.addToValue(charToInsert, pairCount)

                val newPair1 = "${pair.first()}$charToInsert"
                newPairsCount.addToValue(newPair1, pairCount)

                val newPair2 = "$charToInsert${pair.last()}"
                newPairsCount.addToValue(newPair2, pairCount)

                newPairsCount.addToValue(pair, -pairCount)
            }
        }

        return PolymerState(newPairsCount, newCharsCount)
    }

    fun part2(input: Day14Input): Long {
        val pairs = stringToPairs(input.template)
        println("Pairs: $pairs")
        val pairsCounts = pairs.groupingBy { it }.eachCount()
            .map { Pair(it.key, it.value.toLong()) }.toMap()
        val charCounts = input.template.toCharArray().toList().groupingBy { it }.eachCount()
            .map { Pair(it.key, it.value.toLong()) }.toMap()

        var state = PolymerState(pairsCounts, charCounts)
        println("Init state: $state")
        for (i in 1..40) {
            state = step2(state, input.rules)
            println("After step $i: $state")
        }

        val min = state.charsCounts.minOf { it.value }
        val max = state.charsCounts.maxOf { it.value }

        return max - min
    }

    // ***********************************************************************************

    val testInput = readDay14Input("Day14_test")
    println("testInput: $testInput")
    val input = readDay14Input("Day14")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 1588)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 2188189693529)
    println("Part2: " + part2(input))
}

private data class PolymerState(val pairsCounts: Map<String, Long>, val charsCounts: Map<Char, Long>)

private data class Day14Input(val template: String, val rules: Map<String,Char>)
private fun readDay14Input(name: String): Day14Input {
    val lines = readInput(name)
    val template = lines.first()

    val rules = lines.subList(2, lines.size)
        .map { line ->
            line.split(" -> ").let { (a, b) -> Pair(a.trim(), b.trim().first())}
        }.toMap()
    return Day14Input(template, rules)
}
