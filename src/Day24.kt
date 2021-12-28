fun main() {

    // PART 1 ******************************************************************************
    data class VariableState(val possibleValues: Set<Long>)

    fun calculateSet(s1: Set<Long>, s2: Set<Long>, operator: (Long, Long) -> Long): Set<Long> =
        s1.asSequence().flatMap { i1 ->
            s2.asSequence().map { i2 ->
                operator(i1, i2)
            }
        }.toSet()

    fun eval(p1: VariableState, p2: VariableState, operator: (Long, Long) -> Long): VariableState {
        val possibleValues = calculateSet(p1.possibleValues, p2.possibleValues, operator)
        return VariableState(possibleValues)
    }

    fun add(p1: VariableState, p2: VariableState): VariableState = eval(p1, p2, Long::plus)
    fun mul(p1: VariableState, p2: VariableState): VariableState = eval(p1, p2, Long::times)
    fun div(p1: VariableState, p2: VariableState): VariableState = eval(p1, p2, Long::div)
    fun mod(p1: VariableState, p2: VariableState): VariableState = eval(p1, p2, Long::mod)

    fun eql(p1: VariableState, p2: VariableState): VariableState =
        eval(p1, p2) { a, b ->
            if (a == b) 1 else 0
        }

    fun inp(p1: VariableState, p2: VariableState): VariableState = p2

    fun functionFor(instructionType: String): (VariableState, VariableState) -> VariableState {
        return when(instructionType) {
            "add" -> ::add
            "mul" -> ::mul
            "div" -> ::div
            "mod" -> ::mod
            "eql" -> ::eql
            "inp" -> ::inp
            else -> throw error("unknown instruction type $instructionType")
        }
    }

    fun canGiveZZero(instructions: List<Instruction>, inputs: List<Int?>): Boolean {
        val variablesState = listOf('w', 'x', 'y', 'z')
            .map { it to VariableState(setOf(0)) }
            .toMap().toMutableMap()

        var inputIndex = 0
        for ((i, instruction) in instructions.withIndex()) {
            val currVariableState = variablesState[instruction.variable] ?: throw error("Unknown variable: ${instruction.variable}")

            val operandState =
                when (instruction.type) {
                    "inp" ->
                        inputs.getOrNull(inputIndex++)?.let { input -> VariableState(setOf(input.toLong())) }
                            ?: VariableState((1L..9L).toSet())
                    else -> {
                        val operand = instruction.operand
                            ?: throw error("Missing second operand for instruction ${instruction.type}")
                        when (operand) {
                            is VariableOperand -> variablesState[operand.variable]
                                ?: throw error("No variable value: ${operand.variable}")
                            is NumberOperand -> VariableState(setOf(operand.value.toLong()))
                        }
                    }
                }

            val resultVariableState = functionFor(instruction.type)(currVariableState, operandState)
            variablesState[instruction.variable] = resultVariableState

//            val variableStatesStr = variablesState.entries.joinToString(" ") { (v, state) ->
//                "$v=(values:${state.possibleValues.size},min:${state.possibleValues.minOf { it }}, max:${state.possibleValues.maxOf { it }}, 0:${state.possibleValues.contains(0)},inputDeps:${state.dependsOnInput.size})" }
//            println("After instruction $i: $variableStatesStr")
        }
        val zState = variablesState['z']!!
        val zeroPossible = zState.possibleValues.contains(0L)
        if (inputs.size <= 3) {
            println("Input: $inputs  can return 0: $zeroPossible")
        }
        return zeroPossible
    }

    fun findRecursively(instructions: List<Instruction>, fixedModelNumberBeginning: List<Int>, dirDown: Boolean = true): List<Int>? {
        if (fixedModelNumberBeginning.size == 14) {
            return fixedModelNumberBeginning
        }
        val range = if (dirDown) 9 downTo 1 else 1..9
        return range.asSequence()
            .mapNotNull { i ->
                val newFixedBeginning = fixedModelNumberBeginning + i
                if (canGiveZZero(instructions, newFixedBeginning)) findRecursively(instructions, newFixedBeginning, dirDown)
                else null
            }
            .firstOrNull()
    }

    fun part1(instructions: List<Instruction>): String? {
        val resultList = findRecursively(instructions, emptyList())

        return resultList?.joinToString("") { it.toString() }
    }

    // PART 2 ******************************************************************************

    fun part2(instructions: List<Instruction>): String? {
        val resultList = findRecursively(instructions, emptyList(), false)

        return resultList?.joinToString("") { it.toString() }
    }

    // ***********************************************************************************

    val testInput = readDay24Input("Day24_test")
    println("testInput: $testInput")
    val input = readDay24Input("Day24")
    println("input: $input")

    // test if implementation meets criteria from the description:
    println("Part1: " + part1(input))
    println("Part2: " + part2(input))
}

private sealed class Operand
private data class NumberOperand(val value: Int): Operand()
private data class VariableOperand(val variable: Char): Operand()

private data class Instruction(val type: String, val variable: Char, val operand: Operand?)

private fun String.parseOperand(): Operand =
    if (length == 1 && first().isLetter()) VariableOperand(first())
    else NumberOperand(toInt())

private fun parseInstruction(s: String): Instruction {
    val words = s.splitToWords()
    return Instruction(words[0], words[1].first(), words.getOrNull(2)?.parseOperand())
}

private fun readDay24Input(name: String): List<Instruction> {
    return readInput(name).map { line -> parseInstruction(line) }.toList()
}
