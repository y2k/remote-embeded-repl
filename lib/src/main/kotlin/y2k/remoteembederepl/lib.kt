package y2k.remoteembederepl

import org.mozilla.javascript.Context

suspend fun main(args: Array<String>) {
    println(eval("2+2"))
}

suspend fun createConnector(port: Int): Any {
    TODO()
}

private fun eval(s: String): Any {
    val cx = Context.enter()
    val scope = cx.initStandardObjects()
    val result = cx.evaluateString(scope, s, "<cmd>", 1, null)
    Context.exit()
    return result
}
