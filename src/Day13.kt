fun main() {

    fun doFold(dots: Set<Dot>, fold: Fold): Set<Dot> {
        return dots
            .map { dot ->
                if (fold.direction == 'x') {
                    val newX = if (dot.x > fold.line) fold.line * 2 - dot.x else dot.x
                    Dot(newX, dot.y)
                } else {
                    val newY = if (dot.y > fold.line) fold.line * 2 - dot.y else dot.y
                    Dot(dot.x, newY)
                }
            }
            .toSet()
    }

    // PART 1 ******************************************************************************

    fun part1(input: Day13Input): Int {
        val fold = input.folds.first()
        return doFold(input.dots.toSet(), fold).size
    }

    // PART 2 ******************************************************************************

    fun part2(input: Day13Input) {
        val final = input.folds.fold(input.dots.toSet()) { acc, fold -> doFold(acc, fold) }

        val linesOfX = final.groupBy({ it.y }, { it.x })
        val maxX = final.map { it.x }.maxOf { it }
        val maxY = final.map { it.y }.maxOf { it }

        for (y in 0..maxY) {
            val xs = linesOfX[y] ?: emptyList()
            for (x in 0..maxX) {
                val c = if (x in xs) '#' else ' '
                print(c)
            }
            println();
        }
    }

    // ***********************************************************************************

    val testInput = readDay13Input("Day13_test")
    println("testInput: $testInput")
    val input = readDay13Input("Day13")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 17)
    println("Part1: " + part1(input))

    println("Part2: Test:")
    part2(testInput)
    println("Part2: ")
    part2(input)
}

private data class Dot(val x: Int, val y: Int)
private data class Fold(val direction: Char, val line: Int)

private data class Day13Input(val dots: List<Dot>, val folds: List<Fold>)

private fun readDay13Input(name: String): Day13Input {
    val lines = readInput(name)
    var dotsSection = true
    val dots = mutableListOf<Dot>()
    val folds = mutableListOf<Fold>()
    for (line in lines) {
        if (line.trim().isEmpty()) {
            dotsSection = false
            continue
        }
        if (dotsSection) {
            val dot = line.split(",").map { it.toInt() }.let { (x, y) -> Dot(x, y)}
            dots.add(dot)
        } else {
            val fold = line.substring(11).split("=").let { (a, b) -> Fold(a.first(), b.toInt())}
            folds.add(fold)
        }
    }
    return Day13Input(dots, folds)
}
