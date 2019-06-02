### Удаленный REPL лиспа для Android

[Antlr граматика](lib/src/main/kotlin/y2k/remoteembederepl/EmbededLisp.g4)

### Пример использования (Kotlin Scratches)

##### Показать тост

```kotlin
io.y2k.remoteconnector.Client.sendObject(
    """
        |(.show
        |   (android.widget.Toast/makeText
        |       io.y2k.replexample.App/instance
        |       "Hello world"
        |       android.widget.Toast/LENGTH_LONG))
    """.trimMargin()
)
```

##### Открыть youtube

```kotlin
io.y2k.remoteconnector.Client.sendObject(
    """
        |(.startActivity
        |   io.y2k.replexample.App/instance
        |   (android.content.Intent.
        |       android.content.Intent/ACTION_VIEW
        |       (android.net.Uri/parse "https://youtu.be/dQw4w9WgXcQ")))
    """.trimMargin()
)
```

##### Показть нотификацию

```kotlin
io.y2k.remoteconnector.Client.sendObject(
    """
        |(.notify
        |   (androidx.core.app.NotificationManagerCompat/from
        |       io.y2k.replexample.App/instance)
        |   0
        |   (.build
        |       (.setContentText
        |           (.setContentTitle
        |               (.setSmallIcon
        |                   (androidx.core.app.NotificationCompat${"$"}Builder.
        |                       io.y2k.replexample.App/instance
        |                       "default")
        |                   android.R${"$"}drawable/sym_def_app_icon)
        |               "Hello")
        |           "World")))
    """.trimMargin()
)
```

### Интеграция на клиенте

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