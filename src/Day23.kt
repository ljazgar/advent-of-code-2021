package day23

import day23.AmphipodType.*
import kotlin.math.abs
import kotlin.math.min

fun main() {

    fun findMinCost(initialState: BurrowState, burrow: Burrow): Int {
        initialState.print(burrow)
        val finalState = burrow.finalState()

        val stateReachCost = mutableMapOf<BurrowState, Int>()
        stateReachCost[initialState] = 0

        val visitedStates = mutableSetOf<BurrowState>()
        val notVisitedStates = mutableListOf(initialState)
        notVisitedStates.add(initialState)

        var visitedAgainCounter = 0
        var visitedFinalCounter = 0
        var minToFinal = Int.MAX_VALUE

        while (notVisitedStates.isNotEmpty()) {
            val currState = notVisitedStates.last()
            notVisitedStates.removeLast()
            visitedStates.add(currState)

            val currStateReachCost = stateReachCost[currState]!!
            if (currState == finalState) {
                minToFinal = min(minToFinal, currStateReachCost)
                visitedFinalCounter++
                continue
            }

            if (visitedStates.size % 10000 == 0) {
                println("visited: ${visitedStates.size}  Queue size: ${notVisitedStates.size}  visitedAgain: $visitedAgainCounter visitedFinal:$visitedFinalCounter minToFinal: $minToFinal  curCost: $currStateReachCost ")
            }

            val nextStatesWithCost = currState.nextStates(burrow)

            nextStatesWithCost.forEach { (nextState, moveCost) ->
                val nextStateReachCost = currStateReachCost + moveCost
                val prevCost = stateReachCost[nextState]
                if (prevCost == null || nextStateReachCost < prevCost) {
                    stateReachCost[nextState] = nextStateReachCost
                }

                if (nextState !in visitedStates || nextStateReachCost < (prevCost ?: Int.MAX_VALUE)) {
                    notVisitedStates.add(nextState)
                } else {
                    visitedAgainCounter++
                }
            }
        }

        println("visited: ${visitedStates.size}  Queue size: ${notVisitedStates.size}  visitedAgain: $visitedAgainCounter visitedFinal:$visitedFinalCounter minToFinal: $minToFinal ")

        return stateReachCost[finalState] ?: throw error("Impossible")
    }

    // PART 1 ******************************************************************************
    fun part1(initialState: BurrowState): Int {
        return findMinCost(initialState, Burrow(2))
    }

    // PART 2 ******************************************************************************

    fun part2(initialState: BurrowState): Int {
        return findMinCost(initialState, Burrow(4))
    }

    // ***********************************************************************************
    // test if implementation meets criteria from the description:
    val part1TestResult = part1(startStateTest)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 12521)
    println("Part1")
    println("Part1: " + part1(startState))

    val part2TestResult = part2(startState2Test)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 44169)
    println("Part2: " + part2(startState2))
}

// ***********************************************************************************

private enum class AmphipodType(val type: Char, val movingCost: Int, val targetRoom: Int) {
    A('A', 1, 0),
    B('B', 10, 1),
    C('C', 100, 2),
    D('D', 1000, 3),
}

private data class Burrow(val roomDepth: Int) {
    val hallLength = 11
    val roomsNumber = 4
    val roomNumbers = (0 until roomsNumber)
    val roomEntrances = roomNumbers.map { room -> (room+1) * 2 }
    val roomAmphipodTypes = roomNumbers.map { room -> AmphipodType.values().first { it.targetRoom == room } }
    val hallIsStayPosition = (0 until hallLength).map { x -> x !in roomEntrances }
}

private fun Burrow.stepsBetween(hallX: Int, roomNumber: Int, roomPos: Int): Int {
    return roomPos + 1 + abs(roomEntrances[roomNumber] - hallX)
}
private fun Burrow.generateState(roomSetter: (room: Int) -> List<AmphipodType>): BurrowState {
    val rooms = List(roomsNumber, roomSetter)
    val hall = List(hallLength) { null }
    return BurrowState(hall, rooms)
}

private fun Burrow.finalState(): BurrowState {
    val rooms = List(roomsNumber) { room ->
        List(roomDepth) { roomAmphipodTypes[room] }
    }
    val hall = List(hallLength) { null }
    return BurrowState(hall, rooms)
}

// ***********************************************************************************


private data class BurrowState(val hall: HallState, val rooms: List<RoomState>)
private typealias HallState = List<AmphipodType?>
private typealias RoomState = List<AmphipodType?>

private data class RoomStatus(
    val roomNumber: Int,
    val occupiedNearestToEntranceY: Int?,
    val incorrectAmphipodTypes: Boolean,
    val deepestFreeY: Int?
)

private fun evalRoomStatus(roomNumber: Int, roomState: RoomState, burrow: Burrow): RoomStatus {
    var occupiedNearestToEntranceY: Int? = null
    var incorrectAmphipodTypes = false
    var deepestFreeY: Int? = null
    for (y in (burrow.roomDepth - 1) downTo  0) {
        val amph = roomState[y]
        if (amph != null) {
            occupiedNearestToEntranceY = y
            if (amph != burrow.roomAmphipodTypes[roomNumber]) {
                incorrectAmphipodTypes = true
            }
        } else if (deepestFreeY == null) {
            deepestFreeY = y
        }
    }
    return RoomStatus(roomNumber, occupiedNearestToEntranceY, incorrectAmphipodTypes, deepestFreeY)
}

private fun HallState.getStayPositionsReachableFromEntrance(roomNumber: Int, burrow: Burrow): List<Int> {
    val roomEntrance = burrow.roomEntrances[roomNumber]
    val resultPositions = mutableListOf<Int>()
    for (x in roomEntrance until burrow.hallLength) {
        if (this[x] != null) {
            break
        }
        if (burrow.hallIsStayPosition[x]) {
            resultPositions.add(x)
        }
    }
    for (x in roomEntrance downTo 0) {
        if (this[x] != null) {
            break
        }
        if (burrow.hallIsStayPosition[x]) {
            resultPositions.add(x)
        }
    }
    return resultPositions
}

private fun HallState.isRoomEntranceReachable(hallX: Int, roomNumber: Int, burrow: Burrow): Boolean {
    val roomEntrance = burrow.roomEntrances[roomNumber]
    val range = if (hallX < roomEntrance) hallX + 1 .. roomEntrance else roomEntrance until hallX
    return range.all { x -> this[x] == null }
}

private fun BurrowState.exchangeInHallAndRoom(hallX: Int, roomNumber: Int, roomPos: Int): BurrowState {
    val roomValue = this.rooms[roomNumber][roomPos]
    val hallValue = this.hall[hallX]

    val newHall = hall.toMutableList().apply { set(hallX, roomValue) }

    val newRooms = rooms.mapIndexed { i, room ->
        if (i == roomNumber) {
            room.toMutableList().apply { set(roomPos, hallValue) }
        } else {
            room
        }
    }
    return BurrowState(newHall, newRooms)

}

private fun BurrowState.moveFromRoomToHall(roomNumber: Int, roomPos: Int, hallX: Int): BurrowState
    = exchangeInHallAndRoom(hallX, roomNumber, roomPos)

private fun BurrowState.moveFromHallToRoom(hallX: Int, roomNumber: Int, roomPos: Int): BurrowState
    = exchangeInHallAndRoom(hallX, roomNumber, roomPos)


private fun BurrowState.nextStates(burrow: Burrow): List<Pair<BurrowState, Int>> {
    val roomStatuses = burrow.roomNumbers.map { r -> evalRoomStatus(r, this.rooms[r], burrow) }

    // From room to hall
    val fromRoomToHall = roomStatuses.asSequence().mapNotNull { rs ->
        if (rs.incorrectAmphipodTypes && rs.occupiedNearestToEntranceY != null) rs.roomNumber to rs.occupiedNearestToEntranceY
        else null
    }.flatMap { (roomNumber, roomPos) ->
        val amph = this.rooms[roomNumber][roomPos] ?: throw error("Wrong")
        this.hall.getStayPositionsReachableFromEntrance(roomNumber, burrow).asSequence()
            .map { hallX ->
                val cost = burrow.stepsBetween(hallX, roomNumber, roomPos) * amph.movingCost
                val newState = this.moveFromRoomToHall(roomNumber, roomPos, hallX)
                newState to cost
            }
    }

    // from hall to room
    val fromHallToRoom = this.hall.asSequence()
        .mapIndexedNotNull { hallX, amph -> if (amph != null) hallX to amph else null }
        .filter { (hallX, amph) ->
            val targetRoomStatus = roomStatuses[amph.targetRoom]
            !targetRoomStatus.incorrectAmphipodTypes
                && targetRoomStatus.deepestFreeY != null
                && hall.isRoomEntranceReachable(hallX, amph.targetRoom, burrow)
        }
        .map { (hallX, amph) ->
            val deepestFreeRoomPos = roomStatuses[amph.targetRoom].deepestFreeY ?: throw error("Unexpected lack of free position in room")
            val cost = burrow.stepsBetween(hallX, amph.targetRoom, deepestFreeRoomPos) * amph.movingCost
            val newState = this.moveFromHallToRoom(hallX, amph.targetRoom, deepestFreeRoomPos)
            newState to cost
        }

    return (fromRoomToHall + fromHallToRoom).toList()
}

// ***************************************************************************
private fun BurrowState.stringify(burrow: Burrow): String {
    return StringBuilder().apply() {
        for (x in hall.indices) {
            append( hall[x]?.type ?: '.')
        }
        appendLine()
        for (y in 0 until burrow.roomDepth) {
            append("  ")
            for (r in burrow.roomNumbers) {
                append( rooms[r][y]?.type ?: '.')
                append(' ')
            }
            appendLine()
        }
    }.toString()
}

private fun BurrowState.print(burrow: Burrow) {
    println(stringify(burrow))
}

private val startStateTest = Burrow(2).generateState { room ->
    when(room) {
        0 -> listOf(B, A)
        1 -> listOf(C, D)
        2 -> listOf(B, C)
        3 -> listOf(D, A)
        else -> throw error("error")
    }
}

private val startState = Burrow(2).generateState { room ->
    when(room) {
        0 -> listOf(D, C)
        1 -> listOf(D, C)
        2 -> listOf(A, B)
        3 -> listOf(A, B)
        else -> throw error("error")
    }
}

private val startState2Test = Burrow(4).generateState { room ->
    when(room) {
        0 -> listOf(B, D, D, A)
        1 -> listOf(C, C, B, D)
        2 -> listOf(B, B, A, C)
        3 -> listOf(D, A, C, A)
        else -> throw error("error")
    }
}

private val startState2 = Burrow(4).generateState { room ->
    when(room) {
        0 -> listOf(D, D, D, C)
        1 -> listOf(D, C, B, C)
        2 -> listOf(A, B, A, B)
        3 -> listOf(A, A, C, B)
        else -> throw error("error")
    }
}





