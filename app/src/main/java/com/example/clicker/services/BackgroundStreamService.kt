package com.example.clicker.services

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.text.font.FontVariation.italic
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.clicker.MainActivity
import com.example.clicker.R
import com.example.clicker.presentation.stream.AndroidConsoleInterface

class BackgroundStreamService: Service() {
    private lateinit var webView: WebView


    override fun onBind(p0: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString()->{
                Log.d("BackgroundStreamServiceOnStartCommand", "onStartCommand START")
                startForeground()
            }
            Actions.END.toString() ->{
                Log.d("BackgroundStreamServiceOnStartCommand", "onStartCommand END")
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    enum class Actions{
        START, END
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BackgroundStreamService", "Service CREATE")


    }
    inner class LocalBinder : Binder() {
        fun getService(): BackgroundStreamService = this@BackgroundStreamService
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BackgroundStreamService", "Service DESTROY")

    }

    private fun startForeground() {
        // Before starting the service as foreground check that the app has the
        // appropriate runtime permissions. In this case, verify that the user has
        // granted the CAMERA permission.

        Log.d("BackgroundStreamServiceOnStartCommand", "startForeground()")
        val notification = createNotification()
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ 100, // Cannot be 0
            /* notification = */ notification,
            /* foregroundServiceType = */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            },
        )
        //https://player.twitch.tv/?channel=piratesoftware&controls=false&muted=false&parent=modderz

        // Initialize WebView on the main thread
        Handler(Looper.getMainLooper()).post {
            webView = WebView(applicationContext)
            webView.settings.javaScriptEnabled = true
           // Log.d("setWebViewURL","url -->$url")
            webView.settings.mediaPlaybackRequiresUserGesture = false


            webView.settings.javaScriptEnabled = true
            webView.addJavascriptInterface(AndroidConsoleInterface(), "AndroidConsole")
            webView.isClickable = true
            webView.settings.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

            webView.settings.allowContentAccess = true
            webView.settings.allowFileAccess = true

            webView.settings.setSupportZoom(true)

            webView.loadUrl("https://player.twitch.tv/?channel=piratesoftware&controls=false&muted=false&parent=modderz")
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ServiceActions.ACTION_SERVICE_AUDIO.toString()
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Background audio mode is active")
            .setContentText("The stream audio will continue even when app is closed")
            .setSmallIcon(R.drawable.ic_launcher_foreground) //this is the icon that gets showed at the top of the screen. Should be the same as the logog
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that fires when the user taps the notification.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }



}
enum class ServiceActions{
    ACTION_SERVICE_AUDIO
}