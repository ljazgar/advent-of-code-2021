import kotlin.math.max

fun main() {

    // PART 1 ******************************************************************************

    fun part1(input: List<SnailFishNumber>): Long {
        val sum = input.sum()
        return sum.magnitude()
    }

    // PART 2 ******************************************************************************

    fun part2(input: List<SnailFishNumber>): Long {

        var currentMax = Long.MIN_VALUE
        for (i in input.indices) {
            for (j in input.indices) {
                val mag = input[i].plus(input[j]).reduce().magnitude()
                currentMax = max(mag, currentMax)
            }
        }
        return currentMax
    }

    // ***********************************************************************************

    // Sum test
    val sumTestInput = readDay18Input("Day18_sumtest")
    println("sumtestInput:")
    sumTestInput.print()
    val sumResult = sumTestInput.sum()
    println("sumtestResult: $sumResult")
    check(sumResult.toString() == "[[[[5,0],[7,4]],[5,5]],[6,6]]")

    // Sum test 2
    val sumTestInput2 = readDay18Input("Day18_sumtest2")
    println()
    println("sumtestInput2:")
    sumTestInput.print()
    val sumResult2 = sumTestInput2.sum()
    println("sumtestResult2: $sumResult2")
    check(sumResult2.toString() == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")

    // Magnitude test
    val magnitude = parseSnailFishNumber("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude()
    check(magnitude == 3488L)

    val testInput = readDay18Input("Day18_test3")
    println("testInput:")
    testInput.print()
    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 4140L)

    val input = readDay18Input("Day18")
    println()
//    println("Input:")
//    input.print()
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println()
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 3993L)
    println("Part2: " + part2(input))
}

abstract class Number {
    abstract fun magnitude(): Long
}
private class RegularNumber(val value: Int) : Number() {
    override fun toString() = "$value"
    override fun magnitude(): Long = value.toLong()
}
private class SnailFishNumber(val first: Number, val second: Number) : Number() {
    override fun toString() = "[$first,$second]"
    override fun magnitude(): Long {
        return first.magnitude() * 3 + second.magnitude() * 2
    }
}

private fun findCommaIndex(string: String): Int {
    var nestLevel = 0
    for (i in 0..string.length) {
        val c = string[i]
        if (c=='[') {
            nestLevel++
        } else if (c==']') {
            nestLevel--
        } else if (c==',' && nestLevel==0) {
            return i
        }
    }
    throw error("Parsing error: No comma in pair")
}

private fun parseNumber(string: String): Number {
    return if (string.first().isDigit()) {
        RegularNumber(string.toInt())
    } else if (string.first() == '[' && string.last() == ']') {
        val sub = string.substring(1, string.length - 1)
        val commaIndex = findCommaIndex(sub)
        val firstStr = sub.substring(0, commaIndex)
        val secondStr = sub.substring(commaIndex + 1, sub.length)
        val first = parseNumber(firstStr)
        val second = parseNumber(secondStr)
        SnailFishNumber(first, second)
    } else {
        throw error("Parse error: Invalid characters")
    }
}

private fun parseSnailFishNumber(string: String): SnailFishNumber {
    val number = parseNumber(string)
    if (number is SnailFishNumber) {
        return number
    } else {
        throw error("Parse error: result is not snailfish number")
    }
}


private fun Number.encode(address: String = ""): List<Pair<String, Int>> {
    return when (this) {
        is RegularNumber -> listOf(address to value)
        is SnailFishNumber -> first.encode(address + "0") + second.encode(address + "1")
        else -> throw error("Encoding error")
    }
}
private fun firstDiffIndex(fromAddress: String, toAddress: String): Int {
    return fromAddress.indices.firstOrNull { i -> i >= toAddress.length || fromAddress[i] != toAddress[i] }
        ?: throw error("Error find diff in addresses. Impossible")
}

private fun transition(fromAddress: String, toAddress: String): String {
    if (fromAddress.isEmpty()) {
        val sb = StringBuilder()
        repeat(toAddress.length) { sb.append('[') }
        return sb.toString()
    }

    val sb = StringBuilder()
    val firstDiffIndex = firstDiffIndex(fromAddress, toAddress)
    repeat(fromAddress.length - firstDiffIndex - 1) { sb.append(']') }
    sb.append(",")
    repeat(toAddress.length - firstDiffIndex - 1) { sb.append('[') }
    return sb.toString()
}

