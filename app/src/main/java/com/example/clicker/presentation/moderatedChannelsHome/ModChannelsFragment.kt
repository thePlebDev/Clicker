package com.example.clicker.presentation.moderatedChannelsHome

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.clicker.databinding.FragmentModChannelsBinding
import com.example.clicker.ui.theme.AppTheme
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.modView.ModViewViewModel
import com.example.clicker.presentation.search.SearchViewModel
import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ModChannelsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ModChannelsFragment : Fragment() {

    private var _binding: FragmentModChannelsBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val streamViewModel: StreamViewModel by activityViewModels()
    private val autoModViewModel: AutoModViewModel by activityViewModels()
    private val modViewViewModel: ModViewViewModel by activityViewModels()
    private val logoutViewModel: LogoutViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentModChannelsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme{
                    ModChannelView(
                        popBackStackNavigation = {  findNavController().popBackStack() },
                        homeViewModel = homeViewModel,
                        streamViewModel =streamViewModel,
                        onNavigate = { dest -> findNavController().navigate(dest) },
                        autoModViewModel = autoModViewModel,
                        updateModViewSettings = { oAuthToken,clientId,broadcasterId,moderatorId ->
                            modViewViewModel.updateAutoModTokens(
                                oAuthToken =oAuthToken,
                                clientId =clientId,
                                broadcasterId=broadcasterId,
                                moderatorId =moderatorId
                            )
                        },
                        createNewTwitchEventWebSocket ={modViewViewModel.createNewTwitchEventWebSocket()},
                        hapticFeedBackError={view.performHapticFeedback(HapticFeedbackConstants.REJECT)},
                        logoutViewModel =logoutViewModel,
                        modViewViewModel=modViewViewModel,
                        searchViewModel=searchViewModel
                    )
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
