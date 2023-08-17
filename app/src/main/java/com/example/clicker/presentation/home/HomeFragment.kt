package com.example.clicker.presentation.home

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import com.example.clicker.presentation.stream.StreamViewModel
import androidx.fragment.app.activityViewModels
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
    private val homeViewModel: HomeViewModel by viewModels()
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val dataStoreViewModel:DataStoreViewModel by activityViewModels()
    private val workerViewModel:WorkerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val clientId =BuildConfig.CLIENT_ID
                val redirectUrl = BuildConfig.REDIRECT_URL


                val tokenString:String = java.util.UUID.randomUUID().toString()

                val twitchIntent = Intent(
                    Intent.ACTION_VIEW, Uri.parse(
                        "https://id.twitch.tv/oauth2/authorize?client_id=$clientId&redirect_uri=$redirectUrl&response_type=token&scope=user:read:follows+channel:moderate+moderation:read+chat:read+chat:edit")
                )
                HomeView(
                    homeViewModel = homeViewModel,
                    streamViewModel = streamViewModel,
                    loginWithTwitch = {startActivity(twitchIntent)},
                    onNavigate = { dest -> findNavController().navigate(dest) },
                    dataStoreViewModel = dataStoreViewModel,
                    workerViewModel = workerViewModel

                )
            }
        }
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()

        val uri: Uri? = activity?.intent?.data

        val width = Resources.getSystem().displayMetrics.widthPixels /2
        val aspectHeight = (width * 0.5625).toInt()


        val verticalHeight = (width * 1.77777777778).toInt()
        dataStoreViewModel.updateAspectWidthHeight(width, aspectHeight )


        if(uri != null && uri.toString().startsWith(BuildConfig.REDIRECT_URL)){
            Log.d("Twitchval",uri.toString())

            val authCode = uri.fragment?.subSequence(13,43).toString()
            Log.d("OAuthCode",uri.toString())




            workerViewModel.setOAuthToken(authCode)
        }
    }

}