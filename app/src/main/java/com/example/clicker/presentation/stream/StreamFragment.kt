package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.R
import com.example.clicker.databinding.FragmentStreamBinding
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.ui.theme.AppTheme

/**
 * A simple [Fragment] subclass.
 * Use the [StreamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StreamFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentStreamBinding? = null
    private val binding get() = _binding!!
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        _binding = FragmentStreamBinding.inflate(inflater, container, false)
        // val channelName =streamViewModel.channelName.value

        val channelName = streamViewModel.channelName.value!!
        Log.d("CHANNELNAMENONENGLISH", "channelName -->$channelName")

        val url = "https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"

        Log.d("CHANNELNAMENONENGLISH", "url -->$url")

        // val view = binding.root

        val view = setOrientation(
            resources = resources,
            binding = binding,
            streamViewModel = streamViewModel,
            homeViewModel = homeViewModel
        )

        val myWebView: WebView = view.findViewById(R.id.webView)
        myWebView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Handle touch down event
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Handle touch up event
                    myWebView.performClick()
                    true
                }
                else -> false
            }
        }


        setWebView(
            myWebView = myWebView,
            url = url
        )
        val backButton: ImageButton? = view.findViewById(R.id.backButton)
        backButton?.setOnClickListener(this)

        return view
    }

    override fun onClick(p0: View?) {
        Log.d("CLICKEDNLOADED", "IT DO BE CLICKING SOMETHING FIERCE")
        findNavController().popBackStack()
    }
}

fun setOrientation(
    resources: Resources,
    binding: FragmentStreamBinding,
    streamViewModel: StreamViewModel,
    homeViewModel: HomeViewModel
): FrameLayout {
    binding.composeView.apply {
        setContent {
            AppTheme{
                StreamView(
                    streamViewModel,
                    homeViewModel
                )

            }

        }
    }

    return binding.root
}
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

fun setWebView(
    myWebView: WebView,
    url: String
) {
    myWebView.settings.mediaPlaybackRequiresUserGesture = false

    myWebView.settings.javaScriptEnabled = true
    myWebView.isClickable = true
    myWebView.settings.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

    myWebView.settings.allowContentAccess = true
    myWebView.settings.allowFileAccess = true

    myWebView.settings.setSupportZoom(true)

    myWebView.loadUrl(url)
}