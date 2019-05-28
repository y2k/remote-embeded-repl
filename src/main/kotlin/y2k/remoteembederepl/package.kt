package y2k.remoteembederepl

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNodeImpl

fun repl(ast: String) {
    fun execute(tree: ParseTree) {
        if (tree is EmbededLispParser.ExpressionContext)
            y2k.remoteembederepl.execute(tree)
        else
            for (i in 0 until tree.childCount)
                execute(tree.getChild(i))
    }

    val parser = EmbededLispParser(CommonTokenStream(EmbededLispLexer(CharStreams.fromString(ast))))
    execute(parser.program())
}

private fun execute(tree: EmbededLispParser.ExpressionContext): Any? {
    tree.getChild(0).let { require(it is TerminalNodeImpl && it.symbol.type == EmbededLispParser.OP) }
    val identifier = tree.getChild(TerminalNodeImpl::class.java, 1)
    require(identifier.symbol.type == EmbededLispParser.IDENTIFIER)

    val arguments = List(tree.childCount, tree::getChild)
        .drop(2)
        .map {
            when (it) {
                is TerminalNodeImpl -> when (it.symbol.type) {
                    EmbededLispParser.STRING -> it.text
                    EmbededLispParser.NUMBER -> it.text.toInt()
                    EmbededLispParser.IDENTIFIER -> TODO()
                    else -> error(it.symbol.type)
                }
                is EmbededLispParser.ExpressionContext -> execute(it)
                else -> error(it)
            }
        }

    return callMethod(identifier.text, arguments)
}

private fun callMethod(identifier: String, arguments: List<Any?>): Any? =
    if (identifier.startsWith(".")) {
        val self = arguments[0]!!
        val method = self.javaClass.methods.first { it.name == identifier.substring(1) }
        method.invoke(self, arguments.drop(1))
    } else {
        val (cn, mn) = identifier.split("/")
        val c = cn::class.java.classLoader.loadClass(cn)
        val m = c.methods.first { it.name == mn }
        m.invoke(null, arguments)
    }
