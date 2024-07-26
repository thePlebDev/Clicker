package com.example.clicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.services.NetworkMonitorService
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    sealed interface ActivityState {
        data object LOADING : ActivityState
        data object LOADED : ActivityState
    }


    override fun onResume() {
        super.onResume()

    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Registers BroadcastReceiver to track network connection changes.
        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
        installSplashScreen()

        supportActionBar!!.hide()

        val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLACK)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        window.setBackgroundDrawable(bitmapDrawable)
        setContentView(R.layout.activity_main)

        var activityState by remember {
            mutableStateOf(ActivityState.LOADING as ActivityState)
        }
        fullyDrawnReporter.addOnReportDrawnListener {
            activityState = ActivityState.LOADED
        }
        ReportFullyDrawnTheme {
            when(activityState) {
                is ActivityState.LOADING -> {
                    // Display the loading UI.
                }
                is ActivityState.LOADED -> {
                    // Display the full UI.
                }
            }
        }
        SideEffect {
            lifecycleScope.launch(Dispatchers.IO) {
                fullyDrawnReporter.addReporter()

                // Perform the background operation.

                fullyDrawnReporter.removeReporter()
            }
            lifecycleScope.launch(Dispatchers.IO) {
                fullyDrawnReporter.addReporter()

                // Perform the background operation.

                fullyDrawnReporter.removeReporter()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
