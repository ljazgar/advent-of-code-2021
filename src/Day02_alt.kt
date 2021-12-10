fun main() {
    fun part1(commands: List<Command>): Int {
        val finalLocation = commands.fold(Location()) { cur, command ->
            when (command.direction) {
                "forward" -> Location(cur.horizontal + command.distance, cur.depth)
                "up"      -> Location(cur.horizontal,                       cur.depth - command.distance)
                "down"    -> Location(cur.horizontal,                       cur.depth + command.distance)
                else -> cur
            }
        }
        return finalLocation.horizontal * finalLocation.depth
    }

    fun part2(commands: List<Command>): Int {
        val finalLocation = commands.fold(State()) { cur, command ->
            when (command.direction) {
                "down"    -> State(cur.location, cur.aim + command.distance)
                "up"      -> State(cur.location, cur.aim - command.distance)
                "forward" -> State(Location(
                    cur.location.horizontal + command.distance,
                    cur.location.depth + cur.aim * command.distance
                ), cur.aim)
                else -> cur
            }
        }.location

        return finalLocation.horizontal * finalLocation.depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readCommandsInput("Day02_test")
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 150)

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 900)

    val input = readCommandsInput("Day02")
    println("Part1: " + part1(input)) // 1947824
    println("Part2: " + part2(input)) // 1813062561
}

private data class Command(val direction: String, val distance: Int)

private fun String.toCommand() : Command =
    split(' ')
        .let { (direction, distanceStr) -> Command(direction, distanceStr.toInt()) }

private fun readCommandsInput(name: String): List<Command> {
    return readInput(name).map { it.toCommand() }
}

data class Location(val horizontal: Int = 0, val depth: Int = 0)

data class State(val location: Location = Location(), val aim: Int = 0)