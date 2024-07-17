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
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.sp
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentNewUserBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.authentication.newUser.views.NewUserComponent


class NewUserFragment : Fragment() {

    private var _binding: FragmentNewUserBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogoutViewModel by activityViewModels()
    private val clientId = BuildConfig.CLIENT_ID
    private val redirectUrl = BuildConfig.REDIRECT_URL


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
        val authorizationUrl = "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit+channel:read:editors+moderator:manage:chat_settings+moderator:read:automod_settings+moderator:manage:chat_messages+moderator:manage:automod_settings+moderator:manage:banned_users+user:read:moderated_channels+channel:manage:broadcast+user:edit:broadcast+moderator:manage:automod+moderator:manage:blocked_terms+user:read:chat+user:bot+channel:bot+moderator:manage:unban_requests+moderator:read:moderators+moderator:read:vips"

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
                        startActivity(twitchIntent2)
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