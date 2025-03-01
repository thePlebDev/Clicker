package com.example.clicker.presentation.search

import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.databinding.FragmentSearchBinding
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.search.views.SearchView
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.ui.theme.AppTheme


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val autoModViewModel: AutoModViewModel by activityViewModels()
    private val modViewViewModel: ModViewViewModel by activityViewModels()
    private val chatSettingsViewModel: ChatSettingsViewModel by activityViewModels()
    private val streamInfoViewModel: StreamInfoViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            //hapticFeedBackError={view.performHapticFeedback(HapticFeedbackConstants.REJECT)},this is wha tis needed

            setContent {
                AppTheme{
//                    SearchView(
//                        onNavigate = { dest -> findNavController().navigate(dest) },
//                        homeViewModel=homeViewModel,
//                        searchViewModel=searchViewModel,
//                        hapticFeedBackError={ view?.performHapticFeedback(HapticFeedbackConstants.REJECT) },
//
//                        streamViewModel = streamViewModel,
//
//                        autoModViewModel =autoModViewModel,
//                        updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
//                            modViewViewModel.updateAutoModTokens(
//                                oAuthToken =oAuthToken,
//                                clientId =clientId,
//                                broadcasterId=broadcasterId,
//                                moderatorId =moderatorId
//                            )
//                        },
//                        createNewTwitchEventWebSocket ={modViewViewModel.createNewTwitchEventWebSocket()},
//                        chatSettingsViewModel=chatSettingsViewModel,
//                        streamInfoViewModel=streamInfoViewModel,
//                        modViewViewModel=modViewViewModel,
//
//                    )
                }
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