private fun List<Pair<String, Int>>.decode(): SnailFishNumber {
    var currentAddress = ""
    val sb = StringBuilder()
    for (code in this) {
        val newAddress = code.first
        val transition = transition(currentAddress, newAddress)
        sb.append(transition)
        sb.append(code.second)
        currentAddress = newAddress
    }
    repeat(currentAddress.length) { sb.append(']') }
    val sfNumberAsString = sb.toString()
    return parseSnailFishNumber(sfNumberAsString)
}


private fun SnailFishNumber.tryExplode(): Pair<Boolean, SnailFishNumber> {
    val encoded = encode().toMutableList()
    val firstToExplodeIndex = encoded.indexOfFirst { it.first.length > 4 }
    if (firstToExplodeIndex < 0) {
        return Pair(false, this)
    }

    val explodingPairFirstAddress = encoded[firstToExplodeIndex].first
    val explodingPairAddress = explodingPairFirstAddress.substring(0, explodingPairFirstAddress.length - 1)
    val explodingPairFirst = encoded[firstToExplodeIndex].second
    val explodingPairSecond = encoded[firstToExplodeIndex+1].second

    encoded.removeAt(firstToExplodeIndex)
    encoded.removeAt(firstToExplodeIndex)
    encoded.add(firstToExplodeIndex, Pair(explodingPairAddress, 0) )

    if (firstToExplodeIndex>0) {
        val prev = encoded[firstToExplodeIndex - 1]
        encoded[firstToExplodeIndex - 1] = Pair(prev.first, prev.second + explodingPairFirst)
    }
    if (firstToExplodeIndex + 1 < encoded.size) {
        val prev = encoded[firstToExplodeIndex + 1]
        encoded[firstToExplodeIndex + 1] = Pair(prev.first, prev.second + explodingPairSecond)
    }

    val result = encoded.decode()
    return Pair(true, result)
}

private fun SnailFishNumber.trySplit(): Pair<Boolean, SnailFishNumber> {
    val encoded = encode().toMutableList()
    val firstToSplitIndex = encoded.indexOfFirst { it.second >= 10 }
    if (firstToSplitIndex < 0) {
        return Pair(false, this)
    }

    val splittingNumberAddress = encoded[firstToSplitIndex].first
    val splittingNumber = encoded[firstToSplitIndex].second

    val newPairFirst = splittingNumber / 2
    val newPairSecond = splittingNumber - newPairFirst

    encoded.removeAt(firstToSplitIndex)
    encoded.addAll(firstToSplitIndex, listOf(
        splittingNumberAddress + "0" to newPairFirst,
        splittingNumberAddress + "1" to newPairSecond
    ))

    val result = encoded.decode()
    return Pair(true, result)
}

private fun SnailFishNumber.reduce(): SnailFishNumber {
    var current = this
    do {
        val explodeResult = current.tryExplode()
        if (explodeResult.first) {
            current = explodeResult.second
            continue
        }
        val splitResult = current.trySplit()
        if (splitResult.first) {
            current = splitResult.second
            continue
        }
        if (!explodeResult.first && !splitResult.first) {
            break
        }
    } while (true)
    return current
}

private fun SnailFishNumber.plus(toAdd: SnailFishNumber): SnailFishNumber {
    return SnailFishNumber(this, toAdd)
}

private fun List<SnailFishNumber>.sum(): SnailFishNumber {
    var sum = this.first()
    for (i in 1 until this.size) {
        sum = sum.plus(this[i]).reduce()
//        println("Sum $i: $sum")
    }
    return sum
}


private fun readDay18Input(name: String): List<SnailFishNumber> {
    return readInput(name).map { s -> parseSnailFishNumber(s) }
}

private fun List<SnailFishNumber>.print() {
    forEach { println(it) }
}


private fun encodingTest(input: List<SnailFishNumber>) {
    println("Encoding")
    input.forEach {
        println("SFNumber: $it")
        val encoded = it.encode()
        println("Encoded: $encoded")
        val decoded = encoded.decode()
        println("Decoded: $decoded")
        if (it.toString() != decoded.toString()) {
            throw error("Encoding test failed")
        }
    }
}
