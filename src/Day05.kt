import kotlin.math.abs

fun main() {
    fun part1(input: List<Line>): Int {
        return input
            .filter { it.isHorizontal() || it.isVertical() }
            .flatMap { line -> line.getPoints() }
            .groupBy { it }
            .map { entry -> entry.value.size }
            .filter { it > 1 }
            .count()
    }

    fun part2(input: List<Line>): Int {
        return input
            .flatMap { line -> line.getPoints() }
            .groupBy { it }
            .map { entry -> entry.value.size }
            .filter { it > 1 }
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readVentsInput("Day05_test")
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 5)

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 12)

    val input = readVentsInput("Day05")
    println("Part1: " + part1(input)) // 6225
    println("Part2: " + part2(input)) // 22116
}

private data class Point(val x: Int, val y: Int)

private data class Line(val from: Point, val to: Point) {
    fun isHorizontal(): Boolean = from.y == to.y
    fun isVertical(): Boolean = from.x == to.x
    fun isDiagonal(): Boolean = abs(from.x - to.x) == abs(from.y - to.y)

    fun getPoints(): List<Point> {
        if (isHorizontal()) {
            return range(from.x, to.x).map { x -> Point(x, from.y) }
        } else if (isVertical()) {
            return range(from.y, to.y).map { y -> Point(from.x, y) }
        } else if (isDiagonal()) {
            val xRange = range(from.x, to.x).toList()
            val yRange = range(from.y, to.y).toList()
            return xRange.mapIndexed() { i, x -> Point(x, yRange[i]) }
        } else {
            return emptyList()
        }
    }

    private fun range(a1: Int, a2: Int) = if (a1 < a2) a1 .. a2 else a1 downTo a2
}

private fun String.toPoint(): Point {
    val (x, y) = trim().split(',')
    return Point(x.toInt(), y.toInt())
}

private fun String.toLine(): Line {
    val (fromStr, toStr) = trim().split("->")
    return Line(fromStr.toPoint(), toStr.toPoint())
}

private fun readVentsInput(name: String): List<Line> {
    return readInput(name).map { it.toLine() }
}
