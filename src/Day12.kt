fun main() {

    fun part1(input: List<Int>): Int {
        return 0
    }

    fun part2(input: List<Int>): Int {
        return 0
    }

    val testInput = readDay12Input("Day12_test")
    println("testInput: $testInput")
    val input = readDay12Input("Day12")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 1234567890)
    println("Part1: " + part1(input))

//    val part2TestResult = part2(testInput)
//    println("Part2: Test Result: $part2TestResult")
//    check( part2TestResult == 1234567890)
//    println("Part2: " + part2(input)) //
}

private fun readDay12Input(name: String): List<Int> {
    return readInput(name).map { it.trim().toInt() }
}

//private fun readDay12nput(name: String): List<String> {
//    return readInput(name).map { it.trim() }
//}
//
//private fun readDay12Input(name: String): List<List<Int>> {
//    return readInput(name)
//        .map { it.trim() }
//        .map { it.toCharArray().map { c -> c.digitToInt() }.toList() }
//        .toList()
//}
