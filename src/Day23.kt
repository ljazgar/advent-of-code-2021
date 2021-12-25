import AmphipodType.*
import kotlin.math.abs
import kotlin.math.min

fun main() {

    // PART 1 ******************************************************************************
//    var counter = 0
//
//    fun evalMinCost(state: AmphipodsState): Long? {
//        if (state.locs.isFinish()) {
//            if (++counter % 10000 == 0) {
//                println("Visited Leaves: $counter")
//            }
//            println("Found final state: Cost: ${state.cost}")
//            return state.cost
//        }
//        val nextStates = state.nextStates()
//        if (nextStates.isEmpty()) {
//            if (++counter % 10000 == 0) {
//                println("Visited Leaves: $counter")
//            }
//            return null
//        }
//        val minCost = nextStates.mapNotNull { evalMinCost(it) }
//            .minByOrNull { it }
//
//        return minCost
//    }

    fun part1(input: LocsState): Long {
        val burrow = Burrow.generate(2)
        val initialState = AmphipodsState(input)
        input.print(burrow)

        val distances = mutableMapOf<AmphipodsState, Long>()
        distances[initialState] = 0L
        val distancesToGo = mutableMapOf<AmphipodsState, Long>()
        distancesToGo[initialState] = initialState.locsState.approxCostToGo(burrow)

        val visitedStates = mutableSetOf<AmphipodsState>()
        val notVisitedStates = mutableSetOf(initialState)
        var visitedAgainCounter = 0
        var globalMin = Long.MAX_VALUE
        while (notVisitedStates.isNotEmpty()) {
            val currState = notVisitedStates.minByOrNull { (distances[it] ?: Long.MAX_VALUE) + (distancesToGo[it] ?: Long.MAX_VALUE)} ?: throw error("Impossible: no min")
            val currStateDistance = distances[currState] ?: Long.MAX_VALUE
            if (currState.locsState.isFinish(burrow)) {
                globalMin = min(globalMin, currStateDistance)
            }

            if (visitedStates.size % 1000 == 0) {
                val visitedFinalStates = visitedStates.filter { it.locsState.isFinish(burrow) }
                val min = visitedFinalStates.firstOrNull()?.let { distances[it] }
                println("visited: ${visitedStates.size}  Queue size: ${notVisitedStates.size}  visitedAgain: $visitedAgainCounter visitedFinal:${visitedFinalStates.count()} min: $min  curDist: $currStateDistance ")
            }
            val nextStatesWithCost = currState.nextStates(burrow)
            nextStatesWithCost.forEach { (nextState, moveCost) ->
                val newDistanceCandidate = currStateDistance + moveCost
                val curDistance = distances[nextState] ?: Long.MAX_VALUE
                distances[nextState] = min(curDistance, newDistanceCandidate)

                var curDistanceToGo = distancesToGo[nextState]
                if (curDistanceToGo == null) {
                    curDistanceToGo = nextState.locsState.approxCostToGo(burrow)
                    distancesToGo[nextState] = curDistanceToGo
                }

                if (nextState !in visitedStates) {
                    if (curDistance + curDistanceToGo < globalMin) {
                        notVisitedStates.add(nextState)
                    }
                } else {
                    visitedAgainCounter++
                }
            }
            notVisitedStates.remove(currState)
            visitedStates.add(currState)
        }

        val finishState = distances.keys.find { it.locsState.isFinish(burrow) }
        val minCost = distances[finishState] ?: throw error("Impossible")
        return minCost
    }

    // PART 2 ******************************************************************************

    fun part2(input: LocsState): Int {
        return 0
    }

    // ***********************************************************************************
//    val test1 = startPositionTest.isFinish()
//    val test2 = testFinishStateCorrect.isFinish()
//    val test3 = testFinishStateCorrect2.isFinish()
//    val same = testFinishStateCorrect == testFinishStateCorrect2

    val testInput = startPositionTest
    val input = startPosition

    // test if implementation meets criteria from the description:
//    val part1TestResult = part1(testInput)
//    println("Part1: Test Result: $part1TestResult")
//    check( part1TestResult == 12521L)
    println("Part1")
    println("Part1: " + part1(input))
//
//    val part2TestResult = part2(testInput)
//    println("Part2: Test Result: $part2TestResult")
//    check( part2TestResult == 100000000)
//    println("Part2: " + part2(input))
}

private data class AmphipodLoc(val x: Int, val y: Int) {
    inline fun isRoom(): Boolean = y > 0
    inline fun isHall(): Boolean = y == 0
    inline fun roomNumber(): Int? = if (isRoom()) Burrow.xToRoomNumber(x) else null
}

