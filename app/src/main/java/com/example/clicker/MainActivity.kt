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

    private val homeViewModel: HomeViewModel by viewModels()





    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        val context: Context = this

        val minimunRequiredVersion = Build.VERSION_CODES.S
        val deviceVersion = Build.VERSION.SDK_INT

        if (deviceVersion >= minimunRequiredVersion) {
            val manager = context.getSystemService(DomainVerificationManager::class.java)
            val userState = manager.getDomainVerificationUserState(context.packageName)
            val allowed = userState?.isLinkHandlingAllowed()
            Log.d("domainManagerStuff", "allowed -> $allowed")

            // Domains that haven't passed Android App Links verification but that the user
            val another = DomainVerificationUserState.DOMAIN_STATE_SELECTED

            val selectedDomain = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }

            val verifiedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_VERIFIED }
            Log.d("domainManagerStuff", "verifiedDomains -> $verifiedDomains")
            // has associated with an app.
            val selectedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
            Log.d("domainManagerStuff", "selectedDomains -> $selectedDomains")

            // All other domains.
            val unapprovedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_NONE }

            Log.d("domainManagerStuff", "unapprovedDomains -> $unapprovedDomains")

            if (selectedDomains!!.isNotEmpty()) {
                homeViewModel.registerDomian(true)
            }
            if (unapprovedDomains!!.isNotEmpty()) {
                homeViewModel.registerDomian(false)
            }
            if (!userState.isLinkHandlingAllowed) {
                homeViewModel.registerDomian(false)
            }
        } else {
            homeViewModel.registerDomian(true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
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


    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
