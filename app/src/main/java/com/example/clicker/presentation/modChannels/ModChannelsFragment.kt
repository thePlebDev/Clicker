package com.example.clicker.presentation.modChannels

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentModChannelsBinding
import com.example.clicker.ui.theme.AppTheme
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.presentation.home.HomeViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ModChannelsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ModChannelsFragment : Fragment() {

    private var _binding: FragmentModChannelsBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentModChannelsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val clientId = BuildConfig.CLIENT_ID
                val redirectUrl = BuildConfig.REDIRECT_URL
                val twitchIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit+channel:read:editors+moderator:manage:chat_settings+moderator:read:automod_settings+moderator:manage:chat_messages+moderator:manage:automod_settings+moderator:manage:banned_users+user:read:moderated_channels"
                    )
                )
                val intent = CustomTabsIntent.Builder().build()
                val authorizationUrl = "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit+channel:read:editors+moderator:manage:chat_settings+moderator:read:automod_settings+moderator:manage:chat_messages+moderator:manage:automod_settings+moderator:manage:banned_users+user:read:moderated_channels"

                AppTheme{
                    ModChannelView(
                        onNavigate = {  findNavController().popBackStack() },
                        homeViewModel = homeViewModel,
                        loginWithTwitch = {
                           // startActivity(twitchIntent)
                            intent.launchUrl(
                                requireActivity(),
                                Uri.parse(authorizationUrl)
                            )
                        }
                    )
                }
            }
        }




        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