private data class Burrow(val locs: Set<AmphipodLoc>, val roomDepth: Int) {
    companion object {
        val hallLength = 11
        val hallXs = 0 until hallLength
        val roomsNumber = 4
        val roomNumbers = (1..roomsNumber)
        val roomXs = roomNumbers.map { roomNumberToX(it) }
        fun xToRoomNumber(x: Int): Int = x/2
        fun roomNumberToX(roomNumber: Int): Int = roomNumber * 2
        fun isRoomEntrance(x: Int): Boolean = x in roomXs

        fun generate(roomDepth: Int): Burrow {
            val locs = mutableSetOf<AmphipodLoc>()
            // Hall
            hallXs
                .filterNot { x -> isRoomEntrance(x) }
                .forEach { x -> locs.add(AmphipodLoc(x, 0))}

            // Rooms
            (1..roomsNumber).forEach { room ->
                (1..roomDepth).forEach { y ->
                    locs.add(AmphipodLoc(roomNumberToX(room), y))
                }
            }
            return Burrow(locs, roomDepth)
        }

        fun roomLoc(roomNumber: Int, depth: Int) = AmphipodLoc(roomNumberToX(roomNumber), depth)
    }

    val movesMap = generateMovesCharacteristics()

    private fun stepsBetween(loc1: AmphipodLoc, loc2: AmphipodLoc): Int {
        return abs(loc1.x - loc2.x) + abs(loc1.y - loc2.y)
    }

    private fun locsBetween(roomLoc: AmphipodLoc, hallLoc: AmphipodLoc): Set<AmphipodLoc> {
        val locsBetween = mutableSetOf<AmphipodLoc>()
        for (y in roomLoc.y-1 downTo 1) {
            locsBetween.add(AmphipodLoc(roomLoc.x, y))
        }
        val xRange = if (roomLoc.x < hallLoc.x) roomLoc.x..hallLoc.x-1 else roomLoc.x downTo hallLoc.x+1
        for (x in xRange) {
            val loc = AmphipodLoc(x, 0)
            if (loc in locs) {
                locsBetween.add(loc)
            }
        }
        return locsBetween
    }

    fun generateMovesCharacteristics(): Map<Pair<AmphipodLoc, AmphipodLoc>, MoveCharacteristic> {
        val (roomLocs, hallLocs) = locs.partition { it.isRoom() }
        return roomLocs.asSequence()
            .flatMap { roomLoc ->
                hallLocs.asSequence().map { hallLoc ->
                    val steps = stepsBetween(roomLoc, hallLoc)
                    val locsBetween = locsBetween(roomLoc, hallLoc)
                    MoveCharacteristic(roomLoc, hallLoc, steps, locsBetween)
                }
            }.flatMap { mch ->
                listOf(
                    (mch.loc1 to mch.loc2) to mch,
                    (mch.loc2 to mch.loc1) to mch,
                )
            }.toMap()
    }

    val allDeeperInRoom = locs.filter { it.isRoom() }.map { it to getAllDeeperInRoom(it) }.toMap()

    private fun getAllDeeperInRoom(loc: AmphipodLoc): Set<AmphipodLoc> {
        return locs.asSequence().filter { it.x == loc.x && it.y > loc.y }.toSet()
    }

    val finishState: LocsState = locs
        .filter { it.isRoom() }
        .mapNotNull { loc ->
            AmphipodType.values()
                .find { a -> a.targetRoom == loc.roomNumber()}
                ?.let { a -> loc to a }
        }
        .toMap()

}


private enum class AmphipodType(val type: Char, val movingCost: Int, val targetRoom: Int) {
    A('A', 1, 1),
    B('B', 10, 2),
    C('C', 100, 3),
    D('D', 1000, 4),
}

private typealias LocsState = Map<AmphipodLoc, AmphipodType>

private data class AmphipodsState(val locsState: LocsState)

private fun LocsState.isFinish(burrow: Burrow): Boolean {
    return this == burrow.finishState
}

private enum class AmphipodStatus { RoomStart, Hall, RoomFinal }

private fun Amphipod.status(burrow: Burrow, locsState: LocsState): AmphipodStatus {
    return if (loc.isRoom()) {
         if (type.targetRoom == loc.roomNumber()) {
             val allDeeperCorrect = burrow.allDeeperInRoom[loc]
                 ?.all { l -> locsState[l]?.targetRoom == type.targetRoom } ?: throw error("Impossible: no deeper")
             if (allDeeperCorrect) {
                 AmphipodStatus.RoomFinal
             } else {
                 AmphipodStatus.RoomStart
             }
         } else {
             AmphipodStatus.RoomStart
         }
    } else {
        AmphipodStatus.Hall
    }
}

private data class Amphipod(val type: AmphipodType, val loc: AmphipodLoc)

private fun Amphipod.canGoTo(burrow: Burrow, targetLoc: AmphipodLoc, locsState: LocsState): MoveCharacteristic? {
    val currentLoc = this.loc
    if (locsState.containsKey(targetLoc)) return null

    val status = status(burrow, locsState)

    if (status == AmphipodStatus.RoomFinal) return null
    if (status == AmphipodStatus.RoomStart && targetLoc.isRoom()) return null
    if (status == AmphipodStatus.Hall) {
        if (Amphipod(type, targetLoc).status(burrow, locsState) != AmphipodStatus.RoomFinal) return null
    }

    val mch = burrow.movesMap[currentLoc to targetLoc] ?: return null
    if (!locsState.allPlacesFree(mch.locsBetween)) return null

    return mch
}

private fun Amphipod.canMove(burrow: Burrow, locsState: LocsState): Boolean {
    val status = status(burrow, locsState)
    return (status != AmphipodStatus.RoomFinal)
}

private fun AmphipodsState.move(amphipod: Amphipod, targetLoc: AmphipodLoc, mch: MoveCharacteristic): Pair<AmphipodsState, Long> {
    val newLocsState = this.locsState.toMutableMap()
    newLocsState.remove(amphipod.loc)
    newLocsState[targetLoc] = amphipod.type

    val cost = mch.steps.toLong() * amphipod.type.movingCost

    return AmphipodsState(newLocsState) to cost
}

private fun LocsState.getAllAmphipods(): Sequence<Amphipod> {
    return this.entries.asSequence().map { (loc, amphType) -> Amphipod(amphType, loc)}
}

private fun LocsState.approxCostToGo(burrow: Burrow): Long {
    return getAllAmphipods().map { amp ->
        val targetRoom = amp.type.targetRoom
        val steps = if (amp.loc.isRoom()) {
            val ampRoomNumber = amp.loc.roomNumber()
            if (ampRoomNumber == targetRoom) {
                0
            } else {
                amp.loc.y + abs(amp.loc.x - Burrow.roomNumberToX(targetRoom)) + 1
            }
        } else {
            abs(amp.loc.x - Burrow.roomNumberToX(targetRoom)) + 1
        }
        steps.toLong() * amp.type.movingCost
    }.sum()
}

private fun AmphipodsState.nextStates(burrow: Burrow): Set<Pair<AmphipodsState, Long>> {
    val allowedMoves = locsState.getAllAmphipods()
        .filter { amphipod -> amphipod.canMove(burrow, locsState) }
        .flatMap { amphipod -> burrow.locs.asSequence()
            .map { loc -> amphipod to loc }
        }
        .mapNotNull { (amphipod, targetLoc) ->
            amphipod.canGoTo(burrow, targetLoc, locsState)?.let { mch ->
                Triple(amphipod, targetLoc, mch)
            }
        }

    val nextStates = allowedMoves.map { (amphipod, loc, mch) -> this.move(amphipod, loc, mch) }.toSet()
    return nextStates
}


private fun LocsState.stringify(burrow: Burrow): String {
    return StringBuilder().apply() {
        for (y in 0..burrow.roomDepth) {
            for (x in Burrow.hallXs) {
                val loc = AmphipodLoc(x, y)
                val ch = when(loc) {
                    in burrow.locs -> get(loc)?.type ?: '.'
                    else -> ' '
                }
                append(ch)
            }
            appendLine()
        }
    }.toString()
}

private fun LocsState.print(burrow: Burrow) {
    println(stringify(burrow))
}

private val startPositionTest = mapOf(
    Burrow.roomLoc(1, 1) to B,
    Burrow.roomLoc(1, 2) to A,
    Burrow.roomLoc(2, 1) to C,
    Burrow.roomLoc(2, 2) to D,
    Burrow.roomLoc(3, 1) to B,
    Burrow.roomLoc(3, 2) to C,
    Burrow.roomLoc(4, 1) to D,
    Burrow.roomLoc(4, 2) to A,
)

private val startPosition = mapOf(
    Burrow.roomLoc(1, 1) to D,
    Burrow.roomLoc(1, 2) to C,
    Burrow.roomLoc(2, 1) to D,
    Burrow.roomLoc(2, 2) to C,
    Burrow.roomLoc(3, 1) to A,
    Burrow.roomLoc(3, 2) to B,
    Burrow.roomLoc(4, 1) to A,
    Burrow.roomLoc(4, 2) to B,
)

private val testFinish = mapOf(
    Burrow.roomLoc(1, 1) to A,
    Burrow.roomLoc(1, 2) to A,
    Burrow.roomLoc(2, 1) to B,
    Burrow.roomLoc(2, 2) to B,
    Burrow.roomLoc(3, 1) to C,
    Burrow.roomLoc(3, 2) to C,
    Burrow.roomLoc(4, 1) to D,
    Burrow.roomLoc(4, 2) to D,
)


//private fun LocsState.placesFree(vararg locs: AmphipodLoc): Boolean {
//    return locs.all { loc -> this[loc] == null }
//}
private fun LocsState.allPlacesFree(locs: Collection<AmphipodLoc>): Boolean {
    return locs.all { loc -> this[loc] == null }
}

//private fun placesFree(vararg locs: AmphipodLoc): (LocsState) -> Boolean {
//    return { locsState -> locsState.placesFree(locs.toList()) }
//}

private data class MoveCharacteristic(val loc1: AmphipodLoc, val loc2: AmphipodLoc, val steps: Int, val locsBetween: Set<AmphipodLoc> )

