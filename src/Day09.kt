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

    fun findLowPoints(map: Array<IntArray>): List<Point> {
        val sizeX = map.first().size
        val sizeY = map.size

        val allPoints = (0 until sizeX).flatMap { x ->
            (0 until sizeY).map { y ->
                Point(x, y)
            }
        }
        return allPoints.filter { p ->
            neighbours(p, sizeX, sizeY).all { map[p.y][p.x] < map[it.y][it.x] }
        }
    }

    fun part1(map: Array<IntArray>): Int {
        val lowPoints = findLowPoints(map)
        return lowPoints.map { p -> map[p.y][p.x] + 1 }.sum()
    }

    fun findBasin(startPoint: Point, map: Array<IntArray>): List<Point> {
        val sizeX = map.first().size
        val sizeY = map.size

        val accLowPoints = MutableList(1) { startPoint }
        var index = 0
        while (index < accLowPoints.size) {
            val neighbours = neighbours(accLowPoints[index], sizeX, sizeY)
            val newNeighbours = neighbours
                .filterNot { accLowPoints.contains(it) }
                .filter { map[it.y][it.x] != 9 }
            accLowPoints.addAll(newNeighbours)
            index++
        }
        return accLowPoints
    }

    fun part2(map: Array<IntArray>): Int {
        val lowPoints = findLowPoints(map)
        val basins = lowPoints.map { findBasin(it, map) }

        val basinSizesDesc = basins.map { it.size }.sortedDescending()
        return basinSizesDesc[0] * basinSizesDesc[1] * basinSizesDesc[2]
    }


    val testInput = readDay10Input("Day09_test")
    val input = readDay10Input("Day09")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 15)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 1134)
    println("Part2: " + part2(input))
}

private fun readDay10Input(name: String): Array<IntArray> {
    return readInput(name)
        .filter { it.isNotEmpty() }
        .map { line -> line.trim() }
        .map { line ->
            line.toCharArray().map { c -> c.digitToInt() }.toIntArray()
        }.toTypedArray()
}
