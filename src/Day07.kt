import kotlin.math.abs

fun main() {

    fun summaryFuelBurn(alignX: Int, initialXs: List<Int>): Int {
        return initialXs.sumOf { abs(alignX - it) }
    }

    fun part1(initialXs: List<Int>): Int {
        val maxX = initialXs.maxOf { it }
        val minX = initialXs.minOf { it }

        return (minX .. maxX)
            .map { alignX -> summaryFuelBurn(alignX, initialXs) }
            .minOf { it }
    }

    fun fuelBurn2(distance: Int): Int {
        return distance * (distance + 1) / 2
    }

    fun summaryFuelBurn2(alignX: Int, initialXs: List<Int>): Int {
        return initialXs.sumOf { fuelBurn2(abs(alignX - it)) }
    }

    fun part2(initialXs: List<Int>): Int {
        val maxX = initialXs.maxOf { it }
        val minX = initialXs.minOf { it }

        return (minX .. maxX)
            .map { alignX -> summaryFuelBurn2(alignX, initialXs) }
            .minOf { it }
    }

    val testInput = readDay08Input("Day07_test")
    val input = readDay08Input("Day07")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 37)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 168)
    println("Part2: " + part2(input)) // 90040997
}

private fun readDay08Input(name: String): List<Int> {
    return readInput(name).first().split(',').map { it.trim().toInt() }
}
