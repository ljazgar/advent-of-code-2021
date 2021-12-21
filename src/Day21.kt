import java.math.BigInteger
import kotlin.math.min

fun main() {

    // PART 1 ******************************************************************************

    fun part1(input: Day21Input): Int {
        val dice = DeterministicDice()
        var player1State = PlayerState(input.player1Position, 0)
        var player2State = PlayerState(input.player2Position, 0)

        while (true) {
            player1State = player1State.move(dice.roll3Times())
            if (player1State.score >= 1000) {
                break
            }

            player2State = player2State.move(dice.roll3Times())
            if (player2State.score >= 1000) {
                break
            }
        }

        val resScore = min(player1State.score, player2State.score)

        return resScore * dice.counter
    }

    // PART 2 ******************************************************************************

    // Number of points in 3 rolls of Dirac dice -> number of cases
    fun roll3Counts(): Map<Int, BigInteger> {
        var map = mutableMapOf<Int, BigInteger>()
        for (d1 in 1..3) {
            for (d2 in 1..3) {
                for (d3 in 1..3) {
                    val sum = d1+d2+d3
                    val current = map[sum] ?: BigInteger.ZERO
                    map[sum] = current + BigInteger.ONE
                }
            }
        }
        return map
    }
    val roll3Counts = roll3Counts()

    fun turn(i: Int, playersStatesCount: PlayersStatesCount): PlayersStatesCount {
        val isPlayer1Turn = i % 2 == 0

        var newPlayersScoresCount = mutableMapOf<PlayersState, BigInteger>()

        for ((playersScores, playersScoresCountValue) in playersStatesCount) {
            if (playersScores.winner() != null) {
                newPlayersScoresCount.add(playersScores, playersScoresCountValue)
                continue
            }
            for ((dicePoints, dicePointsCount) in roll3Counts) {
                val newCount = playersScoresCountValue * dicePointsCount

                val newPlayersScore = if (isPlayer1Turn) {
                    PlayersState(playersScores.p1.move(dicePoints), playersScores.p2)
                } else {
                    PlayersState(playersScores.p1, playersScores.p2.move(dicePoints))
                }
                newPlayersScoresCount.add(newPlayersScore, newCount)
            }
        }
        return newPlayersScoresCount
    }

    fun part2(input: Day21Input): BigInteger {
        val initPlayersScore = PlayersState(
            PlayerState(input.player1Position, 0),
            PlayerState(input.player2Position, 0)
        )
        val initState: PlayersStatesCount = mapOf(initPlayersScore to BigInteger.ONE)

        var turn = 0
        var currentState = initState
        do {
            currentState = turn(turn, currentState)
            val notFinished = currentState.countNotFinishedStates()
//            println("After turn $turn: Size of state: ${currentState.size}. Not finished: $notFinished")
            turn++
        } while (notFinished > 0)

        val (player1, player2) = currentState.entries.partition { entry -> entry.key.winner() == 1 }

        val player1Sum = player1.sumOf { it.value }
        val player2Sum = player2.sumOf { it.value }

        return if (player1Sum > player2Sum) player1Sum else player2Sum
    }

    // ***********************************************************************************

    val testInput = Day21Input(4, 8)
    println("testInput: $testInput")
    val input = Day21Input(2, 8)
    println("input: $input")

    // test if implementation meets criteria from the description:
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 739785)
    println("Part1: " + part1(input))

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == BigInteger("444356092776315"))
    println("Part2: " + part2(input))
}

private data class Day21Input(val player1Position: Int, val player2Position: Int)

private data class PlayerState(val position: Int, val score: Int)
private data class PlayersState(val p1:PlayerState, val p2: PlayerState)
private typealias PlayersStatesCount = Map<PlayersState, BigInteger>
private typealias MutablePlayersStatesCount = MutableMap<PlayersState, BigInteger>

private fun PlayerState.move(roll3TimesResult: Int): PlayerState {
    val newPosition = (position - 1 + roll3TimesResult) % 10 + 1
    val newScore = score + newPosition
    return PlayerState(newPosition, newScore)
}

private fun MutablePlayersStatesCount.add(playersState: PlayersState, valueToAdd: BigInteger) {
    val current = this[playersState] ?: BigInteger.ZERO
    this[playersState] = current + valueToAdd
}

private fun PlayersState.winner(): Int? {
    return when {
      p1.score >= 21 -> 1
      p2.score >= 21 -> 2
      else -> null
    }
}

private fun PlayersStatesCount.countNotFinishedStates(): Int {
    return this.keys.count { playersState -> playersState.winner() == null }
}

private class DeterministicDice {
    var counter = 0
    var value = 0

    fun roll(): Int {
        counter++
        value = value + 1
        if (value > 100) {
            value = 1
        }
        return value
    }

    fun roll3Times(): Int = roll() + roll() + roll()
}