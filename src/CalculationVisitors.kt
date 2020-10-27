open class Node(inData: Int, open var firstOperand: Node? = null, open var secondOperand: Node? = null){
    open var type = 0 // 0 for numbers, 1 for composition, 2 for multiplication
    open val value = 0
}

class Number(inData: Int) : Node(inData){
    override val value = inData
}

class Operation(inData: Int, firstOperand: Node?, secondOperand: Node?) : Node(inData){
    override var type = inData
    override var firstOperand = firstOperand
    override var secondOperand = secondOperand
}

interface Visitor {
    fun printVisit(node : Node?): String {
        return ""
    }
    fun calculateVisit(node : Node?): Int {
        return 0
    }
    fun expandVisitStart(node : Node?) {}
    fun expandVisit(node : Node?): Boolean {
        return true
    }
}

class PrintVisitor: Visitor {
    override fun printVisit(node : Node?): String {
        if (node!!.type == 0)
            return node.value.toString()
        if (node.type == 1)
            return "(" + printVisit(node.firstOperand) + "+" + printVisit(node.secondOperand) + ")"
        return printVisit(node.firstOperand) + "*" + printVisit(node.secondOperand)
    }
}

class CalculateVisitor: Visitor {
    override fun calculateVisit(node : Node?): Int {
        if (node!!.type == 0)
            return node.value
        if (node.type == 1)
            return calculateVisit(node.firstOperand) + calculateVisit(node.secondOperand)
        return calculateVisit(node.firstOperand) * calculateVisit(node.secondOperand)
    }
}

class ExpandVisitor: Visitor {
    override fun printVisit(node : Node?): String {
        if (node!!.type == 0)
            return node.value.toString()
        if (node.type == 1)
            return printVisit(node.firstOperand) + "+" + printVisit(node.secondOperand)
        return printVisit(node.firstOperand) + "*" + printVisit(node.secondOperand)
    }
    override fun expandVisitStart(node : Node?) {
        var isChanged = true
        while (isChanged) {
            isChanged = expandVisit(node)
        }
    }
    override fun expandVisit(node : Node?): Boolean {
        if (node!!.type == 0)
            return false
        if (node.type == 1) {
            return expandVisit(node.firstOperand) or expandVisit(node.secondOperand)
        }
        if (node.firstOperand!!.type == 1) {
            val temp = node.firstOperand
            node.firstOperand = node.secondOperand
            node.secondOperand = temp
        }
        if (node.secondOperand!!.type == 1) {
            val A = node.firstOperand
            val B = node.secondOperand!!.firstOperand
            val C = node.secondOperand!!.secondOperand
            node.type = 1
            node.firstOperand = Operation(2, A, B)
            node.secondOperand = Operation(2, A, C)
            return true
        }
        return expandVisit(node.firstOperand) or expandVisit(node.secondOperand)
    }
}

fun main() {
    val firstNum = Number(6)
    val secondNum = Number(1)
    val firstOp = Operation(1, secondNum, secondNum)
    val secondOp = Operation(2, firstNum, firstOp)
    val thirdNum = Number(2)
    val fourthNum = Number(50)
    val thirdOp = Operation(2, thirdNum, fourthNum)
    val fifthNum = Number(30)
    val sixNum = Number(9)
    val fourthOp = Operation(1, fifthNum, sixNum)
    val fifthOp = Operation(1, thirdOp, fourthOp)
    val sixthOp = Operation(2, secondOp, fifthOp)

    val printVisitor = PrintVisitor()
    val calculateVisitor = CalculateVisitor()
    val expandVisitor = ExpandVisitor()
    println(printVisitor.printVisit(sixthOp))
    println(calculateVisitor.calculateVisit(sixthOp))
    expandVisitor.expandVisitStart(sixthOp)
    println(expandVisitor.printVisit(sixthOp))
}