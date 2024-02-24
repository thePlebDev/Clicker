package com.example.clicker.presentation.stream

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.R
import com.example.clicker.databinding.FragmentStreamBinding
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.stream.views.overlays.HorizontalOverlayView
import com.example.clicker.presentation.stream.views.streamManager.EditStreamInfo
import com.example.clicker.ui.theme.AppTheme


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 * [View.OnClickListener] is used for the back button on the stream
 */
class StreamFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentStreamBinding? = null
    private val binding get() = _binding!!
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val autoModViewModel:AutoModViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        autoModViewModel.setHorizontalOverlayToVisible()
        autoModViewModel.setVerticalOverlayToVisible()
    }

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

        val channelName = streamViewModel.channelName.value
        Log.d("CHANNELNAMENONENGLISH", "channelName -->$channelName")

        val url = "https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"

        fun animateHeight(
            layoutParams: ConstraintLayout.LayoutParams,
            finalHeight:Int,
            overlapView:View
        ){
            val initialHeight = layoutParams.height


           // Create a ValueAnimator for height
            val heightAnimator = ValueAnimator.ofInt(initialHeight, finalHeight).apply {
                duration = 100 // Animation duration in milliseconds

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    layoutParams.height = value
                    overlapView.layoutParams = layoutParams
                }
            }

            // Start the height animator
            heightAnimator.start()

        }

        // val view = binding.root

        val view = setOrientation(
            resources = resources,
            binding = binding,
            streamViewModel = streamViewModel,
            autoModViewModel = autoModViewModel,
            homeViewModel = homeViewModel,
        )

        val myWebView: WebView = view.findViewById(R.id.webView)
        val composeView:ComposeView = view.findViewById(R.id.compose_view)

        val streamManagerUI: View = view.findViewById(R.id.nested_draggable_compose_view)
        val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        streamManagerUI.translationY = height














        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val overlapView:View = view.findViewById(R.id.overlapView)
            val overlayComposeView:View = view.findViewById(R.id.overlapComposeView)
            val rootConstraintLayout:ConstraintLayout = view.findViewById(R.id.rootLayout)






            // Do some stuff
            val clickableWebView: ClickableWebView = myWebView as ClickableWebView

            (clickableWebView as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            clickableWebView.expandedMethod = {
                Log.d("lOGGGINTHEDOUBLECLICK","called to make view expanded")
                clickableWebView.evaluateJavascript("(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();", null);

                val overlayComposeParams = overlayComposeView.layoutParams as ConstraintLayout.LayoutParams
                overlayComposeParams.width =rootConstraintLayout.width


//            Create layout parameters to match parent
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )

                //View.VISIBLE, View.INVISIBLE, View.GONE
                composeView.visibility = View.INVISIBLE

                myWebView.layoutParams = layoutParams
                overlayComposeView.layoutParams = overlayComposeParams
            }

            /**method for the single tap*/
            clickableWebView.singleTapMethod={
                val overlayIsVisible = overlapView.visibility ==View.VISIBLE
                val overlayHeight = overlapView.height
                val determinedHeight = (rootConstraintLayout.height * 0.9).toInt()
                val layoutParams = overlapView.layoutParams as ConstraintLayout.LayoutParams

                //todo:example of not what to do
//                val overlayLayout = ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
//                    1,
//                )
//                val largeOverlayLayout = ConstraintLayout.LayoutParams(
//                    overlapView.layoutParams.width,
//                    determinedHeight,
//                )

                if(overlayHeight == 1){
                    animateHeight(
                        layoutParams =overlapView.layoutParams as ConstraintLayout.LayoutParams,
                        finalHeight = determinedHeight,
                        overlapView = overlapView
                    )
                    autoModViewModel.setHorizontalOverlayToVisible()


                }else{
                    animateHeight(
                        layoutParams =overlapView.layoutParams as ConstraintLayout.LayoutParams,
                        finalHeight = 1,
                        overlapView = overlapView
                    )
                    autoModViewModel.setHorizontalOverlayToHidden()



                }//todo:end

            }

            autoModViewModel.singleTapHideHorizontalVisibility ={
                animateHeight(
                    layoutParams =overlapView.layoutParams as ConstraintLayout.LayoutParams,
                    finalHeight = 1,
                    overlapView = overlapView
                )
            }
            /**collapsed method*/
            clickableWebView.collapsedMethod = {
                val webViewWidth =(rootConstraintLayout.width * 0.6).toInt()
                val overlayComposeParams = overlayComposeView.layoutParams as ConstraintLayout.LayoutParams
                overlayComposeParams.width =(rootConstraintLayout.width * 0.6).toInt()


                val webViewLayout = ConstraintLayout.LayoutParams(
                    webViewWidth,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )


                composeView.visibility = View.VISIBLE
                myWebView.layoutParams = webViewLayout
                overlayComposeView.layoutParams = overlayComposeParams

            }
        }else{

            val clickableWebView: ClickableWebView = myWebView as ClickableWebView
            clickableWebView.singleTapMethod={
                if(autoModViewModel.verticalOverlayIsVisible.value){
                    autoModViewModel.setVerticalOverlayToHidden()
                }else{
                    autoModViewModel.setVerticalOverlayToVisible()
                }
                clickableWebView.evaluateJavascript("(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();", null);

            }
            val backButton: ImageButton? = view.findViewById(R.id.backButton)
            backButton?.setOnClickListener(this)


        }





        /**Below should happen on the double tap*/


        /**above should happen on the double tap*/

        setWebView(
            myWebView = myWebView,
            url = url
        )

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

@OptIn(ExperimentalComposeUiApi::class)
fun setOrientation(
    resources: Resources,
    binding: FragmentStreamBinding,
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    homeViewModel: HomeViewModel,
): FrameLayout {

    val editStreamInfoUI:View =binding.root.findViewById(R.id.nested_draggable_compose_view)

    binding.composeView.apply {
        setContent {
            AppTheme{
                StreamView(
                    streamViewModel,
                    autoModViewModel,
                    homeViewModel,
                    showStreamManager={

//                        val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
//                        streamManagerUI.translationY = 0f
                        /**THE ANIMATION*/

                        val newTranslationY = 0 // Replace R.dimen.new_translation_y with your desired dimension resource
//
                        //// Create ObjectAnimator for translationY property
                        val animator = ObjectAnimator.ofFloat(editStreamInfoUI, "translationY", newTranslationY.toFloat())

                        // Set the duration of the animation
                        animator.duration = 300 // Adjust the duration as needed (in milliseconds)

                        // Start the animation
                        animator.start()
                    }
                )

            }

        }
    }
    binding.overlapComposeView?.apply {
        setContent {
            AppTheme{
                HorizontalOverlayView(
                    streamViewModel
                )

            }

        }
    }
    binding.nestedDraggableComposeView?.apply {
        setContent {
            AppTheme{
                EditStreamInfo(
                    closeEditStreamInfo={
                        val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
                        val newTranslationY = height // Replace R.dimen.new_translation_y with your desired dimension resource
//
                        //// Create ObjectAnimator for translationY property
                        val animator = ObjectAnimator.ofFloat(editStreamInfoUI, "translationY", newTranslationY)

                        // Set the duration of the animation
                        animator.duration = 300 // Adjust the duration as needed (in milliseconds)

                        // Start the animation
                        animator.start()
                    },
                    streamTitle=streamViewModel.clickedStreamInfo.value.streamTitle,
                    updateText = {}
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
