import kotlin.math.max

enum class LaunchResult { IN_TARGET, OVERSHOOT, UNDERSHOOT}

fun main() {

    data class Rectangle(val minX: Int, val minY: Int, val maxX: Int, val maxY: Int)
    data class Point(val x: Int, val y: Int)

    fun Point.isIn(r: Rectangle): Boolean = x >= r.minX && x <= r.maxX && y >= r.minY && y <= r.maxY
    fun Point.isBehind(r: Rectangle): Boolean = x > r.maxX || y < r.minY
    data class Velocity(val x: Int, val y: Int)

    fun Velocity.nextStep(): Velocity {
        val newX = if (x > 0) x - 1 else if (x < 0) x + 1 else 0
        val newY = y - 1
        return Velocity(newX, newY)
    }

    fun Point.nextStep(v: Velocity): Point = Point(x + v.x, y + v.y)

    data class State(val point: Point, val velocity: Velocity)

    fun State.nextStep(): State = State(point.nextStep(velocity), velocity.nextStep())

    data class LaunchResultWithHeight(val result: LaunchResult, val maxHeight: Int)

    fun launchProbe(v: Velocity, targetArea: Rectangle): LaunchResultWithHeight {
        var state = State(Point(0, 0), v)
        var maxHeight = 0
        do {
            state = state.nextStep()
            maxHeight = max(maxHeight, state.point.y)
        } while (!state.point.isBehind(targetArea) && !state.point.isIn(targetArea))

        val result =
            if (state.point.isIn(targetArea)) { LaunchResult.IN_TARGET }
            else {
                if (state.point.x <= targetArea.maxX) { LaunchResult.UNDERSHOOT }
                else { LaunchResult.OVERSHOOT }
            }
        return LaunchResultWithHeight(result, maxHeight)
    }

    fun maxXForVelocity(initialVX: Int): Int {
        var vx = initialVX
        var x = 0
        do {
            x += vx
            vx--
        } while (vx > 0)
        return x
    }

    fun findXVelocities(minX: Int, maxX: Int): List<Int> {
        var vx = 1
        val goodVelocities = mutableListOf<Int>()
        while (maxXForVelocity(vx) < minX) {
            vx++
        }
        while (maxXForVelocity(vx) <= maxX) {
            goodVelocities.add(vx)
            vx++
        }
        return goodVelocities
    }
    // PART 1 ******************************************************************************

    fun part1(targetArea: Rectangle): Int {
//        val result = launchProbe(Velocity(7, 2), targetArea)
//        val result2 = launchProbe(Velocity(6, 3), targetArea)
//        val result3 = launchProbe(Velocity(9, 0), targetArea)
//        val result4 = launchProbe(Velocity(17, -4), targetArea)
//        val result5 = launchProbe(Velocity(6, 9), targetArea)

        val goodXVelocities = findXVelocities(targetArea.minX, targetArea.maxX)

        val velocities = goodXVelocities.asSequence().flatMap { vx ->
            (0 .. 10000).asSequence().map { vy -> Velocity(vx, vy) }
        }
        val inTarget = velocities.map { v -> Pair(v, launchProbe(v, targetArea)) }
            .filter { r -> r.second.result == LaunchResult.IN_TARGET }
            .toList()
        return inTarget.maxOf { r -> r.second.maxHeight }
    }

    // PART 2 ******************************************************************************

    fun readResult(): List<Velocity> {
        return readInput("Day17_test").flatMap {
            it.split(" ").filterNot { it.isEmpty() }.map { s ->
                val splitted = s.split(",")
                Velocity(splitted[0].toInt(), splitted[1].toInt())
            }
        }
    }

    fun part2(targetArea: Rectangle): Int {
        val result5 = launchProbe(Velocity(6, 0), targetArea)
        val result6 = launchProbe(Velocity(7, -1), targetArea)

        val minXVelocity = findXVelocities(targetArea.minX, targetArea.maxX).minOf { it }

        val velocities = (minXVelocity .. targetArea.maxX).asSequence().flatMap { vx ->
            (targetArea.minY .. 100).asSequence().map { vy -> Velocity(vx, vy) }
        }

        val inTarget = velocities.map { v -> Pair(v, launchProbe(v, targetArea)) }
            .filter { r -> r.second.result == LaunchResult.IN_TARGET }
            .map { r -> r.first}
            .toList()

        println("Velocities: $inTarget")

        val testResult = readResult()

        return inTarget.size
    }


    // ***********************************************************************************

    val testInput = Rectangle(20, -10, 30, -5)
    println("testInput: $testInput")

    val input = Rectangle(192, -89, 251, -59)
    println("input: $input")

    // test if implementation meets criteria from the description:
//    val part1TestResult = part1(testInput)
//    println("Part1: Test Result: $part1TestResult")
//    check( part1TestResult == 45)
//
//    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 112)
    println("Part2: " + part2(input))
}


