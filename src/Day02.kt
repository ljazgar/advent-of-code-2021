fun main() {
    fun part1(input: List<String>): Int {
        var x = 0
        var depth = 0
        input.forEach {
            val splitted = it.split(' ')
            val command = splitted[0]
            val distance = splitted[1].toInt()

            when (command) {
                "forward" -> x += distance
                "up" -> depth -= distance
                "down" -> depth += distance
            }
        }
        return x * depth
    }

    fun part2(input: List<String>): Int {
        var x = 0
        var depth = 0
        var aim = 0
        input.forEach {
            val (command, distance) = it.split(' ').let { (a, b) -> Pair(a, b.toInt()) }

            when (command) {
                "down" -> aim += distance
                "up" -> aim -= distance
                "forward" -> {
                    x += distance
                    depth += aim * distance
                }
            }
        }
        return x * depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: " + part1TestResult)
    check( part1TestResult == 150)

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: " + part2TestResult)
    check( part2TestResult == 900)

    val input = readInput("Day02")
    println("Part1: " + part1(input))
    println("Part2: " + part2(input))
}
