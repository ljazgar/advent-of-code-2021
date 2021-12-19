import kotlin.math.abs
import kotlin.math.max

fun main() {

    // PART 1 ******************************************************************************

    fun part1(scanners: Day19Input): Int {
        val absoluteBeacons = mutableSetOf<Location3d>()
        absoluteBeacons.addAll(scanners[0].beacons)

        val scannersLeft = mutableListOf<ScannerWithRotations>()
        scannersLeft.addAll(scanners.subList(1, scanners.size).evalRotations())

        loop@while (scannersLeft.isNotEmpty()) {
            println("Left: ${scannersLeft.size}")
            for (scanner in scannersLeft) {
                val res = absoluteBeacons.overlaps(scanner)
                if (res != null) {
                    val (vector, beacons) = res
                    val moved = beacons.map { it.add(vector) }
                    absoluteBeacons.addAll(moved)
                    scannersLeft.remove(scanner)
                    continue@loop
                }
            }
        }
        return absoluteBeacons.size
    }

    // PART 2 ******************************************************************************

    fun part2(scanners: Day19Input): Int {
        val absoluteBeacons = mutableSetOf<Location3d>()
        absoluteBeacons.addAll(scanners[0].beacons)
        val vectors = mutableSetOf<Vector3d>(Vector3d(0,0,0))

        val scannersLeft = mutableListOf<ScannerWithRotations>()
        scannersLeft.addAll(scanners.subList(1, scanners.size).evalRotations())

        loop@while (scannersLeft.isNotEmpty()) {
            println("Left: ${scannersLeft.size}")
            for (scanner in scannersLeft) {
                val res = absoluteBeacons.overlaps(scanner)
                if (res != null) {
                    val vector = res.first
                    val beacons = res.second
                    val moved = beacons.map { it.add(vector) }
                    vectors.add(vector)
                    absoluteBeacons.addAll(moved)
                    scannersLeft.remove(scanner)
                    continue@loop
                }
            }
        }

        println("Vectors: $vectors")

        var maxDistance = 0
        for (vector1 in vectors) {
            for(vector2 in vectors) {
                maxDistance = max(maxDistance, vector1.distance(vector2))
            }
        }

        return maxDistance
    }

    // ***********************************************************************************

    val testInput = readDay19Input("Day19_test")
//    println("testInput:")
//    testInput.print()

    val input = readDay19Input("Day19")
//    println("\ninput:")
//    input.print()

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 79)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 3621)
    println("Part2: " + part2(input))
}


private fun Beacons.overlaps(other: ScannerWithRotations): Pair<Vector3d, Beacons>? {
    for (rotatedBeaconsInOther in other.rotations) {
        this.overlaps(rotatedBeaconsInOther.beacons)?.let { vector ->
            return Pair(vector, rotatedBeaconsInOther.beacons)
        }
    }
    return null
}

/**
 * Returns vector how others has to be moved to this
 */
private fun Beacons.overlaps(others: Beacons): Vector3d? {
    val vectors = this.asSequence().flatMap { beaconInThese ->
        others.asSequence().map { beaconInOthers ->
            beaconInOthers.vectorTo(beaconInThese)
        }
    }
    return vectors.firstOrNull { vector ->
        others.asSequence()
            .map { it.add(vector) }
            .filter { it in this }
            .mapIndexed { i, _ -> i + 1 }
            .any { count -> count >= 12  }
    }
}

private data class Location3d(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "[$x,$y,$z]"
}
private data class Vector3d(val x: Int, val y: Int, val z: Int)

private fun Location3d.add(v: Vector3d) = Location3d(x+v.x, y+v.y, z+v.z)
private fun Location3d.vectorTo(l: Location3d) = Vector3d(l.x - x, l.y-y, l.z-z)
private fun Vector3d.distance(v: Vector3d): Int = abs(x - v.x) + abs(y - v.y) + abs(z - v.z)


private typealias Beacons = Set<Location3d>

private data class Scanner(val id: Int, val beacons: Beacons) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Scanner $id:\n")
        beacons.forEach { sb.append("$it\n") }
        return sb.toString()
    }
}

private data class RotatedBeacons(val rotationIndex: Int, val rotation: Transformation, val beacons: Beacons)
private data class ScannerWithRotations(val id: Int, val rotations: List<RotatedBeacons>)

private fun Scanner.evalRotations(): ScannerWithRotations =
    ScannerWithRotations(id, beacons.rotations())

private fun List<Scanner>.evalRotations(): List<ScannerWithRotations> =
    map { it.evalRotations()}

private fun Beacons.rotations(): List<RotatedBeacons> {
    return rotations.mapIndexed { i, rotation ->
        val beacons = this.asSequence().map(rotation).toSet()
        RotatedBeacons(i, rotation, beacons)
    }
}

private typealias Transformation = (Location3d) -> Location3d

private val rotations = listOf<Transformation>(
    // up: +Z
    { l -> l },
    { l -> with(l) { Location3d(y, -x, z) }}, //Z
    { l -> with(l) { Location3d(-l.x, -y, z) }}, //ZZ
    { l -> with(l) { Location3d(-y, x, z) }},  //ZZZ

    // front: X + Y
    { l -> with(l) { Location3d(x, z, -y) }},   // X
    { l -> with(l) { Location3d(-y, z, -x) }},  // XY
    { l -> with(l) { Location3d(-x, z, y) }},   // XYY
    { l -> with(l) { Location3d(y, z, x) }},    // XYYY

    // back: XXX + Y
    { l -> with(l) { Location3d(x, -z, y) }},  // XXX
    { l -> with(l) { Location3d(y, -z, -x) }},  // XXX Y
    { l -> with(l) { Location3d(-x, -z, -y) }},  // XXX YY
    { l -> with(l) { Location3d(-y, -z, x) }},  // XXX YY

    // left: Y + X
    { l -> with(l) { Location3d(-z, y, x) }},  // Y
    { l -> with(l) { Location3d(-z, x, -y) }},  // Y X
    { l -> with(l) { Location3d(-z, -y, -x) }},  // Y XX
    { l -> with(l) { Location3d(-z, -x, y) }},  // Y XXX

    // right: YYY + X
    { l -> with(l) { Location3d(z, y, -x) }},  // YYY
    { l -> with(l) { Location3d(z, -x, -y) }},  // YYY X
    { l -> with(l) { Location3d(z, -y, x) }},  // YYY XX
    { l -> with(l) { Location3d(z, x, y) }},  // YYY XXX

    // down: XX + Z
    { l -> with(l) { Location3d(x, -y, -z) }}, // XX
    { l -> with(l) { Location3d(-y, -x, -z) }}, // XX Z
    { l -> with(l) { Location3d(-x, y, -z) }}, // XX ZZ
    { l -> with(l) { Location3d(y, x, -z) }}, // XX ZZZ
)


private typealias Day19Input = List<Scanner>

private fun List<Scanner>.print() {
    forEach { println(it) }
}

private fun readDay19Input(name: String): Day19Input {
    val lines = readInput(name)
    val scanners = mutableListOf<Scanner>()
    var scannerBeacons = mutableListOf<Location3d>()
    var scannerIndex: Int = -1
    for (line in lines) {
        if (line.isEmpty()) {
            continue
        }
        if (line.startsWith("--- scanner")) {
            if (scannerIndex >= 0) {
                scanners.add(Scanner(scannerIndex, scannerBeacons.toSet()))
            }
            scannerIndex++
            scannerBeacons = mutableListOf()
            continue
        }
        val location = line.split(",").map { it.toInt() }.let { (x, y, z) -> Location3d(x, y, z)}
        scannerBeacons.add(location)
    }
    if (scannerIndex >= 0) {
        scanners.add(Scanner(scannerIndex, scannerBeacons.toSet()))
    }

    return scanners
}

