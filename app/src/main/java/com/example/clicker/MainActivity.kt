package com.example.clicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.clicker.presentation.HomeView
import com.example.clicker.presentation.HomeViewModel
import com.example.clicker.ui.theme.ClickerTheme
import androidx.fragment.app.activityViewModels

class MainActivity : ComponentActivity() {
    private val homeViewModel:HomeViewModel by viewModels()
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

                // A surface container using the 'background' color from the theme
            val clientId =BuildConfig.CLIENT_ID
            val redirectUrl = BuildConfig.REDIRECT_URL

            val tokenString:String = java.util.UUID.randomUUID().toString()

            val twitchIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read")
            )
                HomeView(
                    homeViewModel = homeViewModel,
                    loginWithTwitch = {startActivity(twitchIntent)}
                )
        }
    }

    override fun onResume() {
        super.onResume()
        val uri:Uri? = intent.data

        if(uri != null && uri.toString().startsWith(BuildConfig.REDIRECT_URL)){
            Log.d("Twitchval",uri.toString())

            val accessToken = uri.fragment?.subSequence(13,43).toString()




            homeViewModel.updateAuthenticationCode(accessToken)
        }
    }


}
