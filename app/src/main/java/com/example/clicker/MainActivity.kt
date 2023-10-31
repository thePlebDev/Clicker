package com.example.clicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.clicker.presentation.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private val homeViewModel: HomeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        val context: Context = this


        val minimunRequiredVersion =Build.VERSION_CODES.S
        val deviceVersion = Build.VERSION.SDK_INT

        if(deviceVersion >=minimunRequiredVersion){

            val manager = context.getSystemService(DomainVerificationManager::class.java)
            val userState = manager.getDomainVerificationUserState(context.packageName)

            // Domains that haven't passed Android App Links verification but that the user
            // has associated with an app.
            val selectedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
            Log.d("domainManagerStuff","selectedDomains -> ${selectedDomains}")

            // All other domains.
            val unapprovedDomains = userState?.hostToStateMap
                ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_NONE }

            if(selectedDomains!!.isNotEmpty()){
                homeViewModel.registerDomian(true)
            }
            if(unapprovedDomains!!.isNotEmpty()){
                homeViewModel.registerDomian(false)
            }
        }else{
            homeViewModel.registerDomian(true)
        }



    }



    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       System.setProperty("kotlinx.coroutines.debug", if(BuildConfig.DEBUG) "on" else "off")
        installSplashScreen()

        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)


//        val myWebView: WebView = findViewById(R.id.webview)
//
//
//        val channelName ="Robbaz"
//        val url="https://player.twitch.tv/?channel=$channelName&parent=Modderz"
//        val chatUrl = "https://www.twitch.tv/embed/$channelName/chat?parent=Modderz"
//
//
//        myWebView.settings.javaScriptEnabled = true // THis needs to be enabled
//       // myWebView.settings.setSupportMultipleWindows(true) //How to support webview with multiple screens in android
//        myWebView.loadUrl(chatUrl)




        //val myWebView = WebView(this)
       // myWebView.loadUrl(url)
        //setContentView(myWebView)



    }
    




}


