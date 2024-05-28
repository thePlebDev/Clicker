package com.example.clicker.presentation.newUser

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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

}