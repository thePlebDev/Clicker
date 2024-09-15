package com.example.clicker.presentation.stream

import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.R
import com.example.clicker.databinding.FragmentStreamBinding
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.horizontalStreamOverlay.OverlayStreamRow
import com.example.clicker.presentation.modChannels.modVersionThree.ModVersionThreeViewModel
import com.example.clicker.presentation.modChannels.modVersionThree.ModViewComponentVersionThree
import com.example.clicker.presentation.modView.ModViewDragStateViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.stream.customWebViews.VerticalWebView
import com.example.clicker.presentation.stream.customWebViews.HorizontalClickableWebView
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.horizontalLongPress.HorizontalLongPressView
import com.example.clicker.presentation.stream.views.overlays.HorizontalOverlayView
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.ui.theme.AppTheme


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 * [View.OnClickListener] is used for the back button on the stream
 */
class StreamFragment : Fragment() {

    private var _binding: FragmentStreamBinding? = null
    private val binding get() = _binding!!
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val autoModViewModel:AutoModViewModel by activityViewModels()
    private val modViewDragStateViewModel:ModViewDragStateViewModel by activityViewModels()
    private val modViewViewModel:ModViewViewModel by activityViewModels()
    private val modVersionThreeViewModel:ModVersionThreeViewModel by activityViewModels()
    private val chatSettingsViewModel: ChatSettingsViewModel by activityViewModels()
    private val streamInfoViewModel: StreamInfoViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        autoModViewModel.setHorizontalOverlayToVisible()
        autoModViewModel.setVerticalOverlayToVisible()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun animateHeight(
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

    /**
     * setHorizontalExpandedClick is a private function that is used to determine what will happen when the user is in horizontal
     * mode and they double click
     * */
    private fun setHorizontalExpandedClick(
        myWebView: WebView,
        horizontalClickableWebView: HorizontalClickableWebView,
        overlayComposeView: View,
        composeView: ComposeView,
        longPressComposeView: View,
        rootConstraintLayout: ConstraintLayout,
        viewToBeDragged:View
    ){
        horizontalClickableWebView.expandedMethod = {
            Log.d("lOGGGINTHEDOUBLECLICK", "called to make view expanded")
            horizontalClickableWebView.evaluateJavascript(
                "(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();",
                null
            );
            /*************** viewToBeDragged ***************************/
            val viewToBeDraggedParams = viewToBeDragged.layoutParams as ConstraintLayout.LayoutParams
            viewToBeDraggedParams.width =rootConstraintLayout.width
            viewToBeDraggedParams.endToEnd =myWebView.id
            viewToBeDraggedParams.startToStart =myWebView.id

            viewToBeDragged.layoutParams = viewToBeDraggedParams
            Log.d("TryingTOChangeWidth","viewToBeDraggedParams growing")

            /***********************************************************/
            setImmersiveMode(requireActivity().window)

            val overlayComposeParams =
                overlayComposeView.layoutParams as ConstraintLayout.LayoutParams
            overlayComposeParams.width = rootConstraintLayout.width


//      Create layout parameters to match parent
            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )

            //setImmersiveMode(requireActivity().window)


            //View.VISIBLE, View.INVISIBLE, View.GONE
            composeView.visibility = View.INVISIBLE
            longPressComposeView.visibility = View.INVISIBLE

            myWebView.layoutParams = layoutParams

            overlayComposeView.layoutParams = overlayComposeParams
        }
    }

