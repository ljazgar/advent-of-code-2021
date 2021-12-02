fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toInt() }
            .zipWithNext()
            .count { (curr, next) -> next > curr}
    }

    fun part2(input: List<String>): Int {
        val numbers = input.map { it.toInt() }
        return numbers.windowed(3)
            .map { it.sum() }
            .zipWithNext()
            .count { (curr, next) -> next > curr}
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
