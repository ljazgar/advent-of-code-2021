fun main() { // To rewrite

    fun mostCommonBits(input: List<String>): List<Int> {
        val bitsNumber = input[0].length
        val numbersCount = input.size
        val oneCounters = MutableList(bitsNumber) { 0 }

        input.forEach { binaryNumber ->
            for (i in 0 until bitsNumber) {
                if (binaryNumber[i] == '1') {
                    oneCounters[i]++
                }
            }
        }

        val mostCommonBits = oneCounters.map{ oneCounter ->
            if (oneCounter >= numbersCount.toFloat() / 2) 1 else 0
        }

        return mostCommonBits
    }

    fun List<Int>.bitsListToInt(): Int {
        return fold(0) { acc, bit -> (acc shl 1) + bit  }
    }

    fun part1(input: List<String>): Int {
        val mostCommonBits = mostCommonBits(input)

        val gamma = mostCommonBits.bitsListToInt()

        val leastCommonBits = mostCommonBits.map { if (it == 1) 0 else 1 }
        val epsilon = leastCommonBits.bitsListToInt()

        println("Gamma: $gamma Epsilon: $epsilon")
        return gamma * epsilon
    }

    fun oxygen(input: List<String>): Int {
        val bitsNumber = input[0].length

        var filtered = input
        for (bitIndex in 0 until bitsNumber) {
            val mostCommonBits = mostCommonBits(filtered)
            filtered = filtered.filter {
                it[bitIndex].digitToInt() == mostCommonBits[bitIndex]
            }
            if (filtered.size == 1) break
        }
        if (filtered.isEmpty()) {
            error("Filtered everything")
        }
        return Integer.parseInt(filtered.first(), 2)
    }

    fun co2(input: List<String>): Int {
        val bitsNumber = input[0].length

        var filtered = input
        for (bitIndex in 0 until bitsNumber) {
            val mostCommonBits = mostCommonBits(filtered)
            filtered = filtered.filter {
                it[bitIndex].digitToInt() != mostCommonBits[bitIndex]
            }
            if (filtered.size == 1) break
        }
        if (filtered.size == 0) {
            error("Filtered everything")
        }
        return Integer.parseInt(filtered.first(), 2)
    }

    fun part2(input: List<String>): Int {
        val oxygen = oxygen(input)
        val co2 = co2(input)

        println("Oxygen: $oxygen CO2: $co2 ")

        return oxygen * co2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 198)

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 230)

    val input = readInput("Day03")
    println("Part1: " + part1(input)) // 4118544
    println("Part2: " + part2(input))
}
