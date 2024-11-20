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
import com.example.clicker.broadcastReceivers.ShutDownBroadcastReceiver
import com.example.clicker.presentation.stream.AndroidConsoleInterface
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class BackgroundStreamService: Service() {
    private  var webView: WebView? = null

    private var pauseBtnIcon: Int= R.drawable.back_arrow
    private var timeInSeconds =0
    private var timeInMinutes = 0
    private var timeInHours = 0
    private var job: Job? = null


    override fun onBind(p0: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString()->{
                Log.d("BackgroundStreamServiceOnStartCommand", "onStartCommand START")
                intent.extras.toString()
                Log.d("BackgroundStreamServiceOnStartCommand", "extras ${intent?.getStringExtra("channelName")}")
                val channelName = intent.getStringExtra("channelName")
                channelName?.let {
                    startForeground(it)
                }

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


    override fun onDestroy() {
        super.onDestroy()
        Log.d("BackgroundStreamService", "Service DESTROY")
        Log.d("shutdownReceiverContextRegistered","Service DESTROY")

        job?.cancel()
        webView?.let {
            it.loadUrl("about:blank")
            it.clearHistory()
            it.removeAllViews()
            it.destroy()
        }
        webView = null

    }

    private fun startForeground(
        channelName:String
    ) {

        startForeground(100, createNotification2("Timer: 0s"))
        startTimer()
        testLoadWebViewURL(channelName)

    }

    private fun startTimer() {


        // Define CoroutineScope tied to the service lifecycle
        job = CoroutineScope(Dispatchers.IO + CoroutineName("ServiceTimer")).launch {
            while (true) {
                delay(1000L)

                // Increment seconds
                timeInSeconds++
                if (timeInSeconds == 60) {
                    timeInSeconds = 0
                    timeInMinutes++ // Increment minutes when seconds overflow

                    if (timeInMinutes == 60) {
                        timeInMinutes = 0
                        timeInHours++ // Increment hours when minutes overflow
                    }
                }

                // Update notification with formatted time
                val formattedTime = String.format("%02d:%02d:%02d", timeInHours, timeInMinutes, timeInSeconds)
                updateNotification("Active: $formattedTime")
            }
        }
    }
    private fun updateNotification(contentText: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify( 100, createNotification2(contentText))
    }

    fun testLoadWebViewURL(channelName: String){
        //https://player.twitch.tv/?channel=piratesoftware&controls=false&muted=false&parent=modderz

        // Initialize WebView on the Job
        job.apply {
            webView = WebView(applicationContext)
            webView?.settings?.javaScriptEnabled = true
            // Log.d("setWebViewURL","url -->$url")
            webView?.settings?.mediaPlaybackRequiresUserGesture = false
            webView?.addJavascriptInterface(AndroidConsoleInterface(), "AndroidConsole")
            webView?.isClickable = true
            webView?.settings?.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

            webView?.settings?.allowContentAccess = true
            webView?.settings?.allowFileAccess = true

            webView?.settings?.setSupportZoom(true)

            webView?.loadUrl("https://player.twitch.tv/?channel=${channelName}&controls=false&muted=false&parent=modderz")
        }
       //todo: should be some kind of close method
    }

    private fun createNotification2(contentText: String): Notification {
        val intent = Intent(this, ShutDownBroadcastReceiver::class.java).apply {
            action = "com.example.broadcast.MY_NOTIFICATION"
            putExtra("data", "Nothing to see here, move along.")
        }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Background audio")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true) // Makes it non-dismissible
            .addAction(pauseBtnIcon, "CANCEL", pendingIntent) // Action triggers broadcast
            .build()
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

/**
 * [CoroutineScope] tied to this [ViewModel].
 * This scope will be canceled when ViewModel will be cleared, i.e [ViewModel.onCleared] is called
 *
 * This scope is bound to [Dispatchers.Main]
 */
val Service.serviceScope: CoroutineScope
    get() {
        return CoroutineScope(Dispatchers.IO)

    }

internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}