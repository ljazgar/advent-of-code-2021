fun main() {

    // PART 1 ******************************************************************************

    fun part1(input: DayTTInput): Int {
        return 0
    }

    // PART 2 ******************************************************************************

    fun part2(input: DayTTInput): Int {
        return 0
    }

    // ***********************************************************************************

    val testInput = readDayTTInput("DayTT_test")
    println("testInput: $testInput")
    val input = readDayTTInput("DayTT")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 100000000)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 100000000)
    println("Part2: " + part2(input))
}

//typealias DayTTInput = List<String>
//private fun readDay10Input(name: String): DayTTInput {
//    return readInput(name).map { it.trim() }
//}

typealias DayTTInput = List<Int>
private fun readDayTTInput(name: String): DayTTInput {
    return readInput(name).first().split(',').map { it.trim().toInt() }
}


//typealias DayTTInput = List<List<Int>>
//private fun readDayTTInput(name: String): DayTTInput {
//    return readInput(name)
//        .map { it.trim() }
//        .map { it.toCharArray().map { c -> c.digitToInt() }.toList() }
//        .toList()
//}
//private fun List<List<Int>>.print() {
//    val sizeY = size
//    val sizeX = first().size
//    (0 until sizeY).forEach() { y ->
//        (0 until sizeX).forEach() { x ->
//            print(this[y][x])
//        }
//        println()
//    }
//}


//typealias DayTTInput = List<Pair<String, String>>
//private fun readDayTTInput(name: String): DayTTInput {
//    return readInput(name).map { it.split("-").let { (a, b) -> Pair(a, b) } }.toList()
//}

//private data class Dot2(val x: Int, val y: Int)
//private data class Fold2(val direction: Char, val line: Int)
//private data class DayTTInput(val dots: List<Dot2>, val folds: List<Fold2>)
//private fun readDayTTInput(name: String): DayTTInput {
//    val lines = readInput(name)
//    var dotsSection = true
//    val dots = mutableListOf<Dot2>()
//    val folds = mutableListOf<Fold2>()
//    for (line in lines) {
//        if (line.trim().isEmpty()) {
//            dotsSection = false
//            continue
//        }
//        if (dotsSection) {
//            val dot = line.split(",").map { it.toInt() }.let { (x, y) -> Dot2(x, y)}
//            dots.add(dot)
//        } else {
//            val fold = line.substring(11).split("=").let { (a, b) -> Fold2(a.first(), b.toInt())}
//            folds.add(fold)
//        }
//    }
//    return DayTTInput(dots, folds)
//}

//private data class DayTTInput(val template: String, val rules: Map<String,Char>)
//private fun readDayTTInput(name: String): DayTTInput {
//    val lines = readInput(name)
//    val template = lines.first()
//
//    val rules = lines.subList(2, lines.size)
//        .map { line ->
//            line.split(" -> ").let { (a, b) -> Pair(a.trim(), b.trim().first())}
//        }.toMap()
//    return DayTTInput(template, rules)
//}
