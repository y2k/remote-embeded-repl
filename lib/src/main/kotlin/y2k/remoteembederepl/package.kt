package y2k.remoteembederepl

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import java.lang.reflect.Modifier

object Repl {

    fun eval(ast: String) {
        fun executeRec(tree: ParseTree) {
            if (tree is EmbededLispParser.ExpressionContext)
                execute(tree)
            else
                for (i in 0 until tree.childCount)
                    executeRec(tree.getChild(i))
        }

        val parser = EmbededLispParser(CommonTokenStream(EmbededLispLexer(CharStreams.fromString(ast))))
        executeRec(parser.program())
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
                        EmbededLispParser.STRING -> it.text.substring(1, it.text.length - 1)
                        EmbededLispParser.NUMBER -> it.text.toInt()
                        EmbededLispParser.IDENTIFIER -> {
                            val (cls, field) = it.text.split("/")
                            Class.forName(cls)
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
//            val m = self.javaClass.declaredMethods.first { it.name == identifier.substring(1) }
            val mn = identifier.substring(1)
            val params = arguments.drop(1)
            val m = self.javaClass.methods.find {
                it.name == mn &&
                        it.parameterTypes.size == params.size &&
                        it.parameterTypes.zip(params)
                            .all { (cls, v) -> (primitiveTypes[cls] ?: cls).isInstance(v) }
            } ?: error("Can't find method [$identifier] [$arguments]")
            m.invoke(self, *params.toTypedArray())
        } else if (identifier.endsWith(".")) {
            val cn = identifier.substring(0, identifier.length - 1)
            val cls = Class.forName(cn)
            val c = cls.constructors.find {
                it.parameterTypes.size == arguments.size &&
                        it.parameterTypes.zip(arguments)
                            .all { (cls, v) -> (primitiveTypes[cls] ?: cls).isInstance(v) }
            } ?: error("Can't find constructor [$identifier] [$arguments]")
            c.newInstance(*arguments.toTypedArray())
        } else {
            val (cn, mn) = identifier.split("/")
            val c = Class.forName(cn)
            val m = c.methods.find {
                it.name == mn &&
                        Modifier.isStatic(it.modifiers) &&
                        it.parameterTypes.size == arguments.size &&
                        it.parameterTypes.zip(arguments)
                            .all { (cls, v) -> (primitiveTypes[cls] ?: cls).isInstance(v) }
            } ?: error("Can't find method [$identifier] [$arguments]")
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
}
