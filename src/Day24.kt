fun main() {

    // PART 1 ******************************************************************************
    fun List<RPNElement>.getNumberIfNumberLiteral(): Int? {
        val last = lastOrNull()
        return if (last != null && last is NumberLiteral) last.value else null
    }

    fun transformOperation(operator: Operator, firstOperandFunction: List<RPNElement>, secondOperandFunction: List<RPNElement>): List<RPNElement> {
        val firstOperandNumber = firstOperandFunction.getNumberIfNumberLiteral()
        val secondOperandNumber = secondOperandFunction.getNumberIfNumberLiteral()
        return when(operator.code) {
            "add" -> {
                when {
                    firstOperandNumber == 0 -> secondOperandFunction
                    secondOperandNumber == 0 -> firstOperandFunction
                    firstOperandNumber != null && secondOperandNumber != null -> listOf(NumberLiteral(firstOperandNumber + secondOperandNumber))
                    else -> listOf(firstOperandFunction, secondOperandFunction, listOf(operator)).flatten()
                }
            }
            "mul" -> {
                when {
                    firstOperandNumber == 0 || secondOperandNumber == 0 -> listOf(NumberLiteral(0))
                    firstOperandNumber == 1 -> secondOperandFunction
                    secondOperandNumber == 1 -> firstOperandFunction
                    firstOperandNumber != null && secondOperandNumber != null -> listOf(NumberLiteral(firstOperandNumber * secondOperandNumber))
                    else -> listOf(firstOperandFunction, secondOperandFunction, listOf(operator)).flatten()
                }
            }
            "div" -> {
                when {
                    firstOperandNumber == 0 -> listOf(NumberLiteral(0))
                    secondOperandNumber == 0 -> throw error("Div by 0")
                    secondOperandNumber == 1 -> firstOperandFunction
                    firstOperandNumber != null && secondOperandNumber != null -> listOf(NumberLiteral(firstOperandNumber / secondOperandNumber))
                    else -> listOf(firstOperandFunction, secondOperandFunction, listOf(operator)).flatten()
                }
            }
            "mod" -> {
                when {
                    firstOperandNumber == 0 -> listOf(NumberLiteral(0))
                    secondOperandNumber != null && secondOperandNumber <= 0 -> throw error("mod by 0 or negativ")
                    secondOperandNumber == 1 -> listOf(NumberLiteral(0))
                    firstOperandNumber != null && secondOperandNumber != null -> listOf(NumberLiteral(firstOperandNumber % secondOperandNumber))
                    else -> listOf(firstOperandFunction, secondOperandFunction, listOf(operator)).flatten()
                }
            }
            "eql" -> {
                when {
                    firstOperandFunction == secondOperandFunction -> listOf(NumberLiteral(1))
                    firstOperandNumber != null && secondOperandNumber != null
                        && firstOperandNumber != secondOperandNumber -> listOf(NumberLiteral(0))
                    firstOperandFunction.last() is InputElement
                        && secondOperandNumber != null &&(secondOperandNumber < 1 || secondOperandNumber > 9) -> listOf(NumberLiteral(0))
                    secondOperandFunction.last() is InputElement
                        && firstOperandNumber != null &&(firstOperandNumber < 1 || firstOperandNumber > 9) -> listOf(NumberLiteral(0))
                    else -> listOf(firstOperandFunction, secondOperandFunction, listOf(operator)).flatten()
                }
            }
            else -> throw error("unknown operator ${operator.code}")
        }
    }

    fun part1(instructions: Day24Input): Int {
        val variablesFunctions = listOf('w', 'x', 'y', 'z')
            .map { it to listOf<RPNElement>(NumberLiteral(0)) }
            .toMap().toMutableMap()

        var inputIndex = 0
        for ((i, instruction) in instructions.withIndex()) {
            val firstOperandFunction = variablesFunctions[instruction.variable] ?: throw error("Unknown variable: ${instruction.variable}")
            val secondOperand = instruction.operand
            val secondOperandFunction =
                when (secondOperand) {
                    is VariableOperand ->
                        variablesFunctions[secondOperand.variable] ?: throw error("Unknown variable in operation operand: ${secondOperand.variable}")
                    is NumberOperand -> listOf(NumberLiteral(secondOperand.value))
                    else -> null
                }
            val newFunction =
                when (instruction.type) {
                    "inp" -> listOf(InputElement(inputIndex++))
                    else -> transformOperation(Operator(instruction.type), firstOperandFunction, secondOperandFunction!!)
                }
            variablesFunctions[instruction.variable] = newFunction
            println("After $i instruction: instruction length: w=${variablesFunctions['w']?.size} x=${variablesFunctions['x']?.size} y=${variablesFunctions['y']?.size} z=${variablesFunctions['z']?.size}")
        }

        val zFunction = variablesFunctions['z']!!
        val res = zFunction.asSequence()
            .mapNotNull { e -> if (e is InputElement) e else null }
            .groupingBy { it.inputIndex }.eachCount()
        println("Res: $res")
        return 0
    }

    // PART 2 ******************************************************************************

    fun part2(input: Day24Input): Int {
        return 0
    }

    // ***********************************************************************************

    val testInput = readDay24Input("Day24_test")
    println("testInput: $testInput")
    val input = readDay24Input("Day24")
    println("input: $input")

    // test if implementation meets criteria from the description:
//    val part1TestResult = part1(testInput)
//    println("Part1: Test Result: $part1TestResult")
//    check( part1TestResult == 100000000)
    println("Part1: " + part1(input))

//    val part2TestResult = part2(testInput)
//    println("Part2: Test Result: $part2TestResult")
//    check( part2TestResult == 100000000)
//    println("Part2: " + part2(input))
}

//private sealed class Symbol
//private class Variable(val name: Char) : Symbol() {
//    companion object {
//        val W = Variable('w')
//        val X = Variable('x')
//        val Y = Variable('y')
//        val Z = Variable('z')
//    }
//}
//private class Literal(val value: Int): Symbol()
//
//private sealed class Instruction(val type: String)
//
//private data class InpInstruction(val variable: Variable) : Instruction("inp")
//private data class AddInstruction(val variable: Variable, val value: Symbol) : Instruction("add")
//private data class MulInstruction(val variable: Variable, val value: Symbol) : Instruction("mul")
//private data class DivInstruction(val variable: Variable, val value: Symbol) : Instruction("div")
//private data class ModInstruction(val variable: Variable, val value: Symbol) : Instruction("mod")
//private data class EqlInstruction(val variable: Variable, val value: Symbol) : Instruction("eql")

private sealed class Operand
private data class NumberOperand(val value: Int): Operand()
private data class VariableOperand(val variable: Char): Operand()

private data class Instruction(val type: String, val variable: Char, val operand: Operand?)

private typealias Day24Input = List<Instruction>

private fun String.parseOperand(): Operand =
    if (length == 1 && first().isLetter()) VariableOperand(first())
    else NumberOperand(toInt())

private fun parseInstruction(s: String): Instruction {
    val words = s.splitToWords()
    return Instruction(words[0], words[1].first(), words.getOrNull(2)?.parseOperand())
}

private fun readDay24Input(name: String): Day24Input {
    return readInput(name).map { line -> parseInstruction(line) }.toList()
}

private sealed class RPNElement
private data class Operator(val code: String): RPNElement()
private data class NumberLiteral(val value: Int): RPNElement()
private data class InputElement(val inputIndex: Int): RPNElement()