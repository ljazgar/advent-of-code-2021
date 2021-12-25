fun main() {

    // PART 1 ******************************************************************************

    fun CocumberField.stepEast(): CocumberField {
        val newCocumberField = mutableListOf<MutableList<Char>>()

        // move east-facing
        for (y in this.indices) {
            val originalLine = this[y]
            val newLine = mutableListOf<Char>()
            for (x in originalLine.indices) {
                val westX = (x + originalLine.size - 1) % originalLine.size
                val eastX = (x + 1) % originalLine.size
                if (originalLine[westX] == '>' && originalLine[x] == '.') {
                    newLine.add(x, '>')
                } else if (originalLine[x] == '>' && originalLine[eastX] == '.') {
                    newLine.add(x, '.')
                } else {
                    newLine.add(x, originalLine[x])
                }
            }
            newCocumberField.add(newLine)
        }
        return newCocumberField
    }

    fun CocumberField.stepSouth(): CocumberField {
        val newCocumberField = mutableListOf<MutableList<Char>>()
        for (y in this.indices) {
            newCocumberField.add(MutableList(this.first().size) { ' ' })
        }

        // move south-facing
        for (x in this.first().indices) {
            for (y in this.indices) {
                val southY = (y + 1) % this.size
                val northY = (y + this.size - 1) % this.size
                if (this[northY][x] == 'v' && this[y][x] == '.') {
                    newCocumberField[y][x] = 'v'
                } else if (this[y][x] == 'v' && this[southY][x] == '.') {
                    newCocumberField[y][x] = '.'
                } else {
                    newCocumberField[y][x] = this[y][x]
                }
            }
        }

        return newCocumberField
    }

    fun CocumberField.step(): CocumberField {
        val stepEast = this.stepEast()
        return stepEast.stepSouth()
    }
    fun part1(input: CocumberField): Int {
        var steps = 0
        var state = input
        var previousState: CocumberField = mutableListOf<MutableList<Char>>()
        do {
            val newState = state.step()
            previousState = state
            state = newState
            steps++

            println("After step: $steps")
            state.print()
            println()
        } while(state != previousState)
        return steps
    }

    // PART 2 ******************************************************************************

    fun part2(input: CocumberField): Int {
        return 0
    }

    // ***********************************************************************************

    val testInput = readDay25Input("Day25_test")
//    println("testInput: $testInput")
    testInput.print()
    val input = readDay25Input("Day25")
//    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 58)
    println("Part1: " + part1(input))
//
//    val part2TestResult = part2(testInput)
//    println("Part2: Test Result: $part2TestResult")
//    check( part2TestResult == 100000000)
//    println("Part2: " + part2(input))
}


typealias CocumberField = MutableList<MutableList<Char>>
private fun readDay25Input(name: String): CocumberField {
    return readInput(name)
        .map { it.trim() }
        .map { it.toCharArray().toMutableList() }
        .toMutableList()
}
private fun CocumberField.print() {
    val sizeY = size
    val sizeX = first().size
    (0 until sizeY).forEach() { y ->
        (0 until sizeX).forEach() { x ->
            print(this[y][x])
        }
        println()
    }
}

