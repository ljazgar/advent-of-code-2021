import java.math.BigInteger

fun main() {


    // PART 1 ******************************************************************************
    fun Packet.flatten(): List<Packet> {
        return when (this) {
            is LiteralPacket -> listOf(this)
            is OperatorPacket -> listOf(this) + this.subPackets.flatMap { subpacket -> subpacket.flatten() }
            else -> emptyList()
        }
    }

    fun part1(input: Day16Input): Int {
        val binaryInput = decodeHexToBin(input)
        println("Binary: $binaryInput")

        val reader = binaryInput.iterator()
        val packet = readPacket(reader)
        println("Packet: $packet")
        val packets = packet.flatten()
        return packets.sumOf { it.version }
    }

    // PART 2 ******************************************************************************
    fun operatorFunction(typeId: Int): (List<BigInteger>) -> BigInteger {
        return when(typeId) {
            0 -> { values: List<BigInteger> -> values.fold(BigInteger.ZERO) {acc, i -> acc + i } }
            1 -> { values: List<BigInteger> -> values.fold(BigInteger.ONE) {acc, i -> acc * i } }
            2 -> { values: List<BigInteger> -> values.minOf { it } }
            3 -> { values: List<BigInteger> -> values.maxOf { it } }
            5 -> { values: List<BigInteger> -> if (values.first() > values[1]) BigInteger.ONE else BigInteger.ZERO }
            6 -> { values: List<BigInteger> -> if (values.first() < values[1]) BigInteger.ONE else BigInteger.ZERO }
            7 -> { values: List<BigInteger> -> if (values.first() == values[1]) BigInteger.ONE else BigInteger.ZERO }
            else -> throw Error("Unknown operation type")
        }
    }

    fun Packet.eval(): BigInteger {
        return when (this) {
            is LiteralPacket -> this.value
            is OperatorPacket -> {
                val operatorFunction = operatorFunction(this.typeId)
                val subValues = this.subPackets.map { it.eval() }
                operatorFunction(subValues)
            }
            else -> throw error("Unknown packet")
        }
    }

    fun part2(input: Day16Input): BigInteger {
        val binaryInput = decodeHexToBin(input)
        val reader = binaryInput.iterator()
        val packet = readPacket(reader)
        println("Packet: $packet")
        return packet.eval()
    }

    // ***********************************************************************************

    val input = readDay16Input("Day16")
    println("input: $input")

    fun testPart1(input: String, expectedValue: Int) {
        println("testInput: $input")
        val part1TestResult = part1(input)
        println("Part1: Test Result: $part1TestResult Expected: $expectedValue")
        check( part1TestResult == expectedValue)
    }
    fun testPart2(input: String, expectedValue: BigInteger) {
        println("testInput: $input")
        val part1TestResult = part2(input)
        println("Part1: Test Result: $part1TestResult Expected: $expectedValue")
        check( part1TestResult == expectedValue)
    }
    // test if implementation meets criteria from the description:
//    part1("D2FE28")
//    part1("38006F45291200")
//    part1("EE00D40C823060")
    testPart1("8A004A801A8002F478", 16)
    testPart1("620080001611562C8802118E34", 12)
    testPart1("C0015000016115A2E0802F182340", 23)
    testPart1("A0016C880162017C3686B18A3D4780", 31)

    println("Part1: " + part1(input))

    testPart2("C200B40A82", 3.toBigInteger())
    testPart2("04005AC33890", 54.toBigInteger())
    testPart2("880086C3E88112", 7.toBigInteger())
    testPart2("CE00C43D881120", 9.toBigInteger())
    testPart2("D8005AC2A8F0", 1.toBigInteger())
    testPart2("F600BC2D8F", 0.toBigInteger())
    testPart2("9C005AC2F8F0", 0.toBigInteger())
    testPart2("9C0141080250320F1802104A08", 1.toBigInteger())

    println("Part2: " + part2(input))
}

typealias Day16Input = String
private fun readDay16Input(name: String): Day16Input {
    return readInput(name).first()
}

fun decodeHexToBin(hex: String): String {
    return hex.toCharArray().joinToString(separator = "") { hexChar ->
        when (hexChar) {
            '0' -> "0000"
            '1' -> "0001"
            '2' -> "0010"
            '3' -> "0011"
            '4' -> "0100"
            '5' -> "0101"
            '6' -> "0110"
            '7' -> "0111"
            '8' -> "1000"
            '9' -> "1001"
            'A' -> "1010"
            'B' -> "1011"
            'C' -> "1100"
            'D' -> "1101"
            'E' -> "1110"
            'F' -> "1111"
            else -> throw error("not hex digit")
        }
    }
}

abstract class Packet(val version: Int, val typeId: Int)
class LiteralPacket(version: Int, typeId: Int, val value: BigInteger) : Packet(version, typeId) {
    override fun toString() =
        "LiteralPacket(version=$version, typeId=$typeId, value=$value)"
}
class OperatorPacket(version: Int, typeId: Int, val subPackets: List<Packet>) : Packet(version, typeId) {
    override fun toString() =
        "OperatorPacket(version=$version, typeId=$typeId, subpackets=${subPackets})"
}

fun String.binaryToInt(): Int {
    return toCharArray().toList().fold(0) { acc, bit -> (acc shl 1) + bit.digitToInt()  }
}

fun String.binaryToBigInt(): BigInteger {
    return toCharArray().toList().fold(0.toBigInteger()) { acc, bit -> (acc shl 1) + bit.digitToInt().toBigInteger()  }
}

fun CharIterator.readString(length: Int): String {
    return (0 until length).map { this.nextChar() }.joinToString(separator = "")
}

fun readLiteralValue(reader: CharIterator): BigInteger {
    val fiveBitsChunks = mutableListOf<String>()
    do {
        val chunk = reader.readString(5)
        fiveBitsChunks.add(chunk)
    } while(chunk.first() == '1')
    val valueBinary = fiveBitsChunks.joinToString(separator = "") { it.substring(1, 5) }
    return valueBinary.binaryToBigInt()
}

fun readOperatorValue(reader: CharIterator): List<Packet> {
    val lengthType = reader.readString(1)
    return if (lengthType == "0") {
        val subPacketsLength = reader.readString(15).binaryToInt()
        val subPackets = reader.readString(subPacketsLength)
        val subPacketsReader = subPackets.iterator()
        val packets = mutableListOf<Packet>()
        while (subPacketsReader.hasNext()) {
            val packet = readPacket(subPacketsReader)
            packets.add(packet)
        }
        packets
    } else {
        val subPacketsNumber = reader.readString(11).binaryToInt()
        (1 .. subPacketsNumber).map { readPacket(reader) }
    }
}

fun readPacket(reader: CharIterator): Packet {
    val version = reader.readString(3).binaryToInt()
    val typeId = reader.readString(3).binaryToInt()
    return if (typeId == 4) {
        val value = readLiteralValue(reader)
        LiteralPacket(version, typeId, value)
    } else {
        val subPackets = readOperatorValue(reader)
        OperatorPacket(version, typeId, subPackets)
    }
}
