import java.math.BigInteger

fun main() {
    fun part1(input: List<Int>): Int {
        println("Input: $input")
        val fishList = input.toMutableList()

        for (day in 1..80) {
            var newFishCounter = 0
            for (i in fishList.indices) {
                if (fishList[i] > 0) {
                    fishList[i]--
                } else {
                    fishList[i] = 6
                    newFishCounter++
                }
            }
            fishList.addAll(List(newFishCounter) { 8 })
            println("Day $day: ${fishList.count()}")
        }
        return fishList.count()
    }

    fun part2(input: List<Int>): BigInteger {
        println("Input: $input")

        var counterCount = MutableList(9) { BigInteger.valueOf(0) }
        for (fishCounter in input) {
            counterCount[fishCounter]++
        }
        println("CounterCount: $counterCount")

        for (day in 1..256) {
            val nextCounterCount = MutableList(9) { i ->
                when(i) {
                    0, 1, 2, 3, 4, 5, 7 -> counterCount[i + 1]
                    6 -> counterCount[i + 1] + counterCount[0]
                    8 -> counterCount[0]
                    else -> error("No such element")
                }
            }
            counterCount = nextCounterCount
            println("Day $day: $counterCount  Sum: ${counterCount.sumOf { it }}")
        }

        return counterCount.sumOf { it }
    }

    val testInput = readDay06Input("Day06_test")
    val input = readDay06Input("Day06")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 5934)

    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == BigInteger.valueOf(26984457539L))

    println("Part2: " + part2(input))
}

private fun readDay06Input(name: String): List<Int> {
    return readInput(name).first().split(',').map { it.trim().toInt() }
}
