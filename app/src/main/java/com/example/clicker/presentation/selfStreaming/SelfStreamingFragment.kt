package com.example.clicker.presentation.selfStreaming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentSelfStreamingBinding
import com.example.clicker.presentation.selfStreaming.views.SelfStreamingView


class SelfStreamingFragment : Fragment() {

    private  var _binding: FragmentSelfStreamingBinding? = null
    /**
     * - The external version of [_binding]
     * */
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelfStreamingBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SelfStreamingView()
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