    /**
     * setHorizontalLongPress is a private function that is used to determine what will happen when the user is in horizontal
     * mode and they do a long click
     * */
    private fun setHorizontalLongPress(
        horizontalClickableWebView: HorizontalClickableWebView,
        overlayComposeView: View,
        myWebView: WebView,
        composeView: ComposeView,
        longPressComposeView: View,
        rootConstraintLayout:ConstraintLayout,
        overlapView: View,
        viewToBeDragged:View

    ){
        horizontalClickableWebView.showLongClickView={
            Log.d("LongPressCheck","setHorizontalLongPressTesting()")
            unsetImmersiveMode(requireActivity().window)
            horizontalClickableWebView.evaluateJavascript("(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();", null);

            val sixtyWidth =(rootConstraintLayout.width * 0.6).toInt()
            //GET ALL THE PARAMS WE NEED TO CHANGE

            val overlayComposeParams = overlayComposeView.layoutParams as ConstraintLayout.LayoutParams
            overlayComposeParams.width =(rootConstraintLayout.width * 0.6).toInt()

            val webViewLayoutParams= myWebView.layoutParams as ConstraintLayout.LayoutParams
            /*************** viewToBeDragged ***************************/
            val viewToBeDraggedParams = viewToBeDragged.layoutParams as ConstraintLayout.LayoutParams
            viewToBeDraggedParams.width =(rootConstraintLayout.width * 0.6).toInt()
            viewToBeDraggedParams.endToEnd =myWebView.id
            viewToBeDraggedParams.startToStart =myWebView.id

            viewToBeDragged.layoutParams = viewToBeDraggedParams
            Log.d("TryingTOChangeWidth","viewToBeDraggedParams")

            /***********************************************************/

            //CHANGE THE WIDTH

            webViewLayoutParams.width = sixtyWidth

            //ADD EXTRA CONSTRAINT PARAMS
            webViewLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            overlayComposeParams.endToEnd =overlapView.id
            overlayComposeParams.startToStart =myWebView.id


            //SET IT TO INVISIBLE
            composeView.visibility = View.INVISIBLE


            //APPLY THE PARAMS
            myWebView.layoutParams = webViewLayoutParams
            overlayComposeView.layoutParams = overlayComposeParams

            longPressComposeView.visibility = View.VISIBLE

        }
        horizontalClickableWebView.hideLongClickView ={
            longPressComposeView.visibility = View.INVISIBLE
        }
    }

    //
    private fun horizontalOverlayHeightAnimation(
        horizontalClickableWebView: HorizontalClickableWebView,
        overlapView: View,
        rootConstraintLayout: ConstraintLayout,
        viewToBeDragged:View,
    ){
        val maxHeight = rootConstraintLayout.layoutParams.height
        var ceiling = 0f
        var floor = 0f
//        viewToBeDragged.y = 1321f
        Log.d("viewToBeDraggedTesting","height ->${(maxHeight).toFloat()} dragheight ->${viewToBeDragged.layoutParams.height}")




        viewToBeDragged.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Get the height of the view after it is drawn
                    val viewHeight = viewToBeDragged.height
                    val horizontalClickableWebViewHeight =horizontalClickableWebView.height


                    // Do something with the height
                    Log.d("viewToBeDraggedHeight", "horizontal Height --> $horizontalClickableWebViewHeight")
                    Log.d("viewToBeDraggedHeight", "Height: $viewHeight")
                    Log.d("viewToBeDraggedHeight", "rootConstraintLayout: ${rootConstraintLayout.height}")
                    Log.d("viewToBeDraggedHeight", "roof: ${rootConstraintLayout.height + viewHeight}")
                    Log.d("viewToBeDraggedHeight", "bottom: ${rootConstraintLayout.height - viewHeight}")
                    viewToBeDragged.y =(viewHeight +rootConstraintLayout.height).toFloat()

                    ceiling =(rootConstraintLayout.height - viewHeight).toFloat()
                    floor =(rootConstraintLayout.height + viewHeight).toFloat()
                    ViewCompat.setOnApplyWindowInsetsListener(viewToBeDragged) { view, insets ->
                        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                        horizontalClickableWebView.overlayDragCeiling = 471f
//                        horizontalClickableWebView.overlayDragFloor = 1415f


                        val statusBarTopHeight = systemBarsInsets.top
                        val statusBarBottomHeight = systemBarsInsets.bottom
                        val screenHeight = resources.displayMetrics.heightPixels
                        val heightWithoutStatusBar = screenHeight - statusBarTopHeight
                        Log.d("viewToBeDraggedHeight", "statusBar top --> $statusBarTopHeight")
                        Log.d("viewToBeDraggedHeight", "statusBar Bottom --> $statusBarBottomHeight")

                        // Use heightWithoutStatusBar as needed

                        insets // Return the insets as needed
                    }

                    // Remove the listener to prevent this from being called repeatedly
                    viewToBeDragged.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )




