fun main() {
    fun part1(input: List<String>): Int {
        val numbers = input.map { it.toInt() }
        var count = 0;
        for (i in 1 until numbers.size) {
            if (numbers[i] > numbers[i-1]) {
                count++
            }
        }
        return count;
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map { it.toInt() }
        val windowSums = ArrayList<Int>()
        for (i in 0 until numbers.size - 2) {
            windowSums.add(numbers.subList(i, i + 3).sum())
        }

        var count = 0;
        for (i in 1 until windowSums.size) {
            if (windowSums[i] > windowSums[i-1]) {
                count++
            }
        }
        return count;
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: " + part1TestResult)
    check( part1TestResult == 7)

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: " + part2TestResult)
    check( part2TestResult == 5)

    val input = readInput("Day01")
    println("Part1: " + part1(input)) // 1393
    println("Part2: " + part2(input)) // 1359
}
