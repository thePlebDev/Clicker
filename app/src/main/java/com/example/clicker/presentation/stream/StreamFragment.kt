package com.example.clicker.presentation.stream

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
    private val autoModViewModel:AutoModViewModel by activityViewModels()


    private fun orientationCheck(){
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            Log.d("ORIENTATIONCHANGE", "HORIZONTAL")
            streamViewModel.setOrientation(isHorizontal = true)
        } else {
            // Portrait
            Log.d("ORIENTATIONCHANGE", "STRAIGHT")
            streamViewModel.setOrientation(isHorizontal = false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orientationCheck()
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


        // val view = binding.root

        val view = setOrientation(
            resources = resources,
            binding = binding,
            streamViewModel = streamViewModel,
            autoModViewModel = autoModViewModel,
            homeViewModel = homeViewModel
        )

        val myWebView: WebView = view.findViewById(R.id.webView)
        val composeView:ComposeView = view.findViewById(R.id.compose_view)
        val overlapView:View = view.findViewById(R.id.overlapView)


        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val rootConstraintLayout:ConstraintLayout = view.findViewById(R.id.rootLayout)
            // Do some stuff
            val clickableWebView: ClickableWebView = myWebView as ClickableWebView
            (clickableWebView as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            clickableWebView.expandedMethod = {
                Log.d("lOGGGINTHEDOUBLECLICK","called to make view expanded")
                clickableWebView.evaluateJavascript("(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();", null);


//            Create layout parameters to match parent
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )

                //View.VISIBLE, View.INVISIBLE, View.GONE
                composeView.visibility = View.INVISIBLE
                overlapView.visibility = View.INVISIBLE

                myWebView.layoutParams = layoutParams
            }

            /**collapsed method*/
            clickableWebView.collapsedMethod = {
                val webViewWidth =(rootConstraintLayout.width * 0.6).toInt()


                val webViewLayout = ConstraintLayout.LayoutParams(
                    webViewWidth,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )

                composeView.visibility = View.VISIBLE
                overlapView.visibility = View.VISIBLE
                myWebView.layoutParams = webViewLayout

            }
        }




        /**Below should happen on the double tap*/


        /**above should happen on the double tap*/


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
private fun removeWidthConstraint( view: WebView) {
    val constraintSet = ConstraintSet()
    constraintSet.clear(view.id)

}
private fun removeComposeWidthConstraint(view: ComposeView) {
    val constraintSet = ConstraintSet()
    constraintSet.clear(view.id)

}

fun setOrientation(
    resources: Resources,
    binding: FragmentStreamBinding,
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    homeViewModel: HomeViewModel
): FrameLayout {
    binding.composeView.apply {
        setContent {
            AppTheme{
                StreamView(
                    streamViewModel,
                    autoModViewModel,
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
