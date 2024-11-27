package com.example.clicker.presentation.home

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.BuildConfig
import com.example.clicker.R
import com.example.clicker.cameraNDK.CameraNDKNativeActivity
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.home.models.UserTypes
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
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




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragmentLifeCycle","onCreate")




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
        // Get the activity's intent

        if(value !=UserTypes.NEW){
            binding.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {

                    AppTheme{
                        ValidationView(
                            homeViewModel = homeViewModel,
                            streamViewModel = streamViewModel,
                            onNavigate = { dest -> findNavController().navigate(dest) },
                            autoModViewModel =autoModViewModel,
                            updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
                                modViewViewModel.updateAutoModTokens(
                                    oAuthToken =oAuthToken,
                                    clientId =clientId,
                                    broadcasterId=broadcasterId,
                                    moderatorId =moderatorId
                                )
                            },
                            createNewTwitchEventWebSocket ={modViewViewModel.createNewTwitchEventWebSocket()},
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
                                val myIntent = Intent(requireActivity(), CameraNDKNativeActivity::class.java)
                                startActivity(myIntent)
                            }


                        )
                    }

                }
            }
        }



        return view
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



}

