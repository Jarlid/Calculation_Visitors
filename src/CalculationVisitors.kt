interface Node{
    fun <R> accept(visitor: Visitor<R>): R
    val type: Int // 0 for numbers, 1 for composition, 2 for multiplication
    val firstOperand: Node?
    val secondOperand: Node?
}

class Number(val value: Int) : Node{
    override val type = 0
    override val firstOperand: Node? = null
    override val secondOperand: Node? = null
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}

class Composition(firstOperand: Node, secondOperand: Node) : Node{
    override val type = 1
    override val firstOperand: Node? = firstOperand
    override val secondOperand: Node? = secondOperand
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}

class Multiplication(firstOperand: Node, secondOperand: Node) : Node{
    override val type = 2
    override val firstOperand: Node? = firstOperand
    override val secondOperand: Node? = secondOperand
    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}

interface Visitor <R> {
    fun visit(node : Number): R
    fun visit(node : Composition): R
    fun visit(node : Multiplication): R
}

class PrintVisitor: Visitor <String> {
    override fun visit(node : Number): String {
        return node.value.toString()
    }
    override fun visit(node : Composition): String {
        return "(" + node.firstOperand!!.accept(this) + "+" + node.secondOperand!!.accept(this) + ")"
    }
    override fun visit(node : Multiplication): String {
        return node.firstOperand!!.accept(this) + "*" + node.secondOperand!!.accept(this)
    }
}

class CalculateVisitor: Visitor <Int> {
    override fun visit(node : Number): Int {
        return node.value
    }
    override fun visit(node : Composition): Int {
        return node.firstOperand!!.accept(this) + node.secondOperand!!.accept(this)
    }
    override fun visit(node : Multiplication): Int {
        return node.firstOperand!!.accept(this) * node.secondOperand!!.accept(this)
    }
}

class ExpandVisitorStart: Visitor <Node?> {
    override fun visit(node: Number): Node? {
        var temp: Node? = node
        var toReturn: Node? = node
        while (temp != null) {
            toReturn = temp
            temp = temp.accept(ExpandVisitor())
        }
        return toReturn
    }
    override fun visit(node: Composition): Node? {
        var temp: Node? = node
        var toReturn: Node? = node
        while (temp != null) {
            toReturn = temp
            temp = temp.accept(ExpandVisitor())
        }
        return toReturn
    }
    override fun visit(node: Multiplication): Node? {
        var temp: Node? = node
        var toReturn: Node? = node
        while (temp != null) {
            toReturn = temp
            temp = temp.accept(ExpandVisitor())
        }
        return toReturn
    }
}

class ExpandVisitor: Visitor <Node?>{
    override fun visit(node : Number): Node? {
        return null
    }
    override fun visit(node : Composition): Node? {
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
    override fun visit(node : Multiplication): Node? {
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