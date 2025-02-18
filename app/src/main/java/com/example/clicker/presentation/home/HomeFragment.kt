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
import android.provider.Settings
import android.util.Log
import android.view.DragEvent
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
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.cameraNDK.CameraNDKNativeActivity
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewDragStateViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.models.UserTypes
import com.example.clicker.presentation.home.views.HomeStreamChatViews
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
    lateinit private var myWebView:WebView




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragmentLifeCycle","onCreate")



        Log.d("BuildTypeTesting","${BuildConfig.BUILD_TYPE}")



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

        val windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        val height = windowMetrics.getBounds().height()
        val orientationIsLandscape =resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


        setListenerOnConstraintView(
            streamToBeMoved =streamToBeMoved,
            webView = view.findViewById(R.id.webView),
            height = height
        )

         myWebView = view.findViewById(R.id.webView)
        verticalWebViewOverlayClicked(myWebView as VerticalWebView)


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
                                setWebView(
                                    myWebView=myWebView,
                                    url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"
                                )

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
        } //



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
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Page has finished loading
                Log.d("WebVIewChecking", "Page loaded: $url")
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebVIewChecking", "Page started loading: $url")
            }
        }

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



    var initialY = 0f
    var initialTopMargin = 0
    var isDragging = false
    fun setListenerOnConstraintView(
        streamToBeMoved:View,
        webView: WebView,
        height:Int,
    ){

        val layoutParams = streamToBeMoved.layoutParams as FrameLayout.LayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, height, layoutParams.rightMargin, layoutParams.bottomMargin)
        streamToBeMoved.layoutParams = layoutParams


        webView.setOnTouchListener { v, event ->
            val layoutParams = streamToBeMoved.layoutParams as FrameLayout.LayoutParams

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("DragEventTesting", "DOWN")
                    initialY = event.rawY  // Store the Y touch position
                    initialTopMargin = layoutParams.topMargin // Store the initial top margin
                    isDragging = false // Reset flag
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = (event.rawY - initialY).toInt() // Calculate movement
                    Log.d("changeInACtion","dy -->$dy")

                    //this prevents the little jumps
                    if (dy > 10 || dy < -10) {
                        isDragging = true
                    }

                    if (isDragging) {
                        val newTopMargin = initialTopMargin + dy // Move the entire section up/down

                        layoutParams.topMargin = newTopMargin
                        streamToBeMoved.layoutParams = layoutParams

                        Log.d("DragEventTesting", "Top Margin: $newTopMargin")
                    }

                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    Log.d("DragEventTesting", "UP")
                    val dy = (event.rawY - initialY).toInt()
                    if (!isDragging && Math.abs(dy) < 10) {
                        Log.d("TestingDraggingTap", "WebView was tapped!") // ✅ Log when tap happens
                        val another = webView as VerticalWebView // Ensures accessibility actions
                        another.singleTapMethod()
                    }

                    val layoutParams = streamToBeMoved.layoutParams as FrameLayout.LayoutParams
                    val startY = layoutParams.topMargin
                    val endY = height // the target height you want to move to

                    if(dy >500){
                        ValueAnimator.ofInt(startY, endY).apply {
                            duration = 300 // Adjust duration for the animation
                            addUpdateListener { animator ->
                                layoutParams.setMargins(layoutParams.leftMargin, animator.animatedValue as Int, layoutParams.rightMargin, layoutParams.bottomMargin)
                                streamToBeMoved.layoutParams = layoutParams
                            }
                            start()
                        }
                        webView.loadUrl("about:blank")
                    }else{
                        ValueAnimator.ofInt(startY, 0).apply {
                            duration = 100 // Adjust duration for the animation
                            addUpdateListener { animator ->
                                layoutParams.setMargins(layoutParams.leftMargin, animator.animatedValue as Int, layoutParams.rightMargin, layoutParams.bottomMargin)
                                streamToBeMoved.layoutParams = layoutParams
                            }
                            start()
                        }
                    }
                    // Animate from startY to endY


                    isDragging = false
                    true
                }
                else -> false
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("onConfigurationChanged", "Landscape mode")
            setImmersiveMode(requireActivity().window)

            val layoutParams = myWebView.layoutParams


            layoutParams.width =ConstraintLayout.LayoutParams.MATCH_PARENT
            layoutParams.height =ConstraintLayout.LayoutParams.MATCH_PARENT

//            streamToBeMoved.layoutParams = layoutParams
            myWebView.layoutParams = layoutParams

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("onConfigurationChanged", "Portrait mode")

            val layoutParams = myWebView.layoutParams

            unsetImmersiveMode(requireActivity().window)


            //setting them back to 0 will make the it respect the aspect ratio
            layoutParams.width =0
            layoutParams.height =0

            myWebView.layoutParams = layoutParams

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
        WindowCompat.setDecorFitsSystemWindows(window, true) // this is saying respect the insets
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.let {

            it.show(WindowInsetsCompat.Type.systemBars()) // show the insets
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT // reset to default behavior
        }
    }

    private fun verticalWebViewOverlayClicked(
        verticalClickableWebView: VerticalWebView
    ){
        Log.d("CLICKINGCHECKINGTHINGER","CLICKED")
        verticalClickableWebView.singleTapMethod={

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



}

