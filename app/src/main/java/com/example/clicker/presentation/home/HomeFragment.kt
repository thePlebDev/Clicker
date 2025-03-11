package com.example.clicker.presentation.home

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewDragStateViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.models.UserTypes
import com.example.clicker.presentation.home.views.HomeStreamChatViews
import com.example.clicker.presentation.home.views.VerticalHomeStreamOverlayView
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel
import com.example.clicker.presentation.stream.AndroidConsoleInterface
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.clearHorizontalChat.ClearHorizontalChatView
import com.example.clicker.presentation.stream.customWebViews.VerticalWebView
import com.example.clicker.presentation.stream.setWebView
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.horizontalLongPress.HorizontalLongPressView
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.services.BackgroundStreamService
import com.example.clicker.services.NetworkMonitorService
import com.example.clicker.services.ScreenRecordingService
import com.example.clicker.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    //this is for the screen recorder
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mScreenDensity = 0

    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }


    /** Creates a [File] named with the current date and time */
    private fun createFile(context: Context, extension: String): File {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)

        // Save the video in the public Movies directory
        val videoDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "YourAppName")

        // Create the directory if it doesn't exist
        if (!videoDir.exists()) {
            videoDir.mkdirs()
        }

        return File(videoDir, "VID_${sdf.format(Date())}.$extension")
    }






    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragmentLifeCycle","onCreate")
      //  initializeRegisterForActivityResult()



        Log.d("BuildTypeTesting","${BuildConfig.BUILD_TYPE}")



    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
    private val SCREEN_CAPTURE_REQUEST_CODE = 2001


    // Method to request screen capture permission
    private fun requestScreenCapture() {
        val projectionManager =
            requireContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = projectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, SCREEN_CAPTURE_REQUEST_CODE)
    }
//    this function data needs to be sent to the function above

    private var mediaProjection: MediaProjection? = null
    private lateinit var startMediaProjection: ActivityResultLauncher<Intent>

    private fun registerRecordingToken() {
        val mediaProjectionManager = getSystemService(requireContext(),MediaProjectionManager::class.java)

        if (mediaProjectionManager != null) {
            startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent())
        }
    }
    fun initializeRegisterForActivityResult(){
        val mediaProjectionManager = getSystemService(requireContext(),MediaProjectionManager::class.java)

        // Initialize registerForActivityResult outside the function
        startMediaProjection = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && mediaProjectionManager != null) {
                Log.d("TESTINGTHEFRAGMENTCODE","SEND INTENT")
                //THIS IS WHERE WE SEND THE INTENT




                //end of it
                val startIntent = Intent(requireActivity(), ScreenRecordingService::class.java)
                startIntent.action = BackgroundStreamService.Actions.START.toString()
                startIntent.putExtra("code", result.resultCode);
                startIntent.putExtra("data", result.data!!);
                startIntent.putExtra("output_file_path", outputFile.absolutePath)
                startIntent.putExtra("width", mScreenWidth);
                startIntent.putExtra("height", mScreenHeight);
                startIntent.putExtra("density", mScreenDensity);
                requireActivity().startService(startIntent)
                //mediaProjection = mediaProjectionManager.getMediaProjection(result.resultCode, result.data!!)
            }else{

                Log.d("TESTINGTHEFRAGMENTCODE","not good")
            }
        }
    }

    // Handle the result of the screen capture permission request
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCREEN_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            startScreenRecordingService(data)
        }
    }

    // Method to start the screen recording service
    private fun startScreenRecordingService(data: Intent) {
        val startIntent = Intent(requireActivity(), ScreenRecordingService::class.java)
        startIntent.action = BackgroundStreamService.Actions.START.toString()
        requireActivity().startService(startIntent)
    }

    // Sample method to call startForegroundService with a notification (for foreground service)
    private fun startForegroundService(serviceIntent: Intent) {
        val notification = NotificationCompat.Builder(requireContext(), "YOUR_CHANNEL_ID")
            .setContentTitle("Screen Recording")
            .setContentText("Recording in progress...")
            .setSmallIcon(R.drawable.autorenew_24)
            .setOngoing(true)
            .build()

        // Start the service with the notification and the appropriate foreground service type
//        ServiceCompat.startForeground(
//            requireContext(),
//            99,  // Notification ID
//            notification,
//            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
//        )

        // Start the service itself
        requireContext().startService(serviceIntent)
    }
    private val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    private val REQUEST_CODE = 100
    fun requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), RECORD_AUDIO_PERMISSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(RECORD_AUDIO_PERMISSION), REQUEST_CODE)
        }
    }

    fun getVideoInfo(filePath: String) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)

        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)

        Log.d("TestingBitRateThinger","Duration: $duration ms")
        Log.d("TestingBitRateThinger","Width: $width px")
        Log.d("TestingBitRateThinger","Height: $height px")
        Log.d("TestingBitRateThinger","Bitrate: $bitrate bps")

        retriever.release()
    }



    @SuppressLint("SuspiciousIndentation")
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

         newWebView = binding.webViewTestingMovement // the webView showing the stream

        val windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        val height = windowMetrics.getBounds().height()
        val orientationIsLandscape =resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE



        moveContainerOffScreen(
            streamToBeMoved =streamToBeMoved,
            height = height
        )

        setAndMoveGestureDetection(
            webView=newWebView
        )


        getWebViewHeight(
            webView = newWebView
        )






        if(value !=UserTypes.NEW){
            requestAudioPermission()

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
                                animateContainerToScreenTop(
                                    containerViewToBeMoved=streamToBeMoved,
                                    startY=height,
                                    endY = 0
                                )
                                val channelName = streamViewModel.channelName.value
                                Log.d("CHANNELNAMENONENGLISH", "channelName -->$channelName")
                                Log.d("CLICKEDtOnAVIGAE","CLICKED WEBSOCKET")
                            },
                            hapticFeedBackError = {
                              //  view.performHapticFeedback(HapticFeedbackConstants.REJECT)
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
                                val isLandScape =resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                                if(isLandScape){
                                    streamViewModel.setImmersiveMode(true)
                                   val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                                    if(homeViewModel.clickedStreamerName.value != channelName){
                                        Log.d("ChannelNameTestingthingers","homeViewModel.clickedStreamerName.value != channelName")
                                        setWebViewAndLoadURL(
                                            myWebView=newWebView,
                                            url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"
                                        )

                                    }


                                    animateContainerToScreenTop(
                                        containerViewToBeMoved=streamToBeMoved,
                                        startY=screenHeight,
                                        endY = 0
                                    )

                                    animateToTopLeftCon( //this should be it
                                        newWebView,
                                        screenWidth
                                    )
                                    horizontalFullScreenTap = false //this is for the overlay
                                    smallHeightPositioned=false
                                }else{

                                if(homeViewModel.clickedStreamerName.value != channelName){
                                    Log.d("ChannelNameTestingthingers","homeViewModel.clickedStreamerName.value != channelName")
                                    setWebViewAndLoadURL(
                                        myWebView=newWebView,
                                        url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"
                                    )

                                }


                                isDraggingWebView = false

                                    //todo: THis where the animation function should go
                                    animateWebViewToFullScreenVertical(
                                        webView = newWebView
                                    )

                                    if(smallHeightPositioned){
                                        animateChatVerticalMiniToFullScreen()
                                    }
                                smallHeightPositioned = false


                        }//end of the else
                            },
                            webViewAnimation = { channelName->
                                homeViewModel.setShowHomeChat(true)

                                animateContainerToScreenTop(
                                    containerViewToBeMoved=streamToBeMoved,
                                    startY=windowMetrics.getBounds().height(),
                                    endY = 0
                                )
                                val isLandScape =resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                                if(isLandScape){
                                    streamViewModel.setImmersiveMode(true)
                                    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                                    if(homeViewModel.clickedStreamerName.value != channelName){
                                        setWebViewAndLoadURL(
                                            myWebView=newWebView,
                                            url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"
                                        )

                                    }
                                    animateContainerToScreenTop(
                                        containerViewToBeMoved=streamToBeMoved,
                                        startY=screenHeight,
                                        endY = 0
                                    )

                                    animateToTopLeftCon( //this should be it
                                        newWebView,
                                        screenWidth
                                    )
                                    horizontalFullScreenTap = false //this is for the overlay
                                    smallHeightPositioned=false
                                }else{

                                    if(homeViewModel.clickedStreamerName.value != channelName){
                                        setWebViewAndLoadURL(
                                            myWebView=newWebView,
                                            url="https://player.twitch.tv/?channel=$channelName&controls=false&muted=false&parent=modderz"
                                        )

                                    }


                                    isDraggingWebView = false

                                    //todo: THis where the animation function should go
                                    animateWebViewToFullScreenVertical(
                                        webView = newWebView
                                    )

                                    if(smallHeightPositioned){
                                        animateChatVerticalMiniToFullScreen()
                                    }
                                    smallHeightPositioned = false

                                }//end of the else
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

            binding.horizontalClearChat.apply {
                val activity2 = activity as? Activity


                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    if(streamViewModel.advancedChatSettingsState.value.horizontalClearChat && streamViewModel.immersiveMode.value){

                        AppTheme{
                            ClearHorizontalChatView(
                                streamViewModel,
                                chatSettingsViewModel=chatSettingsViewModel,
//                        offsetX = chatSettingsViewModel.clearChatOffsetX.value,
//                        changeOffsetXClearChat = {delta-> chatSettingsViewModel.changeOffsetXClearChat(delta) }

                            )
                        }
                    }


                }
            }
            binding.composeViewLongPress.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    AppTheme {
                        HorizontalLongPressView(
                            homeViewModel,
                            streamViewModel = streamViewModel,
                            loadURL ={newUrl ->
                                //TODO: THIS NEEDS TO BE IMPLEMENTED WITH THE NEW LOADURL
                               // Log.d("TESTINGNEWOUTURL","url -->$newUrl")
//                                val regex = "(?<=channel=)[^&]+".toRegex()
//                                val match = regex.find(newUrl)?.value?:""
//                                Log.d("TESTINGNEWOUTURL","clicked stream -->${homeViewModel.clickedStreamerName.value}")
//                                Log.d("TESTINGNEWOUTURL","clicked stream -->$match")
//                                if(homeViewModel.clickedStreamerName.value != match ){

                          //      Log.d("TESTINGNEWOUTURL","clicked stream -->$match")
                                    setWebViewAndLoadURL(
                                        myWebView=newWebView,
                                        url=newUrl
                                    )

                               // }
                             //   setWebView(webView,newUrl)
                                     },
                            createNewTwitchEventWebSocket ={modViewViewModel.createNewTwitchEventWebSocket()},
                            updateClickedStreamInfo={clickedStreamInfo ->  streamViewModel.updateClickedStreamInfo(clickedStreamInfo)},
                            updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
                                modViewViewModel.updateAutoModTokens(
                                    oAuthToken =oAuthToken,
                                    clientId =clientId,
                                    broadcasterId=broadcasterId,
                                    moderatorId =moderatorId
                                )
                            },
                            updateStreamerName = { streamerName, clientId,broadcasterId,userId->
                                streamViewModel.updateChannelNameAndClientIdAndUserId(
                                    streamerName,
                                    clientId,
                                    broadcasterId,
                                    userId,
                                    login =homeViewModel.validatedUser.value?.login ?:"",
                                    oAuthToken= homeViewModel.oAuthToken.value ?:""
                                )
                                streamInfoViewModel.getStreamInfo(
                                    authorizationToken= homeViewModel.oAuthToken.value ?:"",
                                    clientId = clientId,
                                    broadcasterId= broadcasterId
                                )
                            }
                        )
                    }
                }

            }

        }



        return view
    }

    /**
     * getWebViewHeight uses a [ViewTreeObserver.OnGlobalLayoutListener] to determine the height of the
     * [webView] and set it as the max height that a webView can become
     *
     * @param webView a [WebView] object that will be used when a user wants to see a stream
     * */
    private fun getWebViewHeight(
        webView: WebView
    ){
        webView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val metrics = Resources.getSystem().displayMetrics
                mScreenDensity = metrics.densityDpi
                mScreenHeight =Resources.getSystem().displayMetrics.heightPixels
                mScreenWidth = Resources.getSystem().displayMetrics.widthPixels
                webView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                maxHeightNewWebView = webView.height

            }
        })

    }


    /**
     * animateWebViewToFullScreenVertical
     * */
    fun animateWebViewToFullScreenVertical(
        webView: WebView
    ){

        val verticalWebViewAnimationX =
            SpringAnimation(webView, SpringAnimation.X).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }
        val verticalWebViewAnimationY =
            SpringAnimation(webView, SpringAnimation.Y).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
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
            doOnEnd {
                verticalWebViewAnimationX.animateToFinalPosition(0f)
                verticalWebViewAnimationY.animateToFinalPosition(0f)
            }


            start()
        }
    }

    /**
     * setWebViewAndLoadURL enables all the neccessary features inside of the [myWebView] param and then loads the url specified
     * inside of the [url] parameter
     *
     * @param myWebView a [WebView] object that will show the stream to the user
     * @param url a String value that will be loaded into the [myWebView]
     * */
    private fun setWebViewAndLoadURL(
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


    /**
     * animateContainerToScreenTop used to animate the container view of [containerViewToBeMoved] from its starting Y position
     * of [startY] to its end position of [endY].
     *
     * @param containerViewToBeMoved a [View] object that will be moved by the animation inside of this function
     * @param startY a Int value used as the starting position of [containerViewToBeMoved]
     * @param endY a Int value used as the ending position of [containerViewToBeMoved]
     * */
    private fun animateContainerToScreenTop(
        containerViewToBeMoved:View,
        startY:Int,
        endY:Int
    ){
        val layoutParams = containerViewToBeMoved.layoutParams as FrameLayout.LayoutParams
        Log.d("ComposeViewTestingHeight","height -->${binding.streamComposeView.height}")
        Log.d("ComposeViewTestingHeight","Y -->${binding.streamComposeView.y}")

        ValueAnimator.ofInt(startY, endY).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                layoutParams.setMargins(layoutParams.leftMargin, animator.animatedValue as Int, layoutParams.rightMargin, layoutParams.bottomMargin)
                containerViewToBeMoved.layoutParams = layoutParams
            }
            start()
        }

    }


    /**
     * horizontalAnimateWidthAndXPosition animates the [webView's][webView] position on the X-axis and width to the size
     * specified by [finalWidth]
     *
     * @param webView a [View] object that will be animated inside of this function
     * @param finalWidth a Int value used to animate [webView] to the final width
     *
     * */
    private fun horizontalAnimateWidthAndXPosition(
        webView: View,
        finalWidth:Int

    ){

        val horizontalSpringAnimationWebViewX = SpringAnimation(webView, SpringAnimation.X).apply {
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

        horizontalSpringAnimationWebViewX.animateToFinalPosition(0f)


    }
    /**
     * animateHorizontalOverlay animates the horizontal overlay that is shown to the user when a double click occurs.
     * The animation is done on the X-axis
     *
     * */
    fun animateHorizontalOverlay(){
        val horizontalOverlaySpringAnimationX = SpringAnimation(binding.horizontalOverlay, SpringAnimation.X).apply {
            spring = SpringForce().apply {
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                stiffness = SpringForce.STIFFNESS_LOW // Lower = smoother motion
            }
        }
        horizontalOverlaySpringAnimationX.animateToFinalPosition(0f)
    }

    /**
     * moveContainerOffScreen uses [FrameLayout.LayoutParams] to move the [streamToBeMoved] parameter off screen by using the
     * specified [height] value
     *
     * @param streamToBeMoved a [View] object that will be moved off screen
     * @param height a Int value that should total the height of the screen
     * */
    private fun moveContainerOffScreen(
        streamToBeMoved:View,
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
    var horizontalLastX = 0f

    private var horizontalFullScreenTap = false
    private var longPressView = false

/**
 * setAndMoveGestureDetection sets a [GestureDetector](https://developer.android.com/reference/android/view/GestureDetector) on the
 * [webView] parameter. This detector is what is going to allow the webView to respond to drag event, single tap and double tap events
 *
 *
 * @param webView a [WebView] object that will show the stream the user wants to watch and interact with
 * */
    private fun setAndMoveGestureDetection(webView: WebView) {
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
            override fun onLongPress(e: MotionEvent) {
                val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                if(isLandscape &&!smallHeightPositioned){
                    if(!horizontalFullScreenTap && !longPressView){
                        Log.d("testingTheLongPress","IN NEW FEATURE")


                        longPressWebViewAnimation(webView = webView)

                        //TODO:SHOW THE FEATURE
                        val longPressView = binding.root.findViewById<ComposeView>(R.id.compose_view_long_press)
                        val horizontalSpringAnimationX =
                            SpringAnimation(longPressView, SpringAnimation.X).apply {
                                spring = SpringForce().apply {
                                    dampingRatio =
                                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                                    stiffness =
                                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                                }
                            }
                        val horizontalSpringAnimationY =
                            SpringAnimation(longPressView, SpringAnimation.Y).apply {
                                spring = SpringForce().apply {
                                    dampingRatio =
                                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                                    stiffness =
                                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                                }
                            }


                        val params = longPressView.layoutParams
                        //this is animating its width
                        val newLongPressWidth = (webView.width * 0.35).toInt()
                        ValueAnimator.ofInt(params.width, newLongPressWidth).apply {
                            duration = 300 // Adjust duration for the animation
                            addUpdateListener { animator ->
                                val longPressViewParams = longPressView.layoutParams
                                longPressViewParams.width = animator.animatedValue as Int
                                longPressView.layoutParams = longPressViewParams
                            }

                            start()
                        }
                        val windowManager =
                            context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        val metrics = windowManager.currentWindowMetrics
                        //CHAT HEIGHT
                        val screenHeightTesting = metrics.bounds.height()


                        Log.d("TestingChatHeight", "heightTesting-->${screenHeightTesting}")

                        ValueAnimator.ofInt(params.height, screenHeightTesting).apply {
                            duration = 300 // Adjust duration for the animation
                            addUpdateListener { animator ->
                                val longPressViewParams = longPressView.layoutParams
                                longPressViewParams.height = animator.animatedValue as Int
                                longPressView.layoutParams = longPressViewParams
                            }
                            doOnEnd {
                                // animation done inside of doOnEnd to make sure we get the proper values
                                val width = webView.width.toFloat()
                                Log.d("TESTINGWEBVIEWwIDTH", "WIDTH-->$width")
                                horizontalSpringAnimationX.animateToFinalPosition(0f)//need to delete this magic number
                                horizontalSpringAnimationY.animateToFinalPosition(0f)//this one is good
                            }

                            start()
                        }
                        longPressView.visibility = View.VISIBLE




                    }else{
                        Log.d("testingTheLongPress","OUT NEW FEATURE")

                    }




                }
                super.onLongPress(e)
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onDoubleTap(e: MotionEvent): Boolean {
                binding.composeViewLongPress.visibility = View.GONE
                val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                Log.d("testinghorizontalfullscreen","horizontalFullScreenTap -->${horizontalFullScreenTap}")
                Log.d("testinghorizontalfullscreen","isLandscape -->${isLandscape}")
                if(!smallHeightPositioned) {

                    if (isLandscape) {

                        if (!horizontalFullScreenTap) {
                            //THIS CONDITIONAL MEANS THAT WE HAVE JUST ROTATED AND ARE IN THE FULL SCREEN
                            //BUT HAVE NOT DOUBLE TAPPED IT YET
                            Log.d("horizontalFullScreenTaptesting","FIRST DOUBLE TAP")
                            //I changed this
                            streamViewModel.setImmersiveMode(true)


                            val newWidth = (webView.width * 0.65).toInt()
                            val fullWebViewWidth = webView.width
                            val chatView = binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
                            // TODO: ANIMATE THE WIDTH,X and y
                            chatView.visibility = View.VISIBLE
                            animateWidthToIncludeChatHorizontalDoubleTap(
                                webView = webView,
                                width=newWidth,
                            )



                            chatAnimateWidthToIncludeChatHorizontalDoubleTap(
                                chatView = chatView,
                                fullWebViewWidth=fullWebViewWidth,
                                threeQuartersWidth = newWidth.toFloat()
                                    )


                            horizontalFullScreenTap = true


                        } else {
                            Log.d("horizontalFullScreenTaptesting","horizontalFullScreenTap")
                            streamViewModel.setImmersiveMode(true)
                            longPressView = false

                            //I want to make the chat invisible
                            val chatView =
                                binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
                            chatView.visibility = View.INVISIBLE
                            //move the stream back to full screen
                            //SFGSDGDS
                            //todo: JUST REPEAT WHAT i HAVE BEEN DOING WITH THE NEW FULL SCREEN
                            animateToTopLeftCon( //this should be it
                                newWebView,
                                screenWidth
                            )

                            //move the overlay
                            horizontalFullScreenTap = false


                        }

                    }
                }



               Log.d("GestureDetectorTapping","DOUBLE")
                return true
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                val composeView = binding.streamComposeView

                Log.d("TestingWebViewHeight", "chat height layout: ${composeView.height}")

                // ofInt() gives the start and end value
                //this is where it goes
                if(isLandscape){

                    if(!smallHeightPositioned){
                        //todo:THIS NEEDS TO BE FIXED

                            val composeView = binding.root.findViewById<ComposeView>(R.id.horizontal_overlay)
                            composeView.visibility = View.VISIBLE

                            //sets the overlay good enough
                        if(!longPressView){
                            composeView.x = 0f
                        }

                            //this is to animate the overlay and show it
                            horizontalAnimateSingleTap(
                                webView
                            )
                            Log.d("ORIENTATIONtESTIN","HORIZONTAL SINGLE TAP big view")



                    }else{
                        streamViewModel.setImmersiveMode(true)
                        binding.streamComposeView.visibility=View.GONE

                        animateContainerToScreenTop(
                            containerViewToBeMoved=streamToBeMoved,
                            startY=screenHeight,
                            endY = 0
                        )

                        animateToTopLeftCon( //this should be it
                            newWebView,
                            screenWidth
                        )
                        //move the overlay
                        horizontalFullScreenTap = false //this is for the overlay
                        smallHeightPositioned=false


                    }


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
                val newScreenHeight =Resources.getSystem().displayMetrics.heightPixels
                val newScreenWidth =Resources.getSystem().displayMetrics.widthPixels

                //DO NOTHING RIGHT NOW
                //TODO  START OF THE HORIZONTAL ANIMATIONS
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        horizontalInitialWebViewY = event.rawY
                        horizontalInitialWebViewX = event.rawX
                        horizontalLastY = webView.y
                        horizontalLastX = webView.x

                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dy =  event.rawY - horizontalInitialWebViewY
                        val dx =  event.rawX-horizontalInitialWebViewX

                        //this is to determine if the view is dragging
                        if (dy > 40 || dy < -40) {
                            horizontalIsDraggingWebView = true
                        }

                        if(horizontalIsDraggingWebView){
                            val minWidth =(minHeight * 16 / 9)
                            val newY =(horizontalLastY + dy).coerceIn(0f, (newScreenHeight - minHeight).toFloat())
                            val newXDragging =(horizontalLastX + dx).coerceIn(0f, (newScreenWidth - minWidth).toFloat())
                            //this is moving the horizontal stream on the Y-axis
                            springAnimation.animateToFinalPosition(newY)
                            //todo: I need the shrinking animation

                            val newHeight = (lastHeight - dy).toInt().coerceIn(minHeight, maxHeightNewWebView)
                            val newWidth = (newHeight * 16 / 9).toInt()

                           // webView.y = (lastY + dy).coerceIn(0f, (screenHeight - 200 - minHeight).toFloat())
                            if(smallHeightPositioned){
                                Log.d("smallHeightPositionedTestingHorizontal","smallHeightPositioned-->${smallHeightPositioned}")
                                springAnimationX.animateToFinalPosition(newXDragging)
                            }
                            springAnimation.animateToFinalPosition(newY)
                            Log.d("HorizontalheightConditional","-->${webView.y > (Resources.getSystem().displayMetrics.heightPixels/2)}")

                            if (webView.y > (Resources.getSystem().displayMetrics.heightPixels/2)) {
                                Log.d("TESITNGaNIMATIONSPECS","TRUE")

                            }else{
                                Log.d("TESITNGaNIMATIONSPECS","FALSE")
                                if(!smallHeightPositioned){
                                    //animate the height and width
                                    val params = webView.layoutParams
//                                    params.width = newWidth
//                                    params.height = newHeight
//                                    webView.layoutParams = params
                                    //  webView.x = newX



                                    //this is animating its width
                                    ValueAnimator.ofInt(params.width, newWidth).apply {
                                        duration = 300 // Adjust duration for the animation
                                        addUpdateListener { animator ->
                                            params.width = animator.animatedValue as Int
                                          //  params.height = newHeight
                                            webView.layoutParams = params
                                        }

                                        start()
                                    }
                                    //this is animating its width
                                    ValueAnimator.ofInt(params.height, newHeight).apply {
                                        duration = 300 // Adjust duration for the animation
                                        addUpdateListener { animator ->
                                            params.height = animator.animatedValue as Int
                                            //  params.height = newHeight
                                            webView.layoutParams = params
                                        }


                                        start()
                                    }
                                    // todo: below needs to be uncommented out
                                    smallHeightPositioned=true
                                }

                            }

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

                        horizontalIsDraggingWebView = false


                        //THIS IS THE CONDITIONAL TO CHECK IF IT SHOULD COLLAPSE
                        val testing =streamToBeMoved.y
                        if(testing==0f && smallHeightPositioned){
                            streamViewModel.setImmersiveMode(false)
                            binding.composeViewLongPress.visibility = View.GONE
                            animateContainerToScreenTop(
                                containerViewToBeMoved=streamToBeMoved,
                                startY=0,
                                endY = screenHeight
                            )
                        }else{
                            Log.d("shouldAnimateBackToTop","BACK TO TOP")
                        }


                        true
                    } //END OF  MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
                    else -> false
                }
                 true

            }//todo: END OF THE HORIZONTAL ANIMATIONS
            else{

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


                            val newHeight = (lastHeight - dy).toInt().coerceIn(minHeight, maxHeightNewWebView)
                            val newWidth = (newHeight * 16 / 9).toInt()
                            if (newHeight == minHeight){
                                smallHeightPositioned = true
                            }


                            if (webView.y > halfwayPoint) {

                            }else{
                                if(!smallHeightPositioned){
                                    Log.d("WebViewPDositionCHeckingtESTING", "SHRINK XY")
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
                            animateContainerToScreenTop(
                                containerViewToBeMoved=streamToBeMoved,
                                startY=0,
                                endY = screenHeight
                            )
                        }else {
                            if (!smallHeightPositioned) {
                                animateWebViewVerticalFullScreen(
                                    webView,
                                    lastHeight
                                )


                                }
                        }


                        true
                    } //END OF  MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
                    else -> false
                }
            }



        }
    }


    fun longPressWebViewAnimation(
        webView:WebView
    ){
        horizontalFullScreenTap = true
        longPressView = true
        val chatView = binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
        chatView.visibility = View.INVISIBLE
        val newWidth = (webView.width * 0.65).toInt()
        val webParams = webView.layoutParams
        ValueAnimator.ofInt(webParams.width, newWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                webParams.width = animator.animatedValue as Int
                webView.layoutParams = webParams
            }


            start()
        }
        val totalScreenWidth =Resources.getSystem().displayMetrics.widthPixels
        ValueAnimator.ofInt(webView.x.toInt(),totalScreenWidth-newWidth ).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.x = value.toFloat()
                binding.horizontalOverlay.x = (animator.animatedValue as Int).toFloat()
            }
            start()
        }
    }

    /*************START ANIMATIONS FOR THE DOUBLE TAP IN HORIZONTAL****************/

    /**
     * animateHeightAndWidthHorizontalDoubleTap animates the width and the X value of the [webView] to make room for the
     * chat
     * @param webView a [WebView] object that will be animated based on the value of [width]
     * @param width a Int value that is used to animate the [webView]
     * */
    @RequiresApi(Build.VERSION_CODES.R)
    fun animateWidthToIncludeChatHorizontalDoubleTap(
        webView: WebView,
        width:Int,
    ){
        ValueAnimator.ofInt(webView.y.toInt(), 0).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.y = value.toFloat()
            }

            doOnEnd {
                ValueAnimator.ofInt(webView.x.toInt(), 0).apply {
                    duration = 300 // Adjust duration for the animation
                    addUpdateListener { animator ->
                        val value = animator.animatedValue as Int
                        webView.x = value.toFloat()
                    }


                    start()
                }

            }


            start()
        }
        val webParams = webView.layoutParams
        ValueAnimator.ofInt(webParams.width, width).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                webParams.width = animator.animatedValue as Int
                webView.layoutParams = webParams
            }


            start()
        }



    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun chatAnimateWidthToIncludeChatHorizontalDoubleTap(
        chatView: ComposeView,
        fullWebViewWidth:Int,
        threeQuartersWidth:Float,

    ){


        chatView.visibility = View.VISIBLE
        val windowManager =
            context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = windowManager.currentWindowMetrics
        val screenHeightForChat = metrics.bounds.height()

        val horizontalSpringAnimationX =
            SpringAnimation(chatView, SpringAnimation.X).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }
        val horizontalSpringAnimationY =
            SpringAnimation(chatView, SpringAnimation.Y).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }


        val params = chatView.layoutParams
        //this is animating its width
        val newChatWidth = (fullWebViewWidth * 0.35).toInt()
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
        val screenHeightTesting = metrics.bounds.height()


        Log.d("TestingChatHeight", "heightTesting-->${screenHeightTesting}")

        ValueAnimator.ofInt(params.height, screenHeightTesting).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val chatParams = chatView.layoutParams
                chatParams.height = animator.animatedValue as Int
                chatView.layoutParams = chatParams
            }
            doOnEnd {
                // animation done inside of doOnEnd to make sure we get the proper values
              //  val width = webView.width.toFloat()
                //Log.d("TESTINGWEBVIEWwIDTH", "WIDTH-->$width")
                horizontalSpringAnimationX.animateToFinalPosition(threeQuartersWidth)//need to delete this magic number
                horizontalSpringAnimationY.animateToFinalPosition(0f)//this one is good
            }

            start()
        }
            }






    /*************END ANIMATIONS FOR THE DOUBLE TAP IN HORIZONTAL****************/

    /**
     * animateWebViewVerticalFullScreen animates the [webView] back to its original height and width based on [lastHeight] value
     *
     * @param webView a [WebView] object that will be animated
     * @param lastHeight a Int value used to determine the width and height of the [webView]
     * */
    private fun animateWebViewVerticalFullScreen(
        webView: WebView,
        lastHeight:Int
    ){
        Log.d("shouldAnimateBackToTop", "BACK TO TOP")
        //animate the Y and width/height
        //todo: checn this animate
        val horizontalSpringAnimationY =
            SpringAnimation(webView, SpringAnimation.Y).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }

        val params = webView.layoutParams

        ValueAnimator.ofInt(params.width, (lastHeight * 16 / 9)).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                params.width = animator.animatedValue as Int
                //  params.height = newHeight
                webView.layoutParams = params
            }

            start()
        }
        //this is animating its width
        ValueAnimator.ofInt(params.height, lastHeight).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                params.height = animator.animatedValue as Int
                //  params.height = newHeight
                webView.layoutParams = params
            }
            doOnEnd {
                horizontalSpringAnimationY.animateToFinalPosition(0f)
            }


            start()
        }
    }

    /**
     * THIS NEEDS TO BE DOCUMENTED
     * */
    fun animateHorizontalSmallSizeToFullScreen(
        screenHeight:Int,

    ){
        //todo: copy this
        animateContainerToScreenTop(
            containerViewToBeMoved=streamToBeMoved,
            startY=screenHeight,
            endY = 0
        )
        //animate to full screen size
        //I want to make the chat invisible
        val chatView = binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
        chatView.visibility = View.INVISIBLE

        //move the stream back to full screen
        animateHorizontalWidthAndHeightToFullScreen(
            newWebView,
            Resources.getSystem().displayMetrics.heightPixels,
        )
        //todo: I need to implement the same animations as
        animateToTopLeftScreen(
            newWebView,
            Resources.getSystem().displayMetrics.widthPixels
        )

        Handler(Looper.getMainLooper()).postDelayed(
            {
                animateHorizontalOverlayToCenter(
                    overlay  = binding.horizontalOverlay,
                    maxWidth = Resources.getSystem().displayMetrics.widthPixels
                )
            },
            1000 // value in milliseconds
        )

        //move the overlay
        horizontalFullScreenTap = false //this is for the overlay
        smallHeightPositioned=false

    }

    @RequiresApi(Build.VERSION_CODES.R)
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
            Log.d("AnimateVerticalTesting","TESTING")
            animateContainerToScreenTop(
                containerViewToBeMoved=streamToBeMoved,
                startY=screenHeight,
                endY = 0
            )
            //todo: I need to animate chat back to zero
            val streamingWebViewHeight2 =binding.webViewTestingMovement.height
            val bottomBarHeight =getNavigationBarHeight()

            Log.d("TestingChat2MovingHeight","608 -->${maxHeightNewWebView}")
            Log.d("TestingChat2MovingHeight","63 -->${bottomBarHeight}")
            Log.d("TestingChat2MovingHeight","671 -->${bottomBarHeight +streamingWebViewHeight2}")
            Log.d("TestingChat2MovingHeight","1611 -->${binding.streamingModalView.height-(maxHeightNewWebView)}")
            Log.d("TestingChat2MovingHeight","1611? -->${binding.testingConstraintAgain.height-maxHeightNewWebView}")
            //animate the width and then the x position

            //animate x-y
            animateChatVerticalMiniToFullScreen()



            //animate height and width
        } else{
            //webview is in full screen form and is being tapped
            verticalWebViewOverlayClicked(webView as VerticalWebView)
        }

        smallHeightPositioned = false
    }


    fun animateChatVerticalMiniToFullScreen(){
        val chatView = binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
        chatView.visibility = View.VISIBLE

        val verticalChatAnimationX =
            SpringAnimation(chatView, SpringAnimation.X).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }
        val verticalChatAnimationY =
            SpringAnimation(chatView, SpringAnimation.Y).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }
        val params = chatView.layoutParams

        val screeWidth = Resources.getSystem().displayMetrics.widthPixels

        Log.d("CHATHEIGHTTESTING","heightPixels -->${Resources.getSystem().displayMetrics.heightPixels.toFloat()}")

        //TODO: ADD THIS CHAT ANIMATION TO FUNCTION
        //ADD THIS CHAT ANIMATION TO WHEN USER CLICKS ON NEW VIDEO FOR VERTICAL
        //ANIMATING CHAT HEIGHT
        ValueAnimator.ofInt(params.height, binding.testingConstraintAgain.height-maxHeightNewWebView).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val chatParams = chatView.layoutParams
                chatParams.height = animator.animatedValue as Int
                chatView.layoutParams = chatParams
            }


            start()
        }
        ValueAnimator.ofInt(params.width, screeWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val chatParams = chatView.layoutParams
                chatParams.width = animator.animatedValue as Int
                chatView.layoutParams = chatParams
            }
            doOnEnd {
                val heightTesting =Resources.getSystem().displayMetrics.heightPixels.toFloat()
                verticalChatAnimationX.animateToFinalPosition(0f)
                verticalChatAnimationY.animateToFinalPosition(maxHeightNewWebView.toFloat())
            }

            start()
        }

    }

    fun testingVerticalAnimationAgain(){
        val chatView = binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
        chatView.visibility = View.VISIBLE

        val verticalChatAnimationX =
            SpringAnimation(chatView, SpringAnimation.X).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }
        val verticalChatAnimationY =
            SpringAnimation(chatView, SpringAnimation.Y).apply {
                spring = SpringForce().apply {
                    dampingRatio =
                        SpringForce.DAMPING_RATIO_NO_BOUNCY // Adjust bounce (0 = bouncy, 1 = smooth)
                    stiffness =
                        SpringForce.STIFFNESS_LOW // Lower = smoother motion
                }
            }
        val params = chatView.layoutParams

        val screeWidth = Resources.getSystem().displayMetrics.widthPixels

        Log.d("CHATHEIGHTTESTING","heightPixels -->${Resources.getSystem().displayMetrics.heightPixels.toFloat()}")

        //TODO: ADD THIS CHAT ANIMATION TO FUNCTION
        //ADD THIS CHAT ANIMATION TO WHEN USER CLICKS ON NEW VIDEO FOR VERTICAL
        //ANIMATING CHAT HEIGHT
        ValueAnimator.ofInt(params.height, binding.testingConstraintAgain.height-maxHeightNewWebView).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val chatParams = chatView.layoutParams
                chatParams.height = animator.animatedValue as Int
                chatView.layoutParams = chatParams
            }


            start()
        }
        ValueAnimator.ofInt(params.width, screeWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val chatParams = chatView.layoutParams
                chatParams.width = animator.animatedValue as Int
                chatView.layoutParams = chatParams
            }
            doOnEnd {
                val heightTesting =Resources.getSystem().displayMetrics.heightPixels.toFloat()
                verticalChatAnimationX.animateToFinalPosition(0f)
                verticalChatAnimationY.animateToFinalPosition(maxHeightNewWebView.toFloat())
            }

            start()
        }
    }
    fun horizontalAnimateSingleTap(
        webView: WebView
    ){
        val composeView = binding.root.findViewById<ComposeView>(R.id.horizontal_overlay)
        composeView.visibility = View.VISIBLE

        animateHeightComposeView(
            composeView,
            (webView.height*0.40).toInt()
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
        webView: View,
        maxHeight: Int,
    ){

        //animating the Y position
        ValueAnimator.ofInt(webView.y.toInt(), 0).apply {
            duration = 100 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.y = value.toFloat()
            }
            doOnEnd {
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

            } //end of the doOnEnd
            start()
        }

    }




    @RequiresApi(Build.VERSION_CODES.R)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //safe guard agains the situations where no stream has been clicked

        binding.composeViewLongPress.visibility = View.GONE



        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setImmersiveMode(requireActivity().window)

            if(homeViewModel.clickedStreamerName.value.length >1) {
                binding.horizontalClearChat.visibility = View.VISIBLE



                val rootView = requireActivity().window.decorView
                rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                        val screenWidth = Resources.getSystem().displayMetrics.widthPixels

                        if (smallHeightPositioned) {
                            animateXAndYToBottomRightScreen(
                                screenHeight = screenHeight,
                                screenWidth = screenWidth
                            )
                        } else {
                            Log.d("ORIENTATIONCHECKING","streamViewModel.SETIMMERSIVEMODE()")
                            streamViewModel.setImmersiveMode(true)
                            val chatView =
                                binding.root.findViewById<ComposeView>(R.id.stream_compose_view)
                            chatView.visibility = View.INVISIBLE

                            animateToTopLeftCon(
                                newWebView,
                                screenWidth
                            )
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    animateHorizontalOverlayToCenter(
                                        overlay = binding.horizontalOverlay,
                                        maxWidth = Resources.getSystem().displayMetrics.widthPixels
                                    )
                                },
                                1000 // value in milliseconds
                            )

                        }

                        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this) // Prevent multiple calls
                    }
                })
                Log.d("onConfigurationChangedTesting", "Landscape mode")
                //todo: this is not working properly
            }







        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            horizontalFullScreenTap = false
            streamViewModel.setImmersiveMode(false)



            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            Log.d("TestingScreenHeight", "portrait -->: ${screenHeight}")
            unsetImmersiveMode(requireActivity().window)
            if(homeViewModel.clickedStreamerName.value.length >1) {

            if(smallHeightPositioned){
                animateXAndYToBottomRightScreen(
                    screenHeight=screenHeight,
                    screenWidth=screenWidth
                )
                //this might not be needed, I am not sure
                animateHeightAndWidth(
                    webView =newWebView,
                    newWidth = (minHeight * 16 / 9)
                )
            }else {
                //This animates back to the home page
                val newWebViewTesting = binding.webViewTestingMovement
                val placeHolder = binding.webView

                animateToFullScreen(
                    newWebViewTesting,
                    maxHeightNewWebView
                )
                //I need to animate the red place holder
                animateToFullScreen(
                    placeHolder,
                    maxHeightNewWebView
                )

                // I need to animate the chat back
                val composeChat = binding.streamComposeView

                val webParams = composeChat.layoutParams

                val screenWidthResource = Resources.getSystem().displayMetrics.widthPixels
                val windowManager =
                    context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val metrics = windowManager.currentWindowMetrics
                val screenWidthTesting = metrics.bounds.width()

//                Handler(Looper.getMainLooper()).postDelayed(
//                    {
//                    },
//                    1000 // value in milliseconds
//                )

                //animate Width
                ValueAnimator.ofInt(webParams.width, screenWidthResource).apply {
                    duration = 300 // Adjust duration for the animation
                    addUpdateListener { animator ->
                        webParams.width = animator.animatedValue as Int
                        composeChat.layoutParams = webParams
                    }
                    doOnEnd {


                        //animate X
                        ValueAnimator.ofInt(composeChat.x.toInt(), 0).apply {
                            duration = 300 // Adjust duration for the animation
                            addUpdateListener { animator ->
                                val value = animator.animatedValue as Int
                                composeChat.x = value.toFloat()
                            }
                            doOnEnd {
                                //animate Y
                                val streamingWebViewHeight = binding.webViewTestingMovement.height
                                Log.d(
                                    "TestingChatMovingHeight",
                                    "streamingView height -->${streamingWebViewHeight}"
                                )
                                ValueAnimator.ofInt(composeChat.y.toInt(), streamingWebViewHeight)
                                    .apply {
                                        duration = 300 // Adjust duration for the animation
                                        addUpdateListener { animator ->
                                            val value = animator.animatedValue as Int
                                            composeChat.y = value.toFloat()
                                        }
                                        start()
                                    }
                                // This method will be executed once the timer is over
                                //animate HEIGHT
                                val webParams2 = composeChat.layoutParams
                                composeChat.visibility = View.VISIBLE

                                val rootView = binding.root


                                //the height of the streamingView
                                val streamingWebViewHeight2 = binding.webViewTestingMovement.height
                                val bottomBarHeight = getNavigationBarHeight()

                                Log.d(
                                    "TestingChatMovingHeight",
                                    "608 -->${streamingWebViewHeight2}"
                                )
                                Log.d("TestingChatMovingHeight", "63 -->${bottomBarHeight}")
                                Log.d(
                                    "TestingChatMovingHeight",
                                    "671 -->${bottomBarHeight + streamingWebViewHeight2}"
                                )
                                Log.d(
                                    "TestingChatMovingHeight",
                                    "1611 -->${binding.streamingModalView.height - (streamingWebViewHeight2)}"
                                )

                                //   Log.d("TestingChatMovingHeight","container height -->${binding.streamingModalView.height-671}")
                                //  Log.d("TestingChatMovingHeight","bottomBarHeight -->${getNavigationBarHeight()}")// I need this
                                val finalHeight =
                                    binding.streamingModalView.height - streamingWebViewHeight2
                                ValueAnimator.ofInt(webParams2.height, finalHeight).apply {
                                    duration = 300 // Adjust duration for the animation
                                    addUpdateListener { animator ->
                                        webParams2.height = animator.animatedValue as Int
                                        composeChat.layoutParams = webParams2
                                    }
                                    start()
                                }

                            }
                            start()
                        }

                    }
                    start()
                }

                //make visible if invisable
            }


        }

        }

    }

    /**
     * animateHeightAndWidth animates the width and height of the [webView] object specified in a 16/9 ratio
     *
     * @param webView a [WebView] object that will be animated
     * @param newWidth a Int value used to animate the [webView]
     * */
    private fun animateHeightAndWidth(
        webView: WebView,
        newWidth:Int
    ){


        val params = webView.layoutParams
        val newHeight = newWidth * (9/16)

        //this is animating its width
        ValueAnimator.ofInt(params.width, newWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                params.width = animator.animatedValue as Int
                //  params.height = newHeight
                webView.layoutParams = params
            }

            start()
        }
        ValueAnimator.ofInt(params.height, newHeight).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                params.height = animator.animatedValue as Int
                //  params.height = newHeight
                webView.layoutParams = params
            }

            start()
        }

    }

    /**
     * animateXAndYToBottomRightScreen animates the X and Y values of the [newWebView] to the positions specified by
     * [screenHeight] and screenWidth
     *
     * @param screenWidth a Int value used to animate the X value of the [newWebView]
     * @param screenHeight a Int value used to animate the Y value of the [newWebView]
     * */
    fun animateXAndYToBottomRightScreen(
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
    /**
     * animateToTopLeftScreen is used to animate [webView] into a full screen position based on the [maxWidth]
     *
     * @param webView a [WebView] object that will be animated to the full screen position
     * @param maxWidth a Int value used to animate the [webView]
     * */
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
    @RequiresApi(Build.VERSION_CODES.R)
    fun animateToTopLeftCon(
        webView: WebView,
        maxWidth:Int
    ){
        val windowMetrics = requireActivity().getWindowManager().getCurrentWindowMetrics();
        val width = windowMetrics.getBounds().width()
        val height = windowMetrics.getBounds().height()

        val newHeight =(width*(9f/16f)).toInt().coerceIn(0,height)
        val newWidth = (newHeight*(16f/9f)).toInt()




        val endWidth = (maxWidth).toInt()
        Log.d("TESTINGMAXLENGHTTHINGER","endWidth -->$endWidth")
        ValueAnimator.ofInt(webView.y.toInt(), 0).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                webView.y = value.toFloat()
            }
            doOnStart {
                val webParams = webView.layoutParams
                webParams.width = width
                webParams.height = newHeight
                webView.layoutParams = webParams
            }
            doOnEnd {
                ValueAnimator.ofInt(webView.x.toInt(), 0).apply {
                    duration = 300 // Adjust duration for the animation
                    addUpdateListener { animator ->
                        val value = animator.animatedValue as Int
                        webView.x = value.toFloat()
                    }

                    start()
                }
            }
            start()
        }


    }
    /**
     * animateHorizontalOverlayToCenter animates the horizontal overlay into the correct horizontal position
     *
     * @param overlay a [View] object that will be animated
     * @param maxWidth a Int value that is used to animate the [overlay]
     * */
    fun animateHorizontalOverlayToCenter(
        overlay: View,
        maxWidth:Int
    ){
        val endWidth = (maxWidth*0.13).toInt()

        ValueAnimator.ofInt(overlay.x.toInt(), endWidth).apply {
            duration = 300 // Adjust duration for the animation
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                overlay.x = value.toFloat()
            }
            start()
        }
    }
    /**
     * animateHorizontalWidthAndHeightToFullScreen animates the full width and height  of [webView] in a 16/9 ration specified by the
     * [maxHeight]
     *
     * @param webView a [WebView] object that will be animated
     * @param maxHeight a Int value that is used to animate the [webView]
     * */
    fun animateHorizontalWidthAndHeightToFullScreen(
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
    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
    fun getNavigationBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
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

