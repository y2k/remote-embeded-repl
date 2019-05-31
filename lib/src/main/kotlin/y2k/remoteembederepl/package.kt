package y2k.remoteembederepl

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import java.lang.reflect.Modifier

object Repl {

    fun eval(ast: String) {
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
}

private fun execute(tree: EmbededLispParser.ExpressionContext): Any? {
    tree.getChild(0).let { require(it is TerminalNodeImpl && it.symbol.type == EmbededLispParser.OP) }
    val identifier = tree.getChild(TerminalNodeImpl::class.java, 1)
    require(identifier.symbol.type == EmbededLispParser.IDENTIFIER)

    val arguments = List(tree.childCount, tree::getChild)
        .drop(2)
        .dropLast(1)
        .map {
            when (it) {
                is TerminalNodeImpl -> when (it.symbol.type) {
                    EmbededLispParser.STRING -> it.text
                    EmbededLispParser.NUMBER -> it.text.toInt()
                    EmbededLispParser.IDENTIFIER -> {
                        val (cls, field) = it.text.split("/")
                        it.javaClass.classLoader
                            .loadClass(cls)
                            .getDeclaredField(field)
                            .get(null)
                    }
                    else -> error("Unsupported type (${it.symbol})")
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
        method.invoke(self, *arguments.drop(1).toTypedArray())
    } else {
        val (cn, mn) = identifier.split("/")
        val c = cn::class.java.classLoader.loadClass(cn)
        val m = c.methods
            .first {
                it.name == mn &&
                        Modifier.isStatic(it.modifiers) &&
                        it.parameterTypes.size == arguments.size &&
                        it.parameterTypes.zip(arguments)
                            .all { (cls, v) -> (primitiveTypes[cls] ?: cls).isInstance(v) }
            }
        m.invoke(null, *arguments.toTypedArray())
    }

private val primitiveTypes = mapOf(
    Boolean::class.javaPrimitiveType to Boolean::class.java,
    Byte::class.javaPrimitiveType to Byte::class.java,
    Short::class.javaPrimitiveType to Short::class.java,
    Character::class.javaPrimitiveType to Character::class.java,
    Integer::class.javaPrimitiveType to Integer::class.java,
    Long::class.javaPrimitiveType to Long::class.java,
    Float::class.javaPrimitiveType to Float::class.java,
    Double::class.javaPrimitiveType to Double::class.java
)
