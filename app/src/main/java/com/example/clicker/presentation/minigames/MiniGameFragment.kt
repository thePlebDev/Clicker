package com.example.clicker.presentation.minigames

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.sp
import androidx.core.view.size
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.clicker.R
import com.example.clicker.databinding.FragmentMiniGameBinding
import com.example.clicker.databinding.FragmentModChannelsBinding
import com.example.clicker.presentation.minigames.views.MiniGameViews
import com.example.clicker.ui.theme.AppTheme


class MiniGameFragment : Fragment() {

    private var _binding: FragmentMiniGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMiniGameBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    MiniGameViews(
                        onNavigate = {dest -> findNavController().navigate(dest) }
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