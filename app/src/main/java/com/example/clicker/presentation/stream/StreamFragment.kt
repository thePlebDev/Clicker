package com.example.clicker.presentation.stream

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentStreamBinding
import androidx.fragment.app.activityViewModels


/**
 * A simple [Fragment] subclass.
 * Use the [StreamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StreamFragment : Fragment() {

    private var _binding: FragmentStreamBinding? = null
    private val binding get() = _binding!!
    private val streamViewModel: StreamViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentStreamBinding.inflate(inflater, container, false)
        //val channelName =streamViewModel.channelName.value

        val channelName = "A_Seagull"
        Log.d("twitchNameonCreateView",channelName)

        val url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"

        val view = binding.root
        val myWebView: WebView = view.findViewById(R.id.webView)
        myWebView.settings.mediaPlaybackRequiresUserGesture = false

        myWebView.settings.javaScriptEnabled = true
        myWebView.loadUrl(url)


        binding.composeView.apply{
            setContent {
                StreamView(
                    streamViewModel
                )

            }
        }

        return view
    }


}