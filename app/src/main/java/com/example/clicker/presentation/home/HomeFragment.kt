package com.example.clicker.presentation.home

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.DragEvent
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.cameraNDK.CameraNDKNativeActivity
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.authentication.views.LoadingIndicator
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewDragStateViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.models.UserTypes
import com.example.clicker.presentation.home.views.HomeStreamChatViews
import com.example.clicker.presentation.home.views.VerticalHomeStreamOverlayView
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel
import com.example.clicker.presentation.stream.AndroidConsoleInterface
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamView
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.customWebViews.VerticalWebView
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.services.BackgroundStreamService
import com.example.clicker.services.NetworkMonitorService
import com.example.clicker.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * **HomeFragment** is a [Fragment] subclass. This class acts as the main entry point for a returning user, both in UI and functionality
 *
 *
 */
@AndroidEntryPoint
class HomeFragment : Fragment(){

    /**
     * - the internal  varible for this class's [view binding](https://developer.android.com/topic/libraries/view-binding) implementation
     * */
    private var _binding: FragmentHomeBinding? = null

    /**
     * - The external version of [_binding]
     * */
    private val binding get() = _binding!!


    /**
     * the variable that acts as access to all the home ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val homeViewModel: HomeViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the stream ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val streamViewModel: StreamViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the autoMod ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val autoModViewModel: AutoModViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the modView ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val modViewViewModel: ModViewViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the logout ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val logoutViewModel: LogoutViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the chat ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val chatSettingsViewModel: ChatSettingsViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the streamInfo ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val streamInfoViewModel: StreamInfoViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the search ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val searchViewModel: SearchViewModel by activityViewModels()

    /**
     * the variable that acts as access to all the search ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val selfStreamingViewModel: SelfStreamingViewModel by activityViewModels()

    private val modViewDragStateViewModel: ModViewDragStateViewModel by activityViewModels()

    lateinit private var streamToBeMoved:View
    lateinit private var newWebView:WebView
    //lateinit private var myWebView:WebView
    var maxHeightNewWebView = 608 // Largest height allowed





    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragmentLifeCycle","onCreate")



        Log.d("BuildTypeTesting","${BuildConfig.BUILD_TYPE}")



    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //networkMonitorViewModel.startService()
        context?.startService(Intent(context, NetworkMonitorService::class.java))
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED






        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val value = homeViewModel.determineUserType()
        checkUserType(view,value)

         streamToBeMoved = view.findViewById(R.id.streaming_modal_view)
         newWebView = view.findViewById(R.id.web_view_testing_movement)

        val windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        val height = windowMetrics.getBounds().height()
        val orientationIsLandscape =resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE



        setListenerOnConstraintView(
            streamToBeMoved =streamToBeMoved,
           // webView = view.findViewById(R.id.webView),
            height = height
        )
        //this is the one that I will be actually using
        moveTheWebView(
            newWebView
        )
        Log.d("TestingWebViewHeight", "ebView.layoutParams.height-->${newWebView.layoutParams.height}")
        newWebView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                newWebView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                maxHeightNewWebView = newWebView.height
                Log.d("TestingWebViewHeight", "After layout: ${newWebView.height}")
            }
        })

        // myWebView = view.findViewById(R.id.webView)





        if(value !=UserTypes.NEW){
            binding.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {

                    AppTheme{
                        ValidationView(
                            homeViewModel = homeViewModel,
                            streamViewModel = streamViewModel,
                            onNavigate = {
                                    dest -> findNavController().navigate(dest)


                                         },
                            autoModViewModel =autoModViewModel,
                            updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
                                Log.d("TestingNavigation","updateModViewSettings")

                                modViewViewModel.updateAutoModTokens(
                                    oAuthToken =oAuthToken,
                                    clientId =clientId,
                                    broadcasterId=broadcasterId,
                                    moderatorId =moderatorId
                                )
                            },
                            createNewTwitchEventWebSocket = {modViewViewModel.createNewTwitchEventWebSocket()
                                homeViewModel.setShowHomeChat(true)
                                animateToScreenTop(
                                    streamToBeMoved=streamToBeMoved,
                                    startY=height,
                                    endY = 0
                                )
                                val channelName = streamViewModel.channelName.value
                                Log.d("CHANNELNAMENONENGLISH", "channelName -->$channelName")
                                Log.d("CLICKEDtOnAVIGAE","CLICKED WEBSOCKET")
                            },
                            hapticFeedBackError = {
                                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                            },
                            logoutViewModel=logoutViewModel,
                            chatSettingsViewModel=chatSettingsViewModel,
                            streamInfoViewModel=streamInfoViewModel,
                            modViewViewModel=modViewViewModel,
                            searchViewModel=searchViewModel,
                            startService={
                             //   val startIntent = Intent(this,BackgroundStreamService::class.java)
                                testingPermissionAgain(requireContext())

//                                val startIntent = Intent(context, BackgroundStreamService::class.java)
//                                startIntent.action = BackgroundStreamService.Actions.START.toString()
//                                context.startService(startIntent)


                                         },
                            endService={
                                val startIntent = Intent(context, BackgroundStreamService::class.java)
                                startIntent.action = BackgroundStreamService.Actions.END.toString()
                                context.startService(startIntent)
                            },
                            checkIfServiceRunning= {
                                isServiceRunning(
                                    context,
                                    BackgroundStreamService::class.java
                                )
                            },
                            openAppSettings = {
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, "elliott.software.clicker")  // Use your app's package name
                                }
                                startActivity(intent)
                            },
                            navigateToStream = {
                                Log.d("TestingNavigation","navigateToStream")
                                val oAuthToken = homeViewModel.oAuthToken.value  ?:""
                                val clientId = homeViewModel.validatedUser.value?.clientId ?:""
                                selfStreamingViewModel.setClientIdOAuthToken(
                                    clientId = clientId,
                                    oAuthToken =oAuthToken,
                                    broadcasterId = homeViewModel.validatedUser.value?.userId ?:""
                                )
                               // findNavController().navigate(R.id.action_homeFragment_to_selfStreamingFragment)


                            },
                            loadUrl  ={ channelName->
                                if(homeViewModel.clickedStreamerName.value != channelName){
                                    setWebView(
                                        myWebView=newWebView,
                                        url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"
                                    )

                                }


                                isDraggingWebView = false

                                val webView = newWebView
                                ValueAnimator.ofInt(webView.y.toInt(), 0).apply {
                                    duration = 300 // Adjust duration for the animation
                                    addUpdateListener { animator ->
                                        val value = animator.animatedValue as Int
                                        webView.y = value.toFloat()
                                    }
                                    start()
                                }
                                ValueAnimator.ofInt(webView.x.toInt(), 0).apply {
                                    duration = 300 // Adjust duration for the animation
                                    addUpdateListener { animator ->
                                        val value = animator.animatedValue as Int
                                        webView.x = value.toFloat()
                                    }
                                    start()
                                }
                                val params = webView.layoutParams
                                ValueAnimator.ofInt(params.height, maxHeightNewWebView).apply {
                                    duration = 300 // Adjust duration for the animation
                                    addUpdateListener { animator ->
                                        val webParams = webView.layoutParams
                                        webParams.height = animator.animatedValue as Int
                                        webView.layoutParams = webParams
                                    }
                                    start()
                                }
                                val newWidth = (maxHeightNewWebView * 16 / 9)
                                Log.d("NEWWIDTHtESTING", "WIDTH-->$newWidth")
                                ValueAnimator.ofInt(params.width, newWidth).apply {
                                    duration = 300 // Adjust duration for the animation
                                    addUpdateListener { animator ->
                                        val webParams = webView.layoutParams
                                        webParams.width = animator.animatedValue as Int
                                        webView.layoutParams = webParams
                                    }


                                    start()
                                }
                                smallHeightPositioned = false


                        }


                        )
                    }

                }
            }
            binding.streamComposeView.apply{
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {

                    AppTheme{
                        HomeStreamChatViews(
                            streamViewModel,
                            autoModViewModel,
                            modViewViewModel,
                            chatSettingsViewModel,
                            hideSoftKeyboard ={
                                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken,0)

                            },
                            showModView={
                                if(!orientationIsLandscape){
                                    Log.d("ShowModViewFunction","clicked")
                                    modViewDragStateViewModel.setShowModView(true)
                                }

                            },
                            modViewIsVisible = modViewDragStateViewModel.showModView.value,
                            streamInfoViewModel=streamInfoViewModel,
                            showHomeChat=homeViewModel.showHomeChat.value
                        )

                    }
                }
            }
            binding.horizontalOverlay.apply{
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {

                    AppTheme{
                        VerticalHomeStreamOverlayView(
                            channelName = streamViewModel.clickedStreamInfo.value.channelName,
                            streamTitle = streamViewModel.clickedStreamInfo.value.streamTitle,
                            category = streamViewModel.clickedStreamInfo.value.category,
                            tags = streamViewModel.tagsImmutable.value,
                        )
                    }
                }
            }

        }



        return view
    }


    fun setWebView(
        myWebView: WebView,
        url: String
    ) {
        Log.d("setWebViewURL","url -->$url")
        myWebView.settings.mediaPlaybackRequiresUserGesture = false


        myWebView.settings.javaScriptEnabled = true
        myWebView.addJavascriptInterface(AndroidConsoleInterface(), "AndroidConsole")
        myWebView.isClickable = true
        myWebView.settings.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

        myWebView.settings.allowContentAccess = true
        myWebView.settings.allowFileAccess = true

        myWebView.settings.setSupportZoom(true)

        myWebView.loadUrl(url)
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (service in runningServices) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun animateToScreenTop(
        streamToBeMoved:View,
        startY:Int,
        endY:Int
    ){
        val layoutParams = streamToBeMoved.layoutParams as FrameLayout.LayoutParams

        ValueAnimator.ofInt(startY, endY).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                layoutParams.setMargins(layoutParams.leftMargin, animator.animatedValue as Int, layoutParams.rightMargin, layoutParams.bottomMargin)
                streamToBeMoved.layoutParams = layoutParams
            }
            start()
        }

    }


    private fun horizontalAnimateWidthNPosition(
        webView: View,
        finalWidth:Int

    ){
        val newHeight = webView.height+100
        val horizontalSpringAnimation = SpringAnimation(webView, SpringAnimation.X).apply {
            spring = SpringForce().apply {
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                stiffness = SpringForce.STIFFNESS_LOW // Lower = smoother motion
            }
        }
       // unsetImmersiveMode(requireActivity().window)
        val params = webView.layoutParams
        ValueAnimator.ofInt(params.width, finalWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val webParams = webView.layoutParams
                webParams.width = animator.animatedValue as Int
                webView.layoutParams = webParams
            }

            start()
        }


        horizontalSpringAnimation.animateToFinalPosition(0f)


    }



    var initialY = 0f
    var initialTopMargin = 0
    var isDragging = false
    fun setListenerOnConstraintView(
        streamToBeMoved:View,
      //  webView: WebView,
        height:Int,
    ){

        val layoutParams = streamToBeMoved.layoutParams as FrameLayout.LayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, height, layoutParams.rightMargin, layoutParams.bottomMargin)
        streamToBeMoved.layoutParams = layoutParams


    }
// VERTICAL ANIMATIONS
    var initialWebViewY = 0f
    var initialWebViewX = 0f
    var isDraggingWebView = false
    var lastY = 0f
    var lastX = 0f
    var lastHeight = 0
    var lastWidth = 0
    val minHeight = 300  // Smallest height allowed
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    var smallHeightPositioned = false
    // Define animations outside for efficiency
    private lateinit var springAnimation: SpringAnimation
    private lateinit var springAnimationX: SpringAnimation

    // HORIZONTAL ANIMATIONS
    var horizontalInitialWebViewY = 0f
    var horizontalInitialWebViewX = 0f
    var horizontalIsDraggingWebView = false
    var horizontalLastY = 0f
    private var horizontalLastTapTime = 0L
    private val horizontalDoubleTapThreshold = 300


    fun moveTheWebView(webView: WebView) {
        webView.settings.mediaPlaybackRequiresUserGesture = false


        webView.settings.javaScriptEnabled = true
        webView.isClickable = true
        webView.settings.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true

        webView.settings.setSupportZoom(true)

      //  webView.loadUrl("https://player.twitch.tv/?channel=Ludwig&controls=false&muted=false&parent=modderz")
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        webView.y = screenHeight.toFloat()
      //  maxHeight = webView.layoutParams.height
        Log.d("WebViewPositionCHecking", "ebView.layoutParams.height-->${webView.layoutParams.height}")
        val halfwayPoint = screenHeight / 2
        // Initialize the Spring Animation
         springAnimation = SpringAnimation(webView, SpringAnimation.Y).apply {
            spring = SpringForce().apply {
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                stiffness = SpringForce.STIFFNESS_LOW // Lower = smoother motion
            }
        }
        springAnimationX = SpringAnimation(webView, SpringAnimation.X).apply {
            spring = SpringForce().apply {
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                stiffness = SpringForce.STIFFNESS_LOW // Lower = smoother motion
            }
        }
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                if(isLandscape){

//                    val newWidth = webView.width * 0.8.toInt()
                    val newWidth = (webView.width * 0.85).toInt()

                    horizontalAnimateWidthNPosition(
                        webView = webView,
                        finalWidth = newWidth,
                    )
                    //this should make sure the place holder always matches the actual webview showing the stream
                    horizontalAnimateWidthNPosition(
                        //this view is the place holder
                        webView = binding.root.findViewById<ComposeView>(R.id.webView),
                        finalWidth = newWidth,
                    )

                    //todo animate the chats width and move its x position to half way

                    val view =binding.root
                    //this is the chat
                   val chatView = view.findViewById<ComposeView>(R.id.stream_compose_view)

                    val horizontalSpringAnimationX = SpringAnimation(chatView, SpringAnimation.X).apply {
                        spring = SpringForce().apply {
                            dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                            stiffness = SpringForce.STIFFNESS_LOW // Lower = smoother motion
                        }
                    }
                    val horizontalSpringAnimationY = SpringAnimation(chatView, SpringAnimation.Y).apply {
                        spring = SpringForce().apply {
                            dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                            stiffness = SpringForce.STIFFNESS_LOW // Lower = smoother motion
                        }
                    }


                    val params = chatView.layoutParams
                    //this is animating its width
                    val newChatWidth = (webView.width *0.45).toInt()
                    ValueAnimator.ofInt(params.width, newChatWidth).apply {
                        duration = 300 // Adjust duration for the animation
                        addUpdateListener { animator ->
                            val chatParams = chatView.layoutParams
                            chatParams.width = animator.animatedValue as Int
                            chatView.layoutParams = chatParams
                        }

                        start()
                    }
                    //CHAT HEIGHT

                    val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager


                        val metrics = windowManager.currentWindowMetrics
                        val screenHeightTesting =  metrics.bounds.height()


                    Log.d("TestingChatHeight", "height --> $screenHeight")
                    val newChatHeight = 1100
                    Log.d("TestingChatHeight","heightTesting-->${screenHeightTesting}")

                    ValueAnimator.ofInt(params.height, screenHeightTesting).apply {
                        duration = 300 // Adjust duration for the animation
                        addUpdateListener { animator ->
                            val chatParams = chatView.layoutParams
                            chatParams.height = animator.animatedValue as Int
                            chatView.layoutParams = chatParams
                        }
                        doOnEnd {
                            // animation done inside of doOnEnd to make sure we get the proper values
                            val width = webView.width
                            Log.d("TESTINGWEBVIEWwIDTH","WIDTH-->$width")
                            horizontalSpringAnimationX.animateToFinalPosition(1538f)//need to delete this magic number
                            horizontalSpringAnimationY.animateToFinalPosition(0f)//this one is good
                        }

                        start()
                    }


                    //BELOW DID NOT WORK

//                    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                    //the chat width can be a little bigger
                    val newChatXPosition = (screenWidth -newChatWidth).toFloat()
//                    horizontalSpringAnimationX.animateToFinalPosition(1700f) // this seems find
//                    horizontalSpringAnimationY.animateToFinalPosition(0f) // this needs to be increased
                    Log.d("chatHeightTesting","height ->${params.height}")


                }



               Log.d("GestureDetectorTapping","DOUBLE")
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                // ofInt() gives the start and end value
                //this is where it goes
                if(isLandscape){
                    Log.d("ORIENTATIONtESTIN","LANDSCAPE")
                    val composeView = binding.root.findViewById<ComposeView>(R.id.horizontal_overlay)
                    composeView.visibility = View.VISIBLE
                    horizontalAnimateSingleTap(
                        webView
                    )

                }else{
                    Log.d("ORIENTATIONtESTIN","vertical")
                    verticalAnimateSingleTap(
                        webView,screenHeight
                    )
                }

                return super.onSingleTapConfirmed(e)
            }


        })

        webView.setOnTouchListener { v, event ->
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            gestureDetector.onTouchEvent(event)


            if(isLandscape){

                //DO NOTHING RIGHT NOW
                //TODO  START OF THE HORIZONTAL ANIMATIONS
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        horizontalInitialWebViewY = event.rawY
                        horizontalInitialWebViewX = event.rawX
                        horizontalLastY = webView.y

                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dy =  event.rawY - horizontalInitialWebViewY
                        val dx = horizontalInitialWebViewX - event.rawX

                        if (dy > 10 || dy < -10) {
                            horizontalIsDraggingWebView = true
                        }
                        if(horizontalIsDraggingWebView){
                            val newY =(horizontalLastY + dy)
                            springAnimation.animateToFinalPosition(newY)

                        }

                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        val dy =  event.rawY - horizontalInitialWebViewY
                        //this is the tapping conditional
                        val newTestingWidth = newWebView.width - (newWebView.width*0.2)
                        val newTestingHeight = (newTestingWidth * 9 / 16)
                        Log.d("TestingTheWebViewWidth","width ->${newTestingWidth}")
                        Log.d("TestingTheWebViewWidth","height ->${newTestingHeight}")


                         if (!isDraggingWebView && Math.abs(dy) < 10) {
//                            val composeView = binding.root.findViewById<ComposeView>(R.id.horizontal_overlay)
//                            composeView.visibility = View.VISIBLE
//
//                            animateHeightComposeView(
//                                composeView,
//                                (webView.height*0.35).toInt()
//                            )
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                    animateHeightComposeView(
//                                        composeView,
//                                        0
//                                    )
//                                },
//                                1000 // value in milliseconds
//                            )

                        } // END OF TAPPING CONDITIONAL
                        horizontalIsDraggingWebView = false


                        true
                    } //END OF  MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
                    else -> false
                }
                 true //todo: END OF THE HORIZONTAL ANIMATIONS

            }else{

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialWebViewY = event.rawY
                        initialWebViewX = event.rawX
                        lastY = webView.y
                        lastX = webView.x
                        lastHeight = webView.height
                        lastWidth = webView.width
                        isDraggingWebView = false
                        springAnimation.cancel()
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dy = event.rawY - initialWebViewY
                        val dx = event.rawX - initialWebViewX

                        if (dy > 10 || dy < -10) {
                            isDraggingWebView = true
                        }

                        if (isDraggingWebView) {

                            // Move Y position
                            // webView.y = lastY + dy+100
                            Log.d("WebViewPositionCHecking", "webView.y-->${webView.y}")
                            val newY =(lastY + dy).coerceIn(0f, (screenHeight - 200 - minHeight).toFloat())
                            val minWidth =(minHeight * 16 / 9)
                            val newXDragging =(lastX + dx).coerceIn(0f, (screenWidth - minWidth).toFloat())
                            //webView.y = (lastY + dy).coerceIn(0f, (screenHeight - 200 - minHeight).toFloat())
                            if(smallHeightPositioned){
                                springAnimationX.animateToFinalPosition(newXDragging)
                            }
                            springAnimation.animateToFinalPosition(newY)

                            // Shrink the WebView (keeping 16:9 ratio)
                            // Check if WebView has moved past halfway
                            if (webView.y > halfwayPoint) {
                                Log.d("WebViewPositionCHecking", "WebView has passed the halfway point!")
                            } else {
                                //  Log.d("WebViewPositionCHecking", "WebView is still above the halfway point.")
                            }
                            val newHeight = (lastHeight - dy).toInt().coerceIn(minHeight, maxHeightNewWebView)
                            val newWidth = (newHeight * 16 / 9).toInt()
                            if (newHeight == minHeight){
                                smallHeightPositioned = true
                            }

                            // Move X position (push to side gradually)
                            val progress = (newHeight - minHeight).toFloat() / (maxHeightNewWebView - minHeight) // 0 to 1
                            val newX = (screenWidth - newWidth) * (1 - progress) // Moves toward the right

                            if (webView.y > halfwayPoint) {

                            }else{
                                if(!smallHeightPositioned){
                                    val params = webView.layoutParams
                                    params.width = newWidth
                                    params.height = newHeight
                                    webView.layoutParams = params
                                    //  webView.x = newX
                                }

                            }


                        }

                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        val dy = (event.rawY - initialWebViewY).toInt()


                        isDraggingWebView = false

                        //The tapped conditional is moved to the gesture detection


                        val testing =streamToBeMoved.y


                        if(testing==0f && smallHeightPositioned){
                            animateToScreenTop(
                                streamToBeMoved=streamToBeMoved,
                                startY=0,
                                endY = screenHeight
                            )
                        }


                        true
                    } //END OF  MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
                    else -> false
                }
            }



        }
    }

    fun verticalAnimateSingleTap(
        webView:WebView,
        screenHeight: Int
    ){
        animateToFullScreen(
            webView,
            maxHeightNewWebView
        )

        //fixes the bug of typing taping when the screen is full
        if(smallHeightPositioned){
            //webview is in mini screen form and is being tapped
            animateToScreenTop(
                streamToBeMoved=streamToBeMoved,
                startY=screenHeight,
                endY = 0
            )
            //above should be in the onSingleTap
        } else{
            //webview is in full screen form and is being tapped
            verticalWebViewOverlayClicked(webView as VerticalWebView)
        }

        smallHeightPositioned = false
    }
    fun horizontalAnimateSingleTap(
        webView: WebView
    ){
        val composeView = binding.root.findViewById<ComposeView>(R.id.horizontal_overlay)
        composeView.visibility = View.VISIBLE

        animateHeightComposeView(
            composeView,
            (webView.height*0.35).toInt()
        )
        Handler(Looper.getMainLooper()).postDelayed({
            animateHeightComposeView(
                composeView,
                0
            )
        },
            1000 // value in milliseconds
        )
    }

    /**
     * animateHeight takes the starting height of the [composeView] and animates it to the final height
     * specified at [finalHeight]
     * */
    private fun animateHeightComposeView(
        composeView: ComposeView,
        finalHeight:Int
    ){
        val params = composeView.layoutParams
        //animating the HEIGHT position
        ValueAnimator.ofInt(params.height, finalHeight).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val webParams = composeView.layoutParams
                webParams.height = animator.animatedValue as Int
                composeView.layoutParams = webParams
            }
            start()
        }
    }


    private fun animateToFullScreen(
        webView: WebView,
        maxHeight: Int,
    ){

        //animating the Y position
        ValueAnimator.ofInt(webView.y.toInt(), 0).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.y = value.toFloat()
            }
            start()
        }
        //animating the X position
        ValueAnimator.ofInt(webView.x.toInt(), 0).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.x = value.toFloat()
            }
            start()
        }
        val params = webView.layoutParams
        //animating the HEIGHT position
        ValueAnimator.ofInt(params.height, maxHeight).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val webParams = webView.layoutParams
                webParams.height = animator.animatedValue as Int
                webView.layoutParams = webParams
            }
            start()
        }
        val newWidth = (maxHeight * 16 / 9)
        Log.d("NEWWIDTHtESTING", "WIDTH-->$newWidth")
        //animating the WIDTH position
        ValueAnimator.ofInt(params.width, newWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val webParams = webView.layoutParams
                webParams.width = animator.animatedValue as Int
                webView.layoutParams = webParams
            }


            start()
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            val rootView = requireActivity().window.decorView
            rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                    val screenWidth = Resources.getSystem().displayMetrics.widthPixels

                    if(smallHeightPositioned){
                        animateToBottomRightScreen(
                            screenHeight=screenHeight,
                            screenWidth=screenWidth
                        )
                    }else{
                        animateHorizontalToFullScreen(
                            newWebView,
                            Resources.getSystem().displayMetrics.heightPixels,

                            )
                        animateToTopLeftScreen(
                            newWebView,
                            screenWidth
                        )

                    }

                    rootView.viewTreeObserver.removeOnGlobalLayoutListener(this) // Prevent multiple calls
                }
            })
            setImmersiveMode(requireActivity().window)
            Log.d("onConfigurationChangedTesting", "Landscape mode")






        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val composeView = binding.root.findViewById<ComposeView>(R.id.horizontal_overlay)
            composeView.visibility = View.GONE

            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            Log.d("TestingScreenHeight", "portrait -->: ${screenHeight}")
            unsetImmersiveMode(requireActivity().window)

            if(smallHeightPositioned){
                animateToBottomRightScreen(
                    screenHeight=screenHeight,
                    screenWidth=screenWidth
                )
            }else{
                animateToFullScreen(
                    newWebView,
                    maxHeightNewWebView
                )
            }



        }

    }

    fun animateToBottomRightScreen(
        screenHeight:Int,
        screenWidth:Int
    ){
        ValueAnimator.ofInt(newWebView.y.toInt(), (screenHeight -newWebView.height)).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                newWebView.y = value.toFloat()
            }
            start()
        }
        ValueAnimator.ofInt(newWebView.x.toInt(), (screenWidth -newWebView.width)).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                newWebView.x = value.toFloat()
            }
            start()
        }
    }
    fun animateToTopLeftScreen(
        webView: WebView,
        maxWidth:Int
    ){
        val endWidth = (maxWidth*0.13).toInt()
        Log.d("TESTINGMAXLENGHTTHINGER","endWidth -->$endWidth")
        ValueAnimator.ofInt(webView.y.toInt(), 0).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.y = value.toFloat()
            }
            start()
        }
        ValueAnimator.ofInt(webView.x.toInt(), endWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.x = value.toFloat()
            }
            start()
        }
    }
    fun animateHorizontalToFullScreen(
        webView:WebView,
        maxHeight:Int,

    ){
        val params = webView.layoutParams
        //animating the HEIGHT position
        ValueAnimator.ofInt(params.height, maxHeight).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val webParams = webView.layoutParams
                webParams.height = animator.animatedValue as Int
                webView.layoutParams = webParams
            }
            start()
        }

        //animating the width position
        val newWidth = (maxHeight * 16 / 9)
        Log.d("NEWWIDTHtESTING", "WIDTH-->$newWidth")
        //animating the WIDTH position
        ValueAnimator.ofInt(params.width, newWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val webParams = webView.layoutParams
                webParams.width = animator.animatedValue as Int
                webView.layoutParams = webParams
            }


            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onPause() {
        super.onPause()
        Log.d("HomeFragmentLifeCycle","onPause")

    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragmentLifeCycle","onResume")
        logoutViewModel.setShowLogin(false)
        checkForUri()
        setAspectRatio()

        val checked =isServiceRunning(
            requireContext(),
            BackgroundStreamService::class.java
        )
        if(checked){
            homeViewModel.changeBackgroundServiceChecked(true)

        }else{
            homeViewModel.changeBackgroundServiceChecked(false)
        }
        // Use the data
    }

    /**
     * - **checkForUri** is a function that runs on every call to [onResume].
     * This function will check for any incoming uri's and extract the OAuthToken if any Uri's are found
     * */
    private fun checkForUri(){
        val uri: Uri? = activity?.intent?.data
        Log.d("Twitchval", "uri -> $uri")
        if (uri != null && uri.toString().startsWith(BuildConfig.REDIRECT_URL)) {
            Log.d("Twitchval", uri.toString())

            val authCode = uri.fragment?.subSequence(13, 43).toString()
            Log.d("OAuthCode", uri.toString())

            homeViewModel.setOAuthToken(authCode)
        }
    }

    /**
     * - **setAspectRatio** is a function that runs on every call to [onResume].
     * This function will identify the device's screen density use to to properly calculate the screen's aspect ratio
     * */
    private fun setAspectRatio(){
        val screenDensity =Resources.getSystem().displayMetrics.density
        val width = Resources.getSystem().displayMetrics.widthPixels / 2
        val aspectHeight = (width * 0.5625).toInt()
        Log.d("onResumeHeight","height->$aspectHeight")
        Log.d("onResumeHeight","width->$width")

        homeViewModel.updateAspectWidthHeight(width, aspectHeight,screenDensity)
        searchViewModel.updateAspectHeightWidthSearchView(width,aspectHeight)

    }


    /**
     * checkUserType() is a thread blocking function that is used to determine if the user is a new user, returning user or a
     * logged out user
     * */
    private fun checkUserType(view: FrameLayout,value: UserTypes){
        view.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return when(value){
                        UserTypes.NEW ->{
                            Log.d("HomeFragmentCheck","NEW")
                            findNavController().navigate(R.id.action_homeFragment_to_newUserFragment)
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        }
                        UserTypes.RETURNING ->{
                            Log.d("HomeFragmentCheck","RETURNING")
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        }
                        UserTypes.LOGGEDOUT ->{
                            Log.d("HomeFragmentCheck","LOGGEDOUT")
                            view.viewTreeObserver.removeOnPreDrawListener(this)
                            findNavController().navigate(R.id.action_homeFragment_to_logoutFragment)
                            true
                        }
                    }
                }
            }
        )
    }

    //this needs to be called when the user switches
    private fun testingPermissionAgain(context: Context) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                // This is straight up granted
                Log.d("testingPermissionAgain", "POST_NOTIFICATIONS granted")

                homeViewModel.changeGrantedNotifications(true)

            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) -> {
                // This will show If the user has granted them before but now has denied them

                Log.d("testingPermissionAgain", "Show UI to inform user why POST_NOTIFICATIONS is needed")
                homeViewModel.changeGrantedNotifications(false)//this is needs to be status denied
                //not allow the service to launch
               // homeViewModel.changeBackgroundServiceChecked(false)

            }
            else -> {
                // first time never seen them
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
                }
            }
        }
    }


    fun setImmersiveMode(window: Window){
        WindowCompat.setDecorFitsSystemWindows(window, false) // this is saying ignore the insets
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.systemBars()) //hide the insets
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    fun unsetImmersiveMode(window: Window) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.let {
            it.show(WindowInsetsCompat.Type.systemBars()) // Show the system bars first
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT // Reset to default behavior
        }
        WindowCompat.setDecorFitsSystemWindows(window, true) // Now respect insets
    }


    private fun verticalWebViewOverlayClicked(
        verticalClickableWebView: VerticalWebView
    ){
        Log.d("CLICKINGCHECKINGTHINGER","CLICKED")


            if(autoModViewModel.verticalOverlayIsVisible.value){
                autoModViewModel.setVerticalOverlayToHidden()
            }else{
                autoModViewModel.setVerticalOverlayToVisible()
            }
            verticalClickableWebView.evaluateJavascript("(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();", null);


            val jsCode2 = """
                 function printClickedToAndroid(quantities) {
        AndroidConsole.logMessage(quantities);
    }
    printClickedToAndroid("Log to the console from Javascript")
 """
            verticalClickableWebView.evaluateJavascript(jsCode2, null)

    }



}

