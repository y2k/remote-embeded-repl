### Удаленный REPL лиспа для Android

[Antlr граматика](lib/src/main/kotlin/y2k/remoteembederepl/EmbededLisp.g4)

#### Пример использования (Kotlin Scratches)

```kotlin
import io.y2k.remoteconnector.Client

val client = Client.sendObject(
    """
        |(.show
        |   (android.widget.Toast/makeText
        |       io.y2k.replexample.App/instance
        |       "Hello world"
        |       android.widget.Toast/LENGTH_LONG))""".trimMargin()
)
```

#### Интеграция на клиенте

```kotlin
class MainActivity : Activity() {

    private lateinit var server: Closeable

    override fun onStart() {
        super.onStart()
        server = Server.start(Repl::eval)
    }

    override fun onStop() {
        super.onStop()
        server.close()
    }
}
```