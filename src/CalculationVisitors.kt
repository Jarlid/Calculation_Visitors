interface Node{
    fun accept(visitor: PrintVisitor): String
    fun accept(visitor: CalculateVisitor): Int
    fun accept(visitor: ExpandVisitor): Node?
    fun accept(visitor: ExpandVisitorStart): Node? {
        return visitor.expandVisitStart(this)
    }
    val type: Int // 0 for numbers, 1 for composition, 2 for multiplication
    val firstOperand: Node?
    val secondOperand: Node?
}

class Number(val value: Int) : Node{
    override val type = 0
    override val firstOperand: Node? = null
    override val secondOperand: Node? = null
    override fun accept(visitor: PrintVisitor): String {
        return visitor.printVisit(this)
    }
    override fun accept(visitor: CalculateVisitor): Int {
        return visitor.calculateVisit(this)
    }
    override fun accept(visitor: ExpandVisitor): Node? {
        return visitor.expandVisit(this)
    }
}

class Composition(firstOperand: Node, secondOperand: Node) : Node{
    override val type = 1
    override val firstOperand: Node? = firstOperand
    override val secondOperand: Node? = secondOperand
    override fun accept(visitor: PrintVisitor): String {
        return visitor.printVisit(this)
    }
    override fun accept(visitor: CalculateVisitor): Int {
        return visitor.calculateVisit(this)
    }
    override fun accept(visitor: ExpandVisitor): Node? {
        return visitor.expandVisit(this)
    }
}

class Multiplication(firstOperand: Node, secondOperand: Node) : Node{
    override val type = 2
    override val firstOperand: Node? = firstOperand
    override val secondOperand: Node? = secondOperand
    override fun accept(visitor: PrintVisitor): String {
        return visitor.printVisit(this)
    }
    override fun accept(visitor: CalculateVisitor): Int {
        return visitor.calculateVisit(this)
    }
    override fun accept(visitor: ExpandVisitor): Node? {
        return visitor.expandVisit(this)
    }
}

interface Visitor {
    fun printVisit(node : Number): String {
        return ""
    }
    fun printVisit(node : Composition): String {
        return ""
    }
    fun printVisit(node : Multiplication): String {
        return ""
    }
    fun calculateVisit(node : Number): Int {
        return 0
    }
    fun calculateVisit(node : Composition): Int {
        return 0
    }
    fun calculateVisit(node : Multiplication): Int {
        return 0
    }
    fun expandVisitStart(node : Node): Node? {
        return null
    }
    fun expandVisit(node : Number): Node? {
        return null
    }
    fun expandVisit(node : Composition): Node? {
        return null
    }
    fun expandVisit(node : Multiplication): Node? {
        return null
    }
}

class PrintVisitor: Visitor {
    override fun printVisit(node : Number): String {
        return node.value.toString()
    }
    override fun printVisit(node : Composition): String {
        return "(" + node.firstOperand!!.accept(this) + "+" + node.secondOperand!!.accept(this) + ")"
    }
    override fun printVisit(node : Multiplication): String {
        return node.firstOperand!!.accept(this) + "*" + node.secondOperand!!.accept(this)
    }
}

class CalculateVisitor: Visitor {
    override fun calculateVisit(node : Number): Int {
        return node.value
    }
    override fun calculateVisit(node : Composition): Int {
        return node.firstOperand!!.accept(this) + node.secondOperand!!.accept(this)
    }
    override fun calculateVisit(node : Multiplication): Int {
        return node.firstOperand!!.accept(this) * node.secondOperand!!.accept(this)
    }
}

class ExpandVisitorStart: Visitor {
    override fun expandVisitStart(node: Node): Node? {
        var temp: Node? = node
        var toReturn: Node? = node
        while (temp != null) {
            toReturn = temp
            temp = temp.accept(ExpandVisitor())
        }
        return toReturn
    }
}

class ExpandVisitor: Visitor {
    override fun expandVisit(node : Number): Node? {
        return null
    }
    override fun expandVisit(node : Composition): Node? {
        val temp1 = node.firstOperand!!.accept(ExpandVisitor())
        val temp2 = node.secondOperand!!.accept(ExpandVisitor())
        if (temp1 == null && temp2 == null)
            return null
        if (temp1 == null)
            return Composition(node.firstOperand, temp2!!)
        if (temp2 == null)
            return Composition(temp1, node.secondOperand)
        return Composition(temp1, temp2)
    }
    override fun expandVisit(node : Multiplication): Node? {
        var temp1 = node.firstOperand
        var temp2 = node.secondOperand
        if (temp1!!.type == 1) {
            val temp = temp1
            temp1 = temp2
            temp2 = temp
        }
        if (temp2!!.type == 1) {
            val A = temp1!!
            val B = temp2.firstOperand!!
            val C = temp2.secondOperand!!
            return Composition(Multiplication(A, B), Multiplication(A, C))
        }
        temp1 = node.firstOperand!!.accept(ExpandVisitor())
        temp2 = node.secondOperand!!.accept(ExpandVisitor())
        if (temp1 == null && temp2 == null)
            return null
        if (temp1 == null)
            return Multiplication(node.firstOperand, temp2!!)
        if (temp2 == null)
            return Multiplication(temp1, node.secondOperand)
        return Multiplication(temp1, temp2)
    }
}

fun main() {
    val firstNum = Number(6)
    val secondNum = Number(1)
    val firstOp = Composition(secondNum, secondNum)
    val secondOp = Multiplication(firstNum, firstOp)
    val thirdNum = Number(2)
    val fourthNum = Number(50)
    val thirdOp = Multiplication(thirdNum, fourthNum)
    val fifthNum = Number(30)
    val sixNum = Number(9)
    val fourthOp = Composition(fifthNum, sixNum)
    val fifthOp = Composition(thirdOp, fourthOp)
    val sixthOp = Multiplication(secondOp, fifthOp)

    println(sixthOp.accept(PrintVisitor()))
    println(sixthOp.accept(CalculateVisitor()))
    println(sixthOp.accept(ExpandVisitorStart())!!.accept(PrintVisitor()))
}