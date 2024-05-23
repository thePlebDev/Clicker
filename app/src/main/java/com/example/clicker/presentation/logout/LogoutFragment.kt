package com.example.clicker.presentation.logout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.clicker.R
import com.example.clicker.databinding.FragmentLogoutBinding
import com.example.clicker.databinding.FragmentNewUserBinding


class LogoutFragment : Fragment() {

    private var _binding: FragmentLogoutBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
      val activityContext = activity?.applicationContext!!
        getActivity()?.window?.statusBarColor = ContextCompat.getColor(activityContext, R.color.red)
        getActivity()?.window?.navigationBarColor = ContextCompat.getColor(activityContext, R.color.black)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        // Make the fragment fullscreen


        binding.composeView.apply {

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TestingLogoutUI()
            }
        }


        return binding.root
    }

}

@Composable
fun TestingLogoutUI(){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Black
                    ),
                    startY = 0f,
                    endY = (screenHeight * 1.6).toFloat()

                )
            )
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier =Modifier.height(90.dp))
            Icon(
                tint = Color.White,
                painter = painterResource(id =R.drawable.ic_launcher_foreground),
                contentDescription = "Modderz logo",
                modifier = Modifier
                    .size(100.dp)
            )
        }

        Column(modifier= Modifier
            .align(Alignment.Center)
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text ="Modderz",color = Color.White, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text ="Because mobile moderators deserve love too",color = Color.White, fontSize = 30.sp)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Button(
                onClick ={},
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Login with Twitch", color = Color.White, fontSize = 18.sp)
            }
        }


    }
}