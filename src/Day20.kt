fun main() {

    // PART 1 ******************************************************************************

    fun enhancementStep(pixelsState: PixelsState, algorithm: List<Boolean>): PixelsState {

        val allNeighbours = pixelsState.pixelsMap.keys.asSequence().flatMap { p -> p.neighbours() }.toSet()
        val enhancedPixels = allNeighbours
            .map { n -> n to n.enhancePixel(pixelsState, algorithm) }
            .toMap()

        val newOthers = if (pixelsState.othersOn) algorithm[511] else algorithm[0]
        return PixelsState(enhancedPixels, newOthers)
    }

    fun part1(input: Day20Input): Int {
        val algorithm = input.algorithm
        println("Init:")
        printPixels(input.pixelsState)
        println()

        val step1 = enhancementStep(input.pixelsState, algorithm)
        println("After step 1:")
        printPixels(step1)
        println()

        val step2 = enhancementStep(step1, algorithm)
        println("After step 2:")
        printPixels(step2)
        println()

        return step2.countOn()
    }

    // PART 2 ******************************************************************************

    fun part2(input: Day20Input): Int {
        val algorithm = input.algorithm
        var pixelsState = input.pixelsState

        repeat(50) { i ->
            pixelsState = enhancementStep(pixelsState, algorithm)
            println("After step $i: ${pixelsState.countOn()}")
        }
        return pixelsState.countOn()
    }

    // ***********************************************************************************

    val testInput = readDay20Input("Day20_test")
//    println("testInput: $testInput")
    val input = readDay20Input("Day20")
//    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 35)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 3351)
    println("Part2: " + part2(input))
}

private data class Pixel(val x: Int, val y: Int)
private data class Day20Input(val algorithm: List<Boolean>, val pixelsState: PixelsState)
private data class PixelsState(val pixelsMap: Map<Pixel, Boolean>, val othersOn: Boolean = false)

private fun Pixel.isOn(pixelsState: PixelsState): Boolean {
    return pixelsState.pixelsMap[this] ?: pixelsState.othersOn
}

private fun Pixel.enhancePixel(pixelsState: PixelsState, algorithm: List<Boolean>): Boolean {
    val algorithmIndex = this.neighbours().asSequence()
        .map { n -> n.isOn(pixelsState) }
        .binaryToInt()

    return algorithm[algorithmIndex]
}

private fun PixelsState.countOn(): Int {
    return pixelsMap.asSequence().mapNotNull { p -> if (p.value) p.key else null }.count()
}

fun Sequence<Boolean>.binaryToInt(): Int {
    return fold(0) { acc, bit -> (acc shl 1) + if (bit) 1 else 0  }
}

private fun Pixel.neighbours(): Iterable<Pixel> {
    return Iterable { iterator {
        yield(Pixel(x - 1, y - 1))
        yield(Pixel(x, y - 1))
        yield(Pixel(x + 1, y - 1))
        yield(Pixel(x - 1, y))
        yield(Pixel(x, y))
        yield(Pixel(x + 1, y))
        yield(Pixel(x - 1, y + 1))
        yield(Pixel(x, y + 1))
        yield(Pixel(x + 1, y + 1))
    } }
}

private fun parseAlgorithm(s: String): List<Boolean> {
    return s.toCharArray().map { c ->
        when(c) {
            '#' -> true
            '.' -> false
            else -> throw error("Invalid character in algorithm")
        }
    }.toList()
}

private fun readDay20Input(name: String): Day20Input {
    val lines = readInput(name)
    val templateStr = lines.first()
    val algorithm = parseAlgorithm(templateStr)

    val pixels = lines.subList(2, lines.size)
        .flatMapIndexed { y, line ->
            line.toCharArray().mapIndexed { x, c ->
                when (c) {
                    '#' -> Pixel(x, y) to true
                    '.' -> Pixel(x, y) to false
                    else -> throw error("Invalid character in image")
                }
            }
        }.toMap()
    return Day20Input(algorithm, PixelsState(pixels))
}

private fun printPixels(pixelsState: PixelsState) {
    val pixels = pixelsState.pixelsMap.keys
    val minX = pixels.minOf { it.x - 1 }
    val maxX = pixels.maxOf { it.x + 1 }
    val minY = pixels.minOf { it.y - 1 }
    val maxY = pixels.maxOf { it.y + 1 }

    for (y in minY .. maxY) {
        for (x in minX .. maxX) {
            val isOn = pixelsState.pixelsMap[Pixel(x, y)] ?: pixelsState.othersOn
            val g = if (isOn) '#' else '.'
            print(g)
        }
        println()
    }
    println("Others: " + if (pixelsState.othersOn) '#' else '.')
}

private fun testEnhancing(input: Day20Input): Int {

    val pixels = setOf(
        Pixel(1, 2),
        Pixel(2, 3),
        Pixel(0, 0),
        Pixel(0, 1),
        Pixel(0, 2),
        Pixel(3, 0),
        Pixel(3, 4),
        Pixel(2, 4),
        Pixel(4, 2),
        Pixel(4, 4),
    )
    val pixelsState = PixelsState(pixels.map { p -> p to true}.toMap())
    val ep = Pixel(2, 2).enhancePixel(pixelsState, input.algorithm)
    return 0
}
