package com.example.clicker.presentation.authentication.newUser

import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentNewUserBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.authentication.newUser.views.NewUserComponent
import com.example.clicker.presentation.authentication.twitchAuthorizationScopeURL
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.stream.AndroidConsoleInterface


class NewUserFragment : Fragment() {

    private var _binding: FragmentNewUserBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogoutViewModel by activityViewModels()
    private val clientId = BuildConfig.CLIENT_ID
    private val redirectUrl = BuildConfig.REDIRECT_URL
    /**
     * the variable that acts as access to all the home ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onResume() {
        super.onResume()
        logoutViewModel.setNewUserNavigateHome(false)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){

            checkDomainVerification()
        }else{
            logoutViewModel.setShowLoginWithTwitchButton(true)
        }
        checkForOAuthToken()


       // Log.d("NewUserFragment", "allowed -> $allowed")
    }
    /**
     * checkForOAuthToken() is a private function meant to check if there is data from the intent. If there is then
     * it should parse that data to check for the a access token
     *
     * */
    private fun checkForOAuthToken(){
        val uri: Uri? = activity?.intent?.data
        if(uri != null){
            val accessTokenRegex = "#access_token=([^&]+)".toRegex()

            val matchResult = accessTokenRegex.find(uri.toString())
            val oAuthToken = matchResult?.groupValues?.get(1)?:""
            logoutViewModel.setShowLogin(true)
            logoutViewModel.validateTokenNewUser(oAuthToken)
            Log.d("NewUserFragmentOAuthToken", "authCode -> $oAuthToken")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val authorizationUrl = twitchAuthorizationScopeURL

        val twitchIntent2 = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                authorizationUrl
            ))


        _binding = FragmentNewUserBinding.inflate(inflater, container, false)

        binding.composeView.apply {

            val domainIntent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, //todo: Need to add implementations to lower the API levels
                Uri.parse("package:${context.packageName}")
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NewUserComponent(
                    loginWithTwitch={
                                    Log.d("LOGGINGINWITHtWITCH","CLICKED")
                        if (BuildConfig.BUILD_TYPE =="questDebug") {
                            binding.webView.visibility = View.VISIBLE
                        }else{
                            startActivity(twitchIntent2)
                        }

                    },
                    logoutViewModel=logoutViewModel,
                    navigateToHomeFragment = {
                        findNavController().navigate(R.id.action_newUserFragment_to_homeFragment)
                    },
                    verifyDomain={
                        context.startActivity(domainIntent)
                    },
                    failedHapticFeedback = {
                        binding.root.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    }
                )

            }
            setWebView(
                binding.webView,
                url=authorizationUrl,
                setOAuthToken={token ->
                    homeViewModel.setOAuthToken(token)
                },
                navigateToHomeFragment={
                    findNavController().navigate(R.id.action_newUserFragment_to_homeFragment)
                }
            )
        }


        return binding.root
    }


    /**
     * checkDomainVerification() contains all of the logic to determine if the user has verified the
     * domain or not. Depending on if the user has verified the domain, state of
     * [showLoginWithTwitchButton][com.example.clicker.presentation.logout.LogoutViewModel.showLoginWithTwitchButton]
     * will be set accordingly
     *
     * */
    @RequiresApi(Build.VERSION_CODES.S)
    fun checkDomainVerification(){
        val manager = context?.getSystemService(DomainVerificationManager::class.java)
        val userState = manager?.getDomainVerificationUserState(requireContext().packageName)
        val allowed = userState?.isLinkHandlingAllowed //this is determine if link handling is allowed

        val selectedDomains = userState?.hostToStateMap
            ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
        val domainVerified =selectedDomains?.get("com.example.modderz")
        when {
            allowed == false -> logoutViewModel.setShowLoginWithTwitchButton(false)
            domainVerified == 1 -> logoutViewModel.setShowLoginWithTwitchButton(true)
            else -> logoutViewModel.setShowLoginWithTwitchButton(false)
        }
    }

}



fun setWebView(
    myWebView: WebView,
    url: String,
    setOAuthToken:(String) ->Unit,
    navigateToHomeFragment:()->Unit,
) {
    Log.d("setWebViewURL","url -->$url")
    myWebView.settings.mediaPlaybackRequiresUserGesture = false


    myWebView.settings.javaScriptEnabled = true
    myWebView.addJavascriptInterface(AndroidConsoleInterface(), "AndroidConsole")
    myWebView.isClickable = true
    myWebView.settings.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

    myWebView.settings.allowContentAccess = true
    myWebView.settings.allowFileAccess = true

    myWebView.settings.setSupportZoom(true)

    myWebView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url.toString()


//            if(url.contains())
            if(url.contains("#access_token=")){
                val oAuthToken =checkingUrl(url)
                Log.d("URLCHECKING","token -->$oAuthToken")
                //I need to store the token and then redirect to the home page
               setOAuthToken(oAuthToken)
                navigateToHomeFragment()
                //TODO: i NEED TO REDIRECT TO THE HOME PAGE. WITH A POP AND NOT ALLOW THE USER BACK


            }

            // Allow URLs that start with the Twitch OAuth URL to be loaded normally in the WebView
            if (url.startsWith("https://id.twitch.tv/oauth2/authorize")) {
                view?.loadUrl(url) // Let WebView load the URL
                return false // Don't override, load in WebView
            }

            // For other URLs (e.g., external links), open them in the browser
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Handle any post-load logic if needed
        }
    }

    myWebView.loadUrl(url)
}

fun checkingUrl(
    url:String
):String{

    val regex = """#access_token=([^&]*)""".toRegex()
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value?:""
}