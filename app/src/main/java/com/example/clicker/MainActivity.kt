package com.example.clicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.presentation.HomeView
import com.example.clicker.presentation.HomeViewModel
import com.example.clicker.ui.theme.ClickerTheme
import androidx.fragment.app.activityViewModels
import com.example.clicker.navigation.Navigation

class MainActivity : ComponentActivity() {
    private val homeViewModel:HomeViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val myWebView: WebView = findViewById(R.id.webview)


        val channelName ="CohhCarnage"
        val url="https://player.twitch.tv/?channel=$channelName&parent=Modderz"


        myWebView.settings.javaScriptEnabled = true // THis needs to be enabled
        myWebView.loadUrl(url)




        //val myWebView = WebView(this)
       // myWebView.loadUrl(url)
        //setContentView(myWebView)



       // setContent {
           // AnotherTesting()

            //Text("hello govna", fontSize = 30.sp)

//
////                // A surface container using the 'background' color from the theme
////            val clientId =BuildConfig.CLIENT_ID
////            val redirectUrl = BuildConfig.REDIRECT_URL
////
////
////            val tokenString:String = java.util.UUID.randomUUID().toString()
////
////            val twitchIntent = Intent(
////                Intent.ACTION_VIEW, Uri.parse(
////                    "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read")
////            )
//////                HomeView(
//////                    homeViewModel = homeViewModel,
//////                    loginWithTwitch = {startActivity(twitchIntent)}
//////                )
////            Navigation(
////                homeViewModel = homeViewModel,
////                loginWithTwitch = {startActivity(twitchIntent)}
////            )
////            Text("another one", fontSize = 20.sp)
       // }
    }

    override fun onResume() {
        super.onResume()
        val uri:Uri? = intent.data

        val width = Resources.getSystem().displayMetrics.widthPixels /2
        val aspectHeight = (width * 0.5625).toInt()


        val verticalHeight = (width * 1.77777777778).toInt()
        homeViewModel.updateAspectWidthHeight(width, aspectHeight )


        if(uri != null && uri.toString().startsWith(BuildConfig.REDIRECT_URL)){
            Log.d("Twitchval",uri.toString())

            val accessToken = uri.fragment?.subSequence(13,43).toString()




            homeViewModel.updateAuthenticationCode(accessToken)
        }
    }


}

@Composable
fun AnotherTesting(){
    val context = LocalContext.current
    //val url="https://player.twitch.tv/?channel=$channelName&parent=Modderz"
    val html = "<iframe src=\"?channel=F1NN5TER&parent=Modderz\" height=\"360\" width=\"640\" allowfullscreen/>"
    AndroidView(

        factory = {
            WebView(context).apply {

                //loadUrl("https://player.twitch.tv/?channel=F1NN5TER&parent=Modderz")
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                settings.loadsImagesAutomatically = true
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.pluginState = WebSettings.PluginState.ON
                settings.mediaPlaybackRequiresUserGesture = false
                settings.domStorageEnabled = true
               // settings.setAppCacheMaxSize(1024 * 8)
                settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
               // settings.setAppCacheEnabled(true)
                loadDataWithBaseURL("https://player.twitch.tv/", html, "text/html", "UTF-8", null)

            }
        }
    )
}
