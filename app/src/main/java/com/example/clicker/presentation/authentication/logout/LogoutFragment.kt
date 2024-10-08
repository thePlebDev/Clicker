package com.example.clicker.presentation.authentication.logout

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.databinding.FragmentLogoutBinding
import com.example.clicker.databinding.FragmentNewUserBinding
import com.example.clicker.presentation.authentication.logout.views.LogoutMainComponent
import com.example.clicker.presentation.authentication.twitchAuthorizationScopeURL
import com.example.clicker.presentation.stream.StreamViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LogoutFragment : Fragment() {

    private var _binding: FragmentLogoutBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogoutViewModel by activityViewModels()
    val clientId = BuildConfig.CLIENT_ID
    val redirectUrl = BuildConfig.REDIRECT_URL





    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LoginFragmentLifeCycleCheck", "onCreate()")

        val window = requireActivity().window

      //  setImmersiveEdgeToEdgeMode(window)
        Log.d("AnotherOneTestingOnCreate","onCreate()")
        val testing =logoutViewModel.navigateHome.value
        Log.d("AnotherOneTestingOnCreate","onCreate() value $testing")
        if(testing == true){
            Log.d("AnotherOneTestingOnCreate","onCreate() true")
            logoutViewModel.setNavigateHome(false)
        }

        super.onCreate(savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LoginFragmentLifeCycleCheck", "onCreateView()")
        // Inflate the layout for this fragment
        val authorizationUrl = twitchAuthorizationScopeURL

        val twitchIntent2 = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                authorizationUrl
            )
        )






        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        // Make the fragment fullscreen

        checkNavigationStatus(binding.root)

        binding.composeView.apply {
            val domainIntent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, //todo: Need to add implementations to lower the API levels
                Uri.parse("package:${context.packageName}")
            )


            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LogoutMainComponent(
                    loginWithTwitch={startActivity(twitchIntent2)},
                    logoutViewModel = logoutViewModel,
                    navigateToHomeFragment = {
                        findNavController().navigate(R.id.action_logoutFragment_to_homeFragment)
                    },
                    verifyDomain={
                        context.startActivity(domainIntent)
                    }
                )
            }
        }




        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        Log.d("LoginFragmentLifeCycleCheck", "onResume()")
        super.onResume()
//        Log.d("LoginViewModelLifecycle","onResume")
        checkAndroidVersion()

        logoutViewModel.setNavigateToLoginWithTwitch(false)

        val uri: Uri? = activity?.intent?.data

        if(uri != null){
            val accessTokenRegex = "#access_token=([^&]+)".toRegex()

            val matchResult = accessTokenRegex.find(uri.toString())
            val oAuthToken = matchResult?.groupValues?.get(1)?:""
            activity?.intent?.data = null
            logoutViewModel.validateOAuthToken(oAuthToken)
            Log.d("LoginFragmentOAuthtoken", "authCode -> $oAuthToken")
        }
    }


    fun checkAndroidVersion(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){

            checkDomainVerification()
        }else{
            logoutViewModel.setShowLoginWithTwitchButton(true)
        }
    }

    private fun checkNavigationStatus(view: FrameLayout){
        view.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return when(logoutViewModel.navigateHome.value){
                        true->{
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            logoutViewModel.setNavigateHome(false)
                            true
                        }
                        false ->{
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        }
                        else ->{
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        }
                    }
                }
            }
        )
    }
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

fun setImmersiveEdgeToEdgeMode(window: Window){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // For SDK >= 30, use WindowInsetsControllerCompat
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        // For SDK < 30, use systemUiVisibility
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}

