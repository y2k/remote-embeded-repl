package io.y2k.replexample

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.y2k.remoteconnector.Server
import y2k.remoteembederepl.Repl
import java.io.Closeable

class MainActivity : Activity() {

    private lateinit var server: Closeable

    override fun onStart() {
        super.onStart()
        server = Server.start(Repl::eval)

        if (false)
            NotificationManagerCompat
                .from(App.instance!!)
                .notify(
                    1,
                    NotificationCompat.Builder(App.instance!!, "default")
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("Hello")
                        .setContentText("World")
//                    .setContentIntent(
//                        PendingIntent.getActivity(
//                            App.instance!!,
//                            1,
//                            Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/dQw4w9WgXcQ")),
//                            PendingIntent.FLAG_UPDATE_CURRENT
//                        )
//                    )
                        .build()
                )
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
        setChannel()
    }

    private fun setChannel() {
        val nm = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel("default", "default", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(nc)
        }
    }

    companion object {
        @JvmField
        var instance: Application? = null
    }
}
