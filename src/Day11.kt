fun main() {

    fun copy(input: List<List<Int>>): MutableList<MutableList<Int>> {
        return input.map { it.toMutableList() }.toMutableList()
    }

    data class Point(val x: Int, val y: Int)

    fun neighbours(point: Point, sizeX: Int = 10, sizeY: Int = 10): Iterable<Point> {
        return Iterable { iterator {
            yield(Point(point.x - 1, point.y - 1))
            yield(Point(point.x, point.y - 1))
            yield(Point(point.x + 1, point.y - 1))
            yield(Point(point.x - 1, point.y))
            yield(Point(point.x + 1, point.y))
            yield(Point(point.x - 1, point.y + 1))
            yield(Point(point.x, point.y + 1))
            yield(Point(point.x + 1, point.y + 1))
        } }
            .filterNot { p -> p.x < 0 || p.y < 0 || p.x >= sizeX || p.y >= sizeY  }
    }

    fun print(octopuses: List<List<Int>>) {
        octopuses.forEach { println(it) }
        println()
    }

    fun step(octopuses: MutableList<MutableList<Int>>): Int {
        // increase all
        val flashPoints = mutableListOf<Point>()
        for (y in 0 until 10) {
            for (x in 0 until 10) {
                if (++octopuses[y][x] == 10) {
                    flashPoints.add(Point(x, y))
                }
            }
        }

        // Induce neighbours
        var index = 0
        while (index < flashPoints.size) {
            for (n in neighbours(flashPoints[index])) {
                if (++octopuses[n.y][n.x] == 10) {
                    flashPoints.add(n)
                }
            }
            index++
        }

        // turn off flashes
        for (y in 0 until 10) {
            for (x in 0 until 10) {
                if (octopuses[y][x] >= 10) {
                    octopuses[y][x] = 0
                }
            }
        }
        return flashPoints.size
    }

    fun part1(input: List<List<Int>>): Int {
        val octopuses = copy(input)
        var sum = 0
        for (i in 1 .. 100) {
            val flashes = step(octopuses)
            sum += flashes
//            println("Step $i")
//            print(input)
//            println("Flashes: $flashes")
        }
        return sum
    }

    fun part2(input: List<List<Int>>): Int {
        val octopuses = copy(input)
        for (i in 1 .. 100000) {
            step(octopuses)
            if (octopuses.all { it.all { it == 0 } }) {
                return i
            }
        }
        return -1
    }

    val testInput = readDay11Input("Day11_test")
    println("testInput: $testInput")
    val input = readDay11Input("Day11")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 1656)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 195)
    println("Part2: " + part2(input)) //
}

private fun readDay11Input(name: String): List<List<Int>> {
    return readInput(name)
        .map { it.trim() }
        .map { it.toCharArray().map { c -> c.digitToInt() }.toList() }
        .toList()
}