        horizontalClickableWebView.dragFunction = { value ->
            // Calculate the new Y position
            val newY = viewToBeDragged.y - value
            viewToBeDragged.y = newY.coerceIn(ceiling, floor) //the lower value is how far you can pull up

        }

        horizontalClickableWebView.singleTapMethod={
            val overlayHeight = overlapView.height
            val determinedHeight = (rootConstraintLayout.height * 0.9).toInt()

            //This is true
            if(overlayHeight == 1){
              //  viewToBeDragged.y = 200f
                animateHeight(
                    layoutParams =overlapView.layoutParams as ConstraintLayout.LayoutParams,
                    finalHeight = determinedHeight,
                    overlapView = overlapView
                )
                autoModViewModel.setHorizontalOverlayToVisible()

            }else{
               // viewToBeDragged.y = height.toFloat()
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

    }
    /**
     * collapseMethodLongPress is a private function that is used to determine what will happen when the user is in horizontal
     * mode and they do a long click. THis function will make the UI go from whatever state it is in, to the long press UI
     * */
    private fun setCollapseMethodLongPress(
        horizontalClickableWebView: HorizontalClickableWebView,
        overlayComposeView: View,
        myWebView: WebView,
        composeView: ComposeView,
        rootConstraintLayout: ConstraintLayout,

    ){
        horizontalClickableWebView.collapsedMethodLongPress = {
            unsetImmersiveMode(requireActivity().window)
            Log.d("collapsedMethodAgain","LONG COLLAPSE")

            val webViewWidth =(rootConstraintLayout.width * 0.6).toInt()
            val overlayComposeParams = overlayComposeView.layoutParams as ConstraintLayout.LayoutParams
            //todo: it definetly has to be the ImmersiveMode



            val webViewLayout = ConstraintLayout.LayoutParams(
                webViewWidth,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )


            composeView.visibility = View.VISIBLE
            myWebView.layoutParams = webViewLayout

            //  overlayComposeParams.startToStart =ConstraintLayout.LayoutParams.PARENT_ID
            overlayComposeParams.endToEnd =myWebView.id
            overlayComposeParams.startToStart =myWebView.id
            overlayComposeParams.width =webViewWidth


            overlayComposeView.layoutParams = overlayComposeParams

        }
    }

    /**
     * collapseMethodLongPress is a private function that is used to determine what will happen when the user is in horizontal
     * mode and they do a double click. THis function will make the UI go from whatever state it is in, to the double click UI
     * */
    private fun setCollapseMethodDoubleClick(
        horizontalClickableWebView: HorizontalClickableWebView,
        overlayComposeView: View,
        myWebView: WebView,
        composeView: ComposeView,
        rootConstraintLayout: ConstraintLayout,
        viewToBeDragged: View,
        ){
        horizontalClickableWebView.collapsedMethodDoubleClick = {

            unsetImmersiveMode(requireActivity().window)
            Log.d("collapsedMethodAgain","DOUBLE COLLAPSE")
            val webViewWidth =(rootConstraintLayout.width * 0.56).toInt()
            val overlayComposeParams = overlayComposeView.layoutParams as ConstraintLayout.LayoutParams


            val webViewLayout = ConstraintLayout.LayoutParams(
                webViewWidth,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )

            /*************** viewToBeDragged ***************************/
            val viewToBeDraggedParams = viewToBeDragged.layoutParams as ConstraintLayout.LayoutParams
            viewToBeDraggedParams.width =(rootConstraintLayout.width * 0.56).toInt()
            viewToBeDraggedParams.endToEnd =myWebView.id
            viewToBeDraggedParams.startToStart =myWebView.id

            viewToBeDragged.layoutParams = viewToBeDraggedParams
            Log.d("TryingTOChangeWidth","viewToBeDraggedParams")

            /***********************************************************/


            composeView.visibility = View.VISIBLE
            myWebView.layoutParams = webViewLayout

            //  overlayComposeParams.startToStart =ConstraintLayout.LayoutParams.PARENT_ID
            overlayComposeParams.endToEnd =myWebView.id
            overlayComposeParams.startToStart =myWebView.id
            overlayComposeParams.width =webViewWidth


            overlayComposeView.layoutParams = overlayComposeParams

        }

    }
    /**
     * setHorizontalUIClickAndLongPressMethods is a utility function and it's entire function is to call the functions,
     * [setHorizontalExpandedClick], [setHorizontalLongPress], [setHorizontalLongPress], [horizontalOverlayHeightAnimation],
     * [setCollapseMethodLongPress] and [setCollapseMethodDoubleClick]
     *
     * */
    private fun setHorizontalUIClickAndLongPressMethods(
        myWebView: WebView,
        horizontalClickableWebView: HorizontalClickableWebView,
        overlayComposeView: View,
        composeView: ComposeView,
        longPressComposeView: View,
        rootConstraintLayout: ConstraintLayout,
        overlapView: View,
        viewToBeDragged: View
    ){

        setHorizontalExpandedClick(
            myWebView =myWebView,
            horizontalClickableWebView =horizontalClickableWebView,
            overlayComposeView =overlayComposeView,
            composeView =composeView,
            longPressComposeView =longPressComposeView,
            rootConstraintLayout = rootConstraintLayout,
            viewToBeDragged=viewToBeDragged
        )


        //todo: changing showLongClickView() into its own method
        /*******************showLongClickView()********************************************************************/

        setHorizontalLongPress(
            horizontalClickableWebView =horizontalClickableWebView,
            overlayComposeView=overlayComposeView,
            composeView =composeView,
            myWebView =myWebView,
            longPressComposeView =longPressComposeView,
            rootConstraintLayout =rootConstraintLayout,
            overlapView =overlapView,
            viewToBeDragged=viewToBeDragged
        )



        /******************************singleTapMethod()*******************************************************/
        horizontalOverlayHeightAnimation(
            horizontalClickableWebView=horizontalClickableWebView,
            overlapView=overlapView,
            rootConstraintLayout=rootConstraintLayout,
            viewToBeDragged=viewToBeDragged
        )


        /**********************collapsed methods***************************************************************/

        setCollapseMethodLongPress(
            myWebView =myWebView,
            horizontalClickableWebView =horizontalClickableWebView,
            overlayComposeView =overlayComposeView,
            composeView =composeView,
            rootConstraintLayout = rootConstraintLayout
        )
        setCollapseMethodDoubleClick(
            myWebView =myWebView,
            horizontalClickableWebView =horizontalClickableWebView,
            overlayComposeView =overlayComposeView,
            composeView =composeView,
            rootConstraintLayout = rootConstraintLayout,
            viewToBeDragged=viewToBeDragged
        )

    }



    @RequiresApi(Build.VERSION_CODES.R)
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



        // val view = binding.root
        val orientationIsLandscape =resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
       // val window =activity?.window



        val view = setOrientation(
            resources = resources,
            binding = binding,
            streamViewModel = streamViewModel,
            autoModViewModel = autoModViewModel,
            homeViewModel = homeViewModel,
            modViewDragStateViewModel =modViewDragStateViewModel,
            modViewViewModel=modViewViewModel,
            orientationIsLandscape =orientationIsLandscape,
            hideSoftKeyboard={
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken,0)
            },
            showSoftKeyboard = {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.root, InputMethodManager.RESULT_SHOWN);
            },
            modVersionThreeViewModel =modVersionThreeViewModel,
            chatSettingsViewModel = chatSettingsViewModel,
            streamInfoViewModel=streamInfoViewModel
        )

        val myWebView: WebView = view.findViewById(R.id.webView) //this is the horizontal view
        val composeView:ComposeView = view.findViewById(R.id.compose_view)



        if (orientationIsLandscape) {
            val overlapView:View = view.findViewById(R.id.overlapView)
            val overlayComposeView:View = view.findViewById(R.id.overlapComposeView)
            val longPressComposeView:View = view.findViewById(R.id.compose_view_long_press)
            val rootConstraintLayout:ConstraintLayout = view.findViewById(R.id.rootLayout)
            val horizontalClickableWebView: HorizontalClickableWebView = myWebView as HorizontalClickableWebView
            val viewToBeDragged:View = view.findViewById(R.id.dragOverlapView)
            /*************** viewToBeDragged ***************************/
            val viewToBeDraggedParams = viewToBeDragged.layoutParams as ConstraintLayout.LayoutParams
            viewToBeDraggedParams.width =(rootConstraintLayout.width * 0.56).toInt()
            viewToBeDraggedParams.endToEnd =myWebView.id
            viewToBeDraggedParams.startToStart =myWebView.id

            viewToBeDragged.layoutParams = viewToBeDraggedParams
            Log.d("TryingTOChangeWidth","viewToBeDraggedParams")

            /***********************************************************/


            (horizontalClickableWebView as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)


            setHorizontalUIClickAndLongPressMethods(
                horizontalClickableWebView =horizontalClickableWebView,
                overlayComposeView=overlayComposeView,
                composeView =composeView,
                myWebView =myWebView,
                longPressComposeView =longPressComposeView,
                rootConstraintLayout =rootConstraintLayout,
                overlapView =overlapView,
                viewToBeDragged=viewToBeDragged
            )


            /******************END OF THE IF STATEMENT*****************************/
        }else{
            /**THIS conditional means that the phone is vertical */
            //I think this is the UI where the modView goes
           // setModViewUIOffScreen(view)
            verticalWebViewOverlayClicked(myWebView as VerticalWebView)
            setBackButtonOnClick(view)


        }
        //todo" THIS IS WHAT SETS THE WEB VIEW

        setWebView(
            myWebView = myWebView,
            url = url
        )


        return view
    }

    //todo: color for overlapView ->#B3000000
    /**
     * verticalWebViewOverlayClicked is a private function that is used to set up the functionality what happens when the stream is
     * in a vertical UI and and the clicks the webView
     * */
    private fun verticalWebViewOverlayClicked(
        verticalClickableWebView: VerticalWebView
    ){
        verticalClickableWebView.singleTapMethod={
            if(autoModViewModel.verticalOverlayIsVisible.value){
                autoModViewModel.setVerticalOverlayToHidden()
            }else{
                autoModViewModel.setVerticalOverlayToVisible()
            }
            verticalClickableWebView.evaluateJavascript("(function() { const button = document.querySelector('[data-a-target=\"content-classification-gate-overlay-start-watching-button\"]'); button && button.click(); })();", null);

        }
    }

    /**
     * setBackButtonOnClick is a private function used to set the clicking functionality of the back button UI
     * */
    private fun setBackButtonOnClick(view: FrameLayout){
        val backButton: ImageButton? = view.findViewById(R.id.backButton)
        backButton?.setOnClickListener {
            // Your code to be executed on button click
            findNavController().popBackStack()
        }
    }

    /**
     * setModViewUIOffScreen function is used to make sure the ModViewUI is off the screen
     * */
    private fun setModViewUIOffScreen(
        view: FrameLayout
    ){
        val streamManagerUI: View = view.findViewById(R.id.nested_draggable_compose_view)
        val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        streamManagerUI.translationY = height
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

@OptIn(ExperimentalComposeUiApi::class)
fun setOrientation(
    resources: Resources,
    binding: FragmentStreamBinding,
    streamViewModel: StreamViewModel,
    autoModViewModel: AutoModViewModel,
    chatSettingsViewModel:ChatSettingsViewModel,
    homeViewModel: HomeViewModel,
    modViewDragStateViewModel: ModViewDragStateViewModel,
    modViewViewModel: ModViewViewModel,
    orientationIsLandscape:Boolean,
    modVersionThreeViewModel:ModVersionThreeViewModel,
    hideSoftKeyboard:() ->Unit,
    showSoftKeyboard:()->Unit,
    streamInfoViewModel:StreamInfoViewModel
): FrameLayout {





    binding.composeView.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme{
                StreamView(
                    streamViewModel,
                    autoModViewModel,
                    modViewViewModel,
                    chatSettingsViewModel,
                    hideSoftKeyboard ={
                        hideSoftKeyboard()

                    },
                    showModView={
                        if(!orientationIsLandscape){
                            Log.d("ShowModViewFunction","clicked")
                            modViewDragStateViewModel.setShowModView(true)

//                            val editStreamInfoUI:View =binding.root.findViewById(R.id.nested_draggable_compose_view)
//                            /**THE ANIMATION*/
//
//                            val newTranslationY = 0 // Replace R.dimen.new_translation_y with your desired dimension resource
//
//                            //// Create ObjectAnimator for translationY property
//                            val animator = ObjectAnimator.ofFloat(editStreamInfoUI, "translationY", newTranslationY.toFloat())
//
//
//                            animator.duration = 300 // Adjust the duration as needed (in milliseconds)
////
//////                        // Start the animation
//                            animator.start()
                        }

                    },
                    modViewIsVisible = modViewDragStateViewModel.showModView.value,
                    streamInfoViewModel=streamInfoViewModel
                )

            }

        }
    }
    binding.dragOverlapComposeView?.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        val webView:WebView = binding.root.findViewById(R.id.webView)
        setContent {
            AppTheme {
                OverlayStreamRow(
                    homeViewModel=homeViewModel,
                    streamViewModel = streamViewModel,
                    loadURL ={newUrl ->setWebView(webView,newUrl)},
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
                    },
                    streamInfoViewModel=streamInfoViewModel
                )

            }
        }
    }

    binding.overlapComposeView?.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme{
                HorizontalOverlayView(
                    streamViewModel
                )
            }
        }
    }
    binding.nestedDraggableComposeView?.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Log.d("CheckingTheModViewShowingStuff","show -->${modViewDragStateViewModel.showModView.value}")
            if(modViewDragStateViewModel.showModView.value){
                AppTheme{

                    ModViewComponentVersionThree(
                        closeModView ={
                            modViewDragStateViewModel.setShowModView(false)
//                            val streamManagerUI: View = binding.root.findViewById(R.id.nested_draggable_compose_view)
//                            val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
//                            streamManagerUI.translationY = height
                        },
                        twitchUserChat=streamViewModel.listChats.toList(),
                        modViewViewModel=modViewViewModel,
                        streamViewModel = streamViewModel,
                        hideSoftKeyboard ={
                            hideSoftKeyboard()
                        },
                        modVersionThreeViewModel =modVersionThreeViewModel,
                        modViewDragStateViewModel=modViewDragStateViewModel,
                        chatSettingsViewModel=chatSettingsViewModel,
                        streamInfoViewModel=streamInfoViewModel
                    )


                }
            }


        }
    }
    binding.composeViewLongPress?.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        val webView:WebView = binding.root.findViewById(R.id.webView)

        setContent {
            AppTheme{
                HorizontalLongPressView(
                    homeViewModel,
                    streamViewModel = streamViewModel,
                    loadURL ={newUrl ->setWebView(webView,newUrl)},
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

    return binding.root
}


fun setWebView(
    myWebView: WebView,
    url: String
) {
    Log.d("setWebViewURL","url -->$url")
    myWebView.settings.mediaPlaybackRequiresUserGesture = false

    myWebView.settings.javaScriptEnabled = true
    myWebView.isClickable = true
    myWebView.settings.domStorageEnabled = true; // THIS ALLOWS THE US TO CLICK ON THE MATURE AUDIENCE BUTTON

    myWebView.settings.allowContentAccess = true
    myWebView.settings.allowFileAccess = true

    myWebView.settings.setSupportZoom(true)

    myWebView.loadUrl(url)
}
