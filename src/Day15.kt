import kotlin.math.min

fun main() {

    data class Point(val x: Int, val y: Int)

    fun neighbours(point: Point, sizeX: Int, sizeY: Int): List<Point> {
        val points = mutableListOf<Point>()
        if (point.x > 0) points.add(Point(point.x - 1, point.y))
        if (point.y > 0) points.add(Point(point.x, point.y - 1))
        if (point.x < sizeX - 1 ) points.add(Point(point.x + 1, point.y))
        if (point.y < sizeY - 1 ) points.add(Point(point.x, point.y + 1))
        return points.toList()
    }

    class PointsQueue(cave: Cave, val distances: List<MutableList<Int>>) {
        val allPointsIterable =
            (0 until cave.sizeX()).flatMap { x ->
                (0 until cave.sizeY()).map { y -> Point(x, y) }
            }

        val visitedPoints = allPointsIterable
                .asSequence()
                .filter { distances[it.y][it.x] < Int.MAX_VALUE }
                .toMutableList()

        fun size() = visitedPoints.size

        fun removeMinimal(): Point {
            visitedPoints.sortByDescending { p -> distances[p.y][p.x] }
            return visitedPoints.removeLast()
        }

        fun setIfLower(p: Point, value: Int) {
            val curNeighbourDistance = distances[p.y][p.x]
            distances[p.y][p.x] = min(curNeighbourDistance, value)
            if (curNeighbourDistance == Int.MAX_VALUE) {
                visitedPoints.add(p)
            }
        }
    }

    fun findMinimalRisk(cave: Cave) : Int {
        val distances = List(cave.sizeY()) { MutableList(cave.sizeX()) { Int.MAX_VALUE} }
        distances[0][0] = 0

        val pointsQueue = PointsQueue(cave, distances)
        while (pointsQueue.size() > 0) {
            val minPoint = pointsQueue.removeMinimal()
            val distanceToPoint = distances[minPoint.y][minPoint.x]
            neighbours(minPoint, cave.sizeX(), cave.sizeY()).forEach { n ->
                val distanceByMinPoint = distanceToPoint + cave.get(n.x, n.y)
                pointsQueue.setIfLower(n, distanceByMinPoint)
            }
        }

        return distances[cave.sizeY() - 1][cave.sizeX() - 1]
    }

    // PART 1 ******************************************************************************

    fun part1(input: Day15Input): Int {
        val cave = Cave(input)
        return findMinimalRisk(cave)
    }


    // PART 2 ******************************************************************************

    fun part2(input: Day15Input): Int {
        val cave = BiggerCave(input)
//        cave.print()
        return findMinimalRisk(cave)
    }

    // ***********************************************************************************

    val testInput = readDay15Input("Day15_test")
    println("testInput: $testInput")
    val input = readDay15Input("Day15")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 40)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 315)
    println("Part2: " + part2(input))
}

private open class Cave(protected val input: Day15Input) {
    open fun sizeX() = input.first().size
    open fun sizeY() = input.size
    open fun get(x: Int, y: Int): Int = input[y][x]
}

private fun Cave.print() {
    (0 until sizeY()).forEach() { y ->
        (0 until sizeX()).forEach() { x ->
            print("${get(x, y)}")
        }
        println()
    }
}

private class BiggerCave(input: Day15Input) : Cave(input) {
    val tileSizeX = super.sizeX()
    val tileSizeY = super.sizeY()

    override fun sizeX() = tileSizeX * 5
    override fun sizeY() = tileSizeY * 5
    override fun get(x: Int, y: Int): Int {
        val tileX = x / tileSizeX
        val xInTile = x % tileSizeX
        val tileY = y / tileSizeY
        val yInTile = y % tileSizeY

        val v = super.input[xInTile][yInTile] + tileX + tileY
        val result = if (v <= 9) v else v - 9
        return result
    }
}

private typealias Day15Input = List<List<Int>>
private fun readDay15Input(name: String): Day15Input {
    return readInput(name)
        .map { it.trim() }
        .map { it.toCharArray().map { c -> c.digitToInt() }.toList() }
        .toList()
}