//private val movesCharacteristics = listOf(
//    MoveCharacteristic(CAVE1_DEEP, HALL_LEFT_DEEP, 4, placesFree(CAVE1_SHALLOW, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE1_DEEP, HALL_LEFT_SHALLOW, 3, placesFree(CAVE1_SHALLOW)),
//    MoveCharacteristic(CAVE1_DEEP, HALL_BETWEEN_1_2, 3, placesFree(CAVE1_SHALLOW)),
//    MoveCharacteristic(CAVE1_DEEP, HALL_BETWEEN_2_3, 5, placesFree(CAVE1_SHALLOW, HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE1_DEEP, HALL_BETWEEN_3_4, 7, placesFree(CAVE1_SHALLOW, HALL_BETWEEN_1_2, HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE1_DEEP, HALL_RIGHT_SHALLOW, 9, placesFree(CAVE1_SHALLOW, HALL_BETWEEN_1_2, HALL_BETWEEN_2_3, HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE1_DEEP, HALL_RIGHT_DEEP, 10, placesFree(CAVE1_SHALLOW, HALL_BETWEEN_1_2, HALL_BETWEEN_2_3, HALL_BETWEEN_3_4, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_LEFT_DEEP, 3, placesFree(HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_LEFT_SHALLOW, 2, placesFree()),
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_BETWEEN_1_2, 2, placesFree()),
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_BETWEEN_2_3, 4, placesFree(HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_BETWEEN_3_4, 6, placesFree(HALL_BETWEEN_1_2, HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_RIGHT_SHALLOW, 8, placesFree(HALL_BETWEEN_1_2, HALL_BETWEEN_2_3, HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE1_SHALLOW, HALL_RIGHT_DEEP, 9, placesFree(HALL_BETWEEN_1_2, HALL_BETWEEN_2_3, HALL_BETWEEN_3_4, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE2_DEEP, HALL_LEFT_DEEP, 6, placesFree(CAVE2_SHALLOW, HALL_BETWEEN_1_2, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE2_DEEP, HALL_LEFT_SHALLOW, 5, placesFree(CAVE2_SHALLOW, HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE2_DEEP, HALL_BETWEEN_1_2, 3, placesFree(CAVE2_SHALLOW)),
//    MoveCharacteristic(CAVE2_DEEP, HALL_BETWEEN_2_3, 3, placesFree(CAVE2_SHALLOW)),
//    MoveCharacteristic(CAVE2_DEEP, HALL_BETWEEN_3_4, 5, placesFree(CAVE2_SHALLOW, HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE2_DEEP, HALL_RIGHT_SHALLOW, 7, placesFree(CAVE2_SHALLOW, HALL_BETWEEN_2_3, HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE2_DEEP, HALL_RIGHT_DEEP, 8, placesFree(CAVE2_SHALLOW, HALL_BETWEEN_2_3, HALL_BETWEEN_3_4, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_LEFT_DEEP, 5, placesFree(HALL_BETWEEN_1_2, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_LEFT_SHALLOW, 4, placesFree(HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_BETWEEN_1_2, 2, placesFree()),
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_BETWEEN_2_3, 2, placesFree()),
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_BETWEEN_3_4, 4, placesFree(HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_RIGHT_SHALLOW, 6, placesFree(HALL_BETWEEN_2_3, HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE2_SHALLOW, HALL_RIGHT_DEEP, 7, placesFree(HALL_BETWEEN_2_3, HALL_BETWEEN_3_4, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE3_DEEP, HALL_LEFT_DEEP, 8, placesFree(CAVE3_SHALLOW, HALL_BETWEEN_2_3, HALL_BETWEEN_1_2, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE3_DEEP, HALL_LEFT_SHALLOW, 7, placesFree(CAVE3_SHALLOW, HALL_BETWEEN_2_3, HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE3_DEEP, HALL_BETWEEN_1_2, 5, placesFree(CAVE3_SHALLOW, HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE3_DEEP, HALL_BETWEEN_2_3, 3, placesFree(CAVE3_SHALLOW)),
//    MoveCharacteristic(CAVE3_DEEP, HALL_BETWEEN_3_4, 3, placesFree(CAVE3_SHALLOW)),
//    MoveCharacteristic(CAVE3_DEEP, HALL_RIGHT_SHALLOW, 5, placesFree(CAVE3_SHALLOW, HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE3_DEEP, HALL_RIGHT_DEEP, 6, placesFree(CAVE3_SHALLOW, HALL_BETWEEN_3_4, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_LEFT_DEEP, 7, placesFree(HALL_BETWEEN_2_3, HALL_BETWEEN_1_2, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_LEFT_SHALLOW, 6, placesFree(HALL_BETWEEN_2_3, HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_BETWEEN_1_2, 4, placesFree(HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_BETWEEN_2_3, 2, placesFree()),
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_BETWEEN_3_4, 2, placesFree()),
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_RIGHT_SHALLOW, 4, placesFree(HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE3_SHALLOW, HALL_RIGHT_DEEP, 5, placesFree(HALL_BETWEEN_3_4, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE4_DEEP, HALL_LEFT_DEEP, 10, placesFree(CAVE4_SHALLOW, HALL_BETWEEN_3_4, HALL_BETWEEN_2_3, HALL_BETWEEN_1_2, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE4_DEEP, HALL_LEFT_SHALLOW, 9, placesFree(CAVE4_SHALLOW, HALL_BETWEEN_3_4, HALL_BETWEEN_2_3, HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE4_DEEP, HALL_BETWEEN_1_2, 7, placesFree(CAVE4_SHALLOW, HALL_BETWEEN_3_4, HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE4_DEEP, HALL_BETWEEN_2_3, 5, placesFree(CAVE4_SHALLOW, HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE4_DEEP, HALL_BETWEEN_3_4, 3, placesFree(CAVE4_SHALLOW)),
//    MoveCharacteristic(CAVE4_DEEP, HALL_RIGHT_SHALLOW, 3, placesFree(CAVE4_SHALLOW)),
//    MoveCharacteristic(CAVE4_DEEP, HALL_RIGHT_DEEP, 4, placesFree(CAVE4_SHALLOW, HALL_RIGHT_DEEP)),
//
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_LEFT_DEEP, 9, placesFree(HALL_BETWEEN_3_4, HALL_BETWEEN_2_3, HALL_BETWEEN_1_2, HALL_LEFT_SHALLOW)),
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_LEFT_SHALLOW, 8, placesFree(HALL_BETWEEN_3_4, HALL_BETWEEN_2_3, HALL_BETWEEN_1_2)),
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_BETWEEN_1_2, 6, placesFree(HALL_BETWEEN_3_4, HALL_BETWEEN_2_3)),
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_BETWEEN_2_3, 4, placesFree(HALL_BETWEEN_3_4)),
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_BETWEEN_3_4, 2, placesFree()),
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_RIGHT_SHALLOW, 2, placesFree()),
//    MoveCharacteristic(CAVE4_SHALLOW, HALL_RIGHT_DEEP, 3, placesFree(HALL_RIGHT_DEEP)),
//)
//
//private fun generateMovesCharsMap(): Map<Pair<AmphipodLoc, AmphipodLoc>, MoveCharacteristic> {
//    return movesCharacteristics.flatMap { mch ->
//        listOf(
//            (mch.loc1 to mch.loc2) to mch,
//            (mch.loc2 to mch.loc1) to mch,
//        )
//    }.toMap()
//}
//
//private val movesCharsMap = generateMovesCharsMap()

