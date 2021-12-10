fun main() {
    // TODO alternatywa z płaską reprezentacją
    fun part1(input: BingoInput): Int {
        input.print()

        val playBoards = input.boards.map { PlayBoard(it) }

        for (number in input.numbers) {
            playBoards.forEach { it.markValue(number) }
            playBoards
                .find { playBoard -> playBoard.checkBingo() }
                ?.let { winningPlayBoard ->
                    winningPlayBoard.board.print()
                    val sumUnmarked = winningPlayBoard.sumUnmarked()
                    return sumUnmarked * number
                }
        }
        error("No win")
    }

    fun part2(input: BingoInput): Int {
        input.print()

        var playBoards = input.boards.map { PlayBoard(it) }

        for (number in input.numbers) {
            playBoards.forEach { it.markValue(number) }
            val (winning, notWinning) = playBoards.partition { playBoard -> playBoard.checkBingo() }
            if (winning.size == 1 && notWinning.isEmpty()) {
                val winningPlayBoard = winning.first()
                val sumUnmarked = winningPlayBoard.sumUnmarked()
                return sumUnmarked * number
            }
            playBoards = notWinning
        }
        error("No win")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readBingoInput("Day04_test")
    val part1TestResult = part1(testInput)
    println("Part1: Test Result: $part1TestResult")
    check( part1TestResult == 4512)

    val part2TestResult = part2(testInput)
    println("Part2: Test Result: $part2TestResult")
    check( part2TestResult == 1924)

    val input = readBingoInput("Day04")
    println("Part1: " + part1(input))
    println("Part2: " + part2(input))
}

class Board(val values: List<List<Int>>) {

    companion object {
        fun parse(lines: List<String>): Board {
            val values = lines.mapIndexed { i, line ->
                line.split(Regex("\\s+")).filter { it.isNotEmpty() }.map { it.toInt() }.toList()
            }.toList()
            return Board(values)
        }
    }

    fun print() {
        values.forEach {
            it.forEach { print("$it ") }
            println()
        }
    }

    fun getValue(row: Int, column: Int): Int {
        return values[row][column]
    }
}

class PlayBoard(val board: Board) {
    private val states = MutableList(5) { MutableList(5) { false }}

    fun markValue(value: Int) {
        board.values.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, boardValue ->
                if (boardValue == value) {
                    states[rowIndex][columnIndex] = true
                }
            }
        }
    }

    fun checkBingo(): Boolean {
        return checkRows() || checkColumns()
    }

    private fun checkRows(): Boolean {
        return states.any { row -> row.all { it } }
    }

    private fun checkColumns(): Boolean {
        columnsLoop@ for (columnIndex in 0 until 5) {
            for (rowIndex in 0 until 5) {
                if (!states[rowIndex][columnIndex]) {
                    continue@columnsLoop
                }
            }
            return true
        }
        return false
    }

    fun sumUnmarked(): Int {
        var sum = 0
        states.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, marked ->
                if (!marked) {
                    sum += board.getValue(rowIndex, columnIndex)
                }
            }
        }
        return sum
    }
}
class BingoInput(val numbers: List<Int>, val boards: List<Board>) {
    fun print() {
        println("Numbers: $numbers")
        println("Boards:")
        boards.forEachIndexed { i, board ->
            println("Board $i")
            board.print()
        }
    }
}

fun readBingoInput(name: String): BingoInput {
    val lines = readInput(name)

    val numbers = lines.first().split(',').map { it.toInt() }

    val boards = lines.subList(2, lines.size)
        .chunked(6)
        .map { chunkLines -> Board.parse(chunkLines.subList(0, 5)) }

    return BingoInput(numbers, boards)
}