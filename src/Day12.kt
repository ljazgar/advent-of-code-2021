fun main() {

    fun createGraph(input: List<Pair<String, String>>): Map<String, List<String>> {
        val reversePasses = input.map { p -> Pair(p.second, p.first) }
        return (input + reversePasses).groupBy({ it.first }, {it.second })
    }

    fun String.isBigCave() = this.toCharArray().all { c -> c.isUpperCase() }

    // PART 1 ******************************************************************************
    fun isPassCorrect(currentPath: List<String>, nextCave: String): Boolean {
        val smallCaves = currentPath.filterNot { it.isBigCave() }
        return nextCave !in smallCaves
    }

    fun countCompletePathsStartingWith(graph: Map<String, List<String>>, path: List<String>): Int {
        if (path.last() == "end") {
            return 1
        }
        val nextCaves = graph[path.last()] ?: emptyList()
        return nextCaves
            .filter { nextCave -> isPassCorrect(path, nextCave)  }
            .map { path + it }
            .map { countCompletePathsStartingWith(graph, it) }
            .sum()
    }

    fun part1(input: List<Pair<String, String>>): Int {
        val graph = createGraph(input)
        return countCompletePathsStartingWith(graph, listOf("start"))
    }

    // PART 2 ******************************************************************************

    fun isPassCorrect2(currentPath: List<String>, nextCave: String): Boolean {
        if (nextCave == "start") {
            return false
        }
        val smallCaves = currentPath.filterNot { it.isBigCave() }
        val smallCavesVisits = smallCaves.groupingBy { it }.eachCount()

        return smallCavesVisits.all { it.value <= 2 }
            && smallCavesVisits.count { it.value >= 2 } <= 1
    }

    fun countCompletePathsStartingWith2(graph: Map<String, List<String>>, path: List<String>): Int {
        if (path.last() == "end") {
            return 1
        }
        val nextCaves = graph[path.last()] ?: emptyList()
        return nextCaves
            .filter { nextCave -> isPassCorrect2(path, nextCave)  }
            .map { path + it }
            .map { countCompletePathsStartingWith2(graph, it) }
            .sum()
    }

    fun part2(input: List<Pair<String, String>>): Int {
        val graph = createGraph(input)
        return countCompletePathsStartingWith2(graph, listOf("start"))
    }

    // ***********************************************************************************

    val testInput = readDay12Input("Day12_test")
    println("testInput: $testInput")
    val input = readDay12Input("Day12")
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 10
    )
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 36)
    println("Part2: " + part2(input)) //
}

private fun readDay12Input(name: String): List<Pair<String, String>> {
    return readInput(name).map { it.split("-").let { (a, b) -> Pair(a, b) } }.toList()
}
