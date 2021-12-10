fun main() {

    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readDay11Input("Day11_test")
    println("testInput: $testInput")
    val input = readDay11Input("Day11")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 1234)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 1234)
    println("Part2: " + part2(input)) //
}

private fun readDay11Input(name: String): List<String> {
    return readInput(name).map { it.trim() }
}

//private fun readDay11Input(name: String): List<Int> {
//    return readInput(name).first().split(',').map { it.trim().toInt() }
//}

//private fun readDay11Input(name: String): List<Entry> {
//    return readInput(name)
//        .map { line -> line.split('|')
//            .let { (signalPatternsPart, outputsPart) ->
//                Entry(signalPatternsPart.splitToWords(), outputsPart.splitToWords())
//            }
//        }
//}

private fun String.splitToWords(): List<String> =
    split(' ').filter { it.isNotEmpty() }.toList()