package com.example.clicker.presentation.home

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Insets
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.presentation.authentication.AuthenticationViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val autoModViewModel: AutoModViewModel by activityViewModels()
    private val dataStoreViewModel: DataStoreViewModel by activityViewModels()
    private val workerViewModel: WorkerViewModel by activityViewModels()
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val clientId = BuildConfig.CLIENT_ID
                val redirectUrl = BuildConfig.REDIRECT_URL

                val tokenString: String = java.util.UUID.randomUUID().toString()

                val twitchIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit+channel:read:editors+moderator:manage:chat_settings+moderator:manage:chat_messages+moderator:manage:banned_users"
                    )
                )


                val authorizationUrl = "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit+channel:read:editors+moderator:manage:chat_settings+moderator:read:automod_settings+moderator:manage:chat_messages+moderator:manage:automod_settings+moderator:manage:banned_users"

                val intent = CustomTabsIntent.Builder().build()
                // twitchIntent.setPackage("com.example.clicker")
                val domainIntent = Intent(
                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )

                val height = getScreenHeight(requireActivity())
                val quarterTotalScreenHeight = height/8
                val loadingPadding = quarterTotalScreenHeight/14



                AppTheme{
                    ValidationView(
                        homeViewModel = homeViewModel,
                        streamViewModel = streamViewModel,
                        authenticationViewModel = authenticationViewModel,
                        loginWithTwitch = {
                            startActivity(twitchIntent)
                            intent.launchUrl(
                                requireActivity(),
                                Uri.parse(authorizationUrl)
                            )
                        },
                        onNavigate = { dest -> findNavController().navigate(dest) },
                        addToLinks = { context.startActivity(domainIntent) },
                        quarterTotalScreenHeight,
                        autoModViewModel =autoModViewModel,


                    )
                }

            }
        }
        return view
    }

    //screen width taking into account any space occupied by system bars.
    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right //width of the content area of the current window or activity
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels // total width of the screen, regardless of the current activity,
        }
    }

    //screen height taking into account any space occupied by system bars.
    fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        val screenDensity =Resources.getSystem().displayMetrics.density

        val uri: Uri? = activity?.intent?.data

        val width = Resources.getSystem().displayMetrics.widthPixels / 2
        val aspectHeight = (width * 0.5625).toInt()

        homeViewModel.updateAspectWidthHeight(width, aspectHeight,screenDensity)

        Log.d("Twitchval", "uri -> $uri")

        if (uri != null && uri.toString().startsWith(BuildConfig.REDIRECT_URL)) {
            Log.d("Twitchval", uri.toString())

            val authCode = uri.fragment?.subSequence(13, 43).toString()
            Log.d("OAuthCode", uri.toString())

            authenticationViewModel.setOAuthToken(authCode)
        }
    }
    fun launchCustomTab() {
    }
}