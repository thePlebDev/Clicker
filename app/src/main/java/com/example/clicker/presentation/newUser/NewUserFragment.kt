package com.example.clicker.presentation.newUser

import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentNewUserBinding
import com.example.clicker.presentation.logout.LogoutViewModel
import com.example.clicker.presentation.newUser.views.NewUserComponent


class NewUserFragment : Fragment() {

    private var _binding: FragmentNewUserBinding? = null
    private val binding get() = _binding!!
    private val logoutViewModel: LogoutViewModel by activityViewModels()
    // twitchIntent.setPackage("com.example.clicker")

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        checkDomainVerification()

       // Log.d("NewUserFragment", "allowed -> $allowed")
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


        _binding = FragmentNewUserBinding.inflate(inflater, container, false)

        binding.composeView.apply {
            val domainIntent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, //todo: Need to add implementations to lower the API levels
                Uri.parse("package:${context.packageName}")
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NewUserComponent(
                    loginWithTwitch={},
                    logoutViewModel=logoutViewModel,
                    navigateToHomeFragment = {},
                    verifyDomain={
                        context.startActivity(domainIntent)
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