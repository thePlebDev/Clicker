package com.example.clicker

import android.annotation.SuppressLint
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.presentation.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private val homeViewModel: HomeViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       System.setProperty("kotlinx.coroutines.debug", if(BuildConfig.DEBUG) "on" else "off")

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


