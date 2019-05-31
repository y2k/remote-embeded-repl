package io.y2k.replexample

import android.app.Activity
import android.app.Application
import io.y2k.remoteconnector.Server
import y2k.remoteembederepl.Repl
import java.io.Closeable

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

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @JvmField
        var instance: Application? = null
    }
}
