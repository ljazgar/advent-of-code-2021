import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

fun main() {

    // PART 1 ******************************************************************************
    fun Cuboid.in50(): Boolean {
        return xRange.first>=-50 && xRange.endInclusive <=50
            && yRange.first>=-50 && yRange.endInclusive <=50
            && zRange.first>=-50 && zRange.endInclusive <=50
    }
    fun part1(input: Day22Input): Int {
        val commands = input.filter { it.cuboid.in50()}

        val turnedOn = mutableSetOf<Loc>()
        for( (i, command) in commands.withIndex()) {

            for(x in command.cuboid.xRange) {
                for(y in command.cuboid.yRange) {
                    for(z in command.cuboid.zRange) {
                        val loc = Loc(x,y,z)
                        if (command.on) {
                            turnedOn.add(loc)
                        } else {
                            turnedOn.remove(loc)
                        }
                    }
                }
            }
            println("Command $i on:${turnedOn.size}")
        }

        return turnedOn.size
    }

    // PART 2 ******************************************************************************
    fun part2(input: Day22Input): BigInteger {
        // Tests
        val cuboid = Cuboid(IntRange(0,3), IntRange(0,3), IntRange(0,3))
        val splitInternal = cuboid.split(Cuboid(IntRange(1,2), IntRange(1,2), IntRange(1,2))).toList()
        val splitCorner = cuboid.split(Cuboid(IntRange(2,5), IntRange(2,5), IntRange(2,5))).toList()
        val splitEqual = cuboid.split(Cuboid(IntRange(0,3), IntRange(0,3), IntRange(0,3))).toList()

        // on, not overlapping cuboids
        var space = emptySet<Cuboid>()

        for ((i, command) in input.withIndex()) {
            val spaceAfter = space.addNewCuboid(command.on, command.cuboid)
            space = spaceAfter
            println("Command $i: cuboids: ${space.size} on: ${space.sumOf { it.capacity() }}")
        }

        return space.sumOf { it.capacity() }
    }

    // ***********************************************************************************
//    val cub1 = Cuboid(xRange=0..3, yRange=0..3, zRange=0..3)
//    val cub2 = Cuboid(xRange=-1..4, yRange=-1..4, zRange=1..2)
//    val overlapsTest1 = cub1.overlaps(cub2)
//    val splitTest = cub1.split(cub2).toList()
//    val overlapsTest2 = Cuboid(xRange=-84658..-71047, yRange=3894..22190, zRange=11351..30230)
//        .overlaps(Cuboid(xRange=-91660..-67049, yRange=7713..17902, zRange=3072..13054))

//    val cub1 = Cuboid(xRange=-20..26, yRange=-36..-30, zRange=-47..-39)
//    val cub2 = Cuboid(xRange=-46..7, yRange=-6..46, zRange=-50..-1)
//    val candidates = cub1.split27Candidates(cub2).toList()
//    val splitResult = cub1.split(cub2).toList()

    val testInput = readDay22Input("Day22_test")
    println("testInput: $testInput")
    val input = readDay22Input("Day22")
    println("input: $input")

    // test if implementation meets criteria from the description:
//    val part1TestResult = part1(testInput)
//    println("Part1: Test Result: $part1TestResult")
//    check( part1TestResult == 590784)
//    println("Part1: " + part1(input))
//
    val testInput2 = readDay22Input("Day22_test2")
    println("testInput: $testInput2")

    val part2TestResult = part2(testInput2)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == BigInteger("2758514936282235"))
//                                               2869751671842346
    println("Part2: " + part2(input))
}

private data class Loc(val x: Int, val y: Int, val z: Int)
private data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange)
private data class ReactorCommand(val on: Boolean, val cuboid: Cuboid)
private typealias Space = Set<Cuboid>
private typealias Day22Input = List<ReactorCommand>
private fun readDay22Input(name: String): Day22Input {
    return readInput(name)
        .map { parseCommand(it.trim()) }
        .toList()
}
private fun parseCommand(line: String): ReactorCommand {
    val (onOffPart, cuboidPart) = line.split(" ")
    val on = when(onOffPart) {
        "on" -> true
        "off" -> false
        else -> throw error("parsing error on/off: $onOffPart")
    }
    val cuboid = parseCuboid(cuboidPart)
    return ReactorCommand(on, cuboid)
}

private fun parseRange(s:String): IntRange {
    val (fromStr, toStr) = s.split("..")
    return IntRange(fromStr.toInt(), toStr.toInt())
}
private fun parseCuboid(s: String): Cuboid {
    val (xPart, yPart, zPart) = s.split(",")
    val xRange = parseRange(xPart.substring(2, xPart.length))
    val yRange = parseRange(yPart.substring(2, yPart.length))
    val zRange = parseRange(zPart.substring(2, zPart.length))
    return Cuboid(xRange, yRange, zRange)
}

private fun IntRange.cropTo(o: IntRange): IntRange {
    val maxStart = max(start, o.start)
    val minEnd = min(endInclusive, o.endInclusive)
    return IntRange(maxStart, minEnd)
}


private fun Cuboid.vertices(): Iterable<Loc> {
    return Iterable { iterator {
        yield(Loc(xRange.start,        yRange.start,        zRange.start))
        yield(Loc(xRange.endInclusive, yRange.start,        zRange.start))
        yield(Loc(xRange.endInclusive, yRange.endInclusive, zRange.start))
        yield(Loc(xRange.start,        yRange.endInclusive, zRange.start))
        yield(Loc(xRange.start,        yRange.start,        zRange.endInclusive))
        yield(Loc(xRange.endInclusive, yRange.start,        zRange.endInclusive))
        yield(Loc(xRange.endInclusive, yRange.endInclusive, zRange.endInclusive))
        yield(Loc(xRange.start,        yRange.endInclusive, zRange.endInclusive))
    } }
}
private fun Cuboid.contains(loc: Loc): Boolean {
    return xRange.contains(loc.x)
        && yRange.contains(loc.y)
        && zRange.contains(loc.z)
}
private fun Cuboid.isInside(o: Cuboid): Boolean {
    return o.xRange.contains(xRange.start) && o.xRange.contains(xRange.endInclusive)
        && o.yRange.contains(yRange.start) && o.yRange.contains(yRange.endInclusive)
        && o.zRange.contains(zRange.start) && o.zRange.contains(zRange.endInclusive)
}
private fun IntRange.intersection(o: IntRange): IntRange? {
    val range = IntRange(max(this.start, o.start), min(this.endInclusive, o.endInclusive))
    return if (range.isEmpty()) null else range
}

private fun Cuboid.overlaps(other: Cuboid): Boolean {
    return this.xRange.intersection(other.xRange) != null
        && this.yRange.intersection(other.yRange) != null
        && this.zRange.intersection(other.zRange) != null
}

private data class SplitResult(val overlapping: Cuboid, val notOverlapping: Collection<Cuboid>)


// Asumption: overlaps
private fun Cuboid.splitAdvanced(newCuboid: Cuboid): SplitResult {
    val cuboids = split(newCuboid).toList()
    val overlapping = cuboids[0]
    val notOverlapping = cuboids.subList(1, cuboids.size)
    return SplitResult(overlapping, notOverlapping)
}

private fun Set<Cuboid>.addNewCuboid(on: Boolean, newCuboid: Cuboid): Space {
//    val wrongCuboid = Cuboid(xRange=-84658..-71047, yRange=12744..22190, zRange=11351..20917)
    val newSpace = mutableSetOf<Cuboid>()
    for (cuboid in this) {
//        if (newSpace.any { it.overlaps(cuboid)}) {
//            println("NewSpace has overlaping ")
//        }
        if (cuboid.overlaps(newCuboid)) {
            val splitRes = cuboid.splitAdvanced(newCuboid)
//            if (splitRes.notOverlapping.countOverlapping() > 0) {
//                println("Not overlapping overlaps")
//            }
//            if (splitRes.notOverlapping.any { c -> !c.isInside(cuboid) }) {
//                println("Wrong split. Result no inside")
//            }
//            if (splitRes.notOverlapping.contains(wrongCuboid)) {
//                println("Discovered wrong cuboid")
//            }
            newSpace.addAll(splitRes.notOverlapping)
//            if (newSpace.countOverlapping() > 0) {
//                println("After adding split result not overlapping overlaps")
//            }
        } else {
            newSpace.add(cuboid)
        }
    }
    if (on) {
        newSpace.add(newCuboid)
//        if (newSpace.countOverlapping() > 0) {
//            println("After adding new Cuboid overlaps")
//        }
    }
    return newSpace
}
private fun Cuboid.capacity(): BigInteger {
    val xSize = xRange.endInclusive - xRange.start + 1
    val ySize = yRange.endInclusive - yRange.start + 1
    val zSize = zRange.endInclusive - zRange.start + 1
    return xSize.toBigInteger() * ySize.toBigInteger() * zSize.toBigInteger()
}


private fun Cuboid.split(o: Cuboid): Sequence<Cuboid> {
    return split27Candidates(o).asSequence()
        .filterNot { c -> c.xRange.isEmpty() || c.yRange.isEmpty() || c.zRange.isEmpty() }
        .map { c -> Cuboid(
            c.xRange.cropTo(this.xRange),
            c.yRange.cropTo(this.yRange),
            c.zRange.cropTo(this.zRange)
        ) }
        .filterNot { c -> c.xRange.isEmpty() || c.yRange.isEmpty() || c.zRange.isEmpty() }
}

//private fun Collection<Cuboid>.countOverlapping(): Int {
//    var counter = 0
//    for (cuboid1 in this) {
//        for (cuboid2 in this) {
//            if (cuboid1 != cuboid2) {
//                if (cuboid1.overlaps(cuboid2)) {
//                    counter++
//                }
//            }
//        }
//    }
//    return counter
//}


private fun Cuboid.split27Candidates(o: Cuboid): Iterable<Cuboid> {
    return Iterable { iterator {
        yield(Cuboid(o.xRange,     o.yRange,        o.zRange))
        //--------- Plane 1
        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            IntRange(yRange.start, o.yRange.start - 1),
            IntRange(zRange.start, o.zRange.start - 1)))
        yield(Cuboid(
            o.xRange,
            IntRange(yRange.start, o.yRange.start - 1),
            IntRange(zRange.start, o.zRange.start - 1)))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            IntRange(yRange.start, o.yRange.start - 1),
            IntRange(zRange.start, o.zRange.start - 1)))

        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            o.yRange,
            IntRange(zRange.start, o.zRange.start - 1)))
        yield(Cuboid(
            o.xRange,
            o.yRange,
            IntRange(zRange.start, o.zRange.start - 1)))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            o.yRange,
            IntRange(zRange.start, o.zRange.start - 1)))

        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            IntRange(zRange.start, o.zRange.start - 1)))
        yield(Cuboid(
            o.xRange,
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            IntRange(zRange.start, o.zRange.start - 1)))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            IntRange(zRange.start, o.zRange.start - 1)))

        //--------- Plane 2
        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            IntRange(yRange.start, o.yRange.start - 1),
            o.zRange
        ))
        yield(Cuboid(
            o.xRange,
            IntRange(yRange.start, o.yRange.start - 1),
            o.zRange
        ))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            IntRange(yRange.start, o.yRange.start - 1),
            o.zRange
        ))

        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            o.yRange,
            o.zRange
        ))
//        yield(Cuboid(
//            o.xRange,
//            o.yRange,
//            o.zRange
//        ))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            o.yRange,
            o.zRange
        ))

        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            o.zRange
        ))
        yield(Cuboid(
            o.xRange,
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            o.zRange
        ))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            o.zRange
        ))

        //--------- Plane 3
        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            IntRange(yRange.start, o.yRange.start - 1),
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
        yield(Cuboid(
            o.xRange,
            IntRange(yRange.start, o.yRange.start - 1),
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            IntRange(yRange.start, o.yRange.start - 1),
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))

        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            o.yRange,
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
        yield(Cuboid(
            o.xRange,
            o.yRange,
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            o.yRange,
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))

        yield(Cuboid(
            IntRange(xRange.start, o.xRange.start - 1),
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
        yield(Cuboid(
            o.xRange,
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
        yield(Cuboid(
            IntRange(o.xRange.endInclusive + 1, xRange.endInclusive),
            IntRange(o.yRange.endInclusive + 1, yRange.endInclusive),
            IntRange(o.zRange.endInclusive + 1, zRange.endInclusive)
        ))
    } }
}
