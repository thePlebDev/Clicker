package com.example.clicker.presentation.minigames.views

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.R
import com.example.clicker.nativeLibraryClasses.NativeCube
import com.example.clicker.presentation.home.testing3DCode.TestingGLSurfaceViewComposable
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


@Composable
fun MiniGameViews(
    onNavigate: (Int) -> Unit,
){
    MiniGameScaffold(
        onNavigate={destination -> onNavigate(destination)}
    )
}

@Composable
fun MiniGameScaffold(
    onNavigate: (Int) -> Unit,
){
    val context = LocalContext.current
    NoDrawerScaffold(
        topBar = {
            TopBarTextRow("Mini games")
        },
        bottomBar = {
            this.FourButtonNavigationBottomBarRow(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                horizontalArrangement = Arrangement.SpaceAround,
                firstButton = {
                    IconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Home",
                        imageVector = Icons.Default.Home,
                        iconContentDescription = "Stay on home page",
                        onClick = {
                            //DONE
                            onNavigate(R.id.action_miniGameFragment_to_homeFragment)
                        },
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                secondButton = {
                    PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Mod Channels",
                        painter = painterResource(R.drawable.moderator_white),
                        iconContentDescription = "Navigate to mod channel page",
                        onClick = {
                            onNavigate(R.id.action_miniGameFragment_to_modChannelsFragment)
                                  },
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                thirdButton = {

                    this.PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        painter = painterResource(id = R.drawable.baseline_category_24),
                        iconContentDescription = "Navigate to search bar",
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Categories",
                        onClick = {

                            onNavigate(R.id.action_miniGameFragment_to_searchFragment)
                        },
                    )
                },

                fourthButton = {
                    this.PainterResourceIconOverTextColumn(
                        iconColor = MaterialTheme.colorScheme.secondary,
                        painter = painterResource(id = R.drawable.videogame_asset),
                        iconContentDescription = "Navigate to mini games page",
                        fontColor = MaterialTheme.colorScheme.onPrimary,
                        text = "Mini Games",
                        onClick = {

                        },
                    )

                }

            )

        },
        content = { contentPadding ->
            PingPongViewGLSurfaceViewComposable(
                context = context,
                modifier = Modifier.padding(contentPadding).fillMaxSize()
            )

        },
    )
}

//this is how we are going to show the openGLCode
@Composable
fun PingPongViewGLSurfaceViewComposable(
    context: Context,
    modifier: Modifier
) {
    AndroidView(
        factory = {
            PingPongView(context)
        },
        modifier = modifier
    )
}

class PingPongView(context: Context?) : GLSurfaceView(context){

    init{
        init()
    }
    private fun init(){

        setEGLContextClientVersion(2)
        setRenderer(Renderer())

    }





}

class Renderer : GLSurfaceView.Renderer {
    override fun onDrawFrame(gl: GL10) {
        // The system calls this method on each redraw of the GLSurfaceView
        //this is called constantly
        //   NativeSquareLoading.step() // this is the checker board

        //  NativeBlurEffect.step() // this is the blur effect
        PingPongSystem.step()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // The system calls this method when the GLSurfaceView geometry changes,
        // including changes in size of the GLSurfaceView or orientation of the device screen.
        //  NativeSquareLoading.init(width, height) // this is the checker board
        // NativeBlurEffect.init(width, height) // this is the blur effect
        PingPongSystem.init(width,height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // The system calls this method once, when creating the GLSurfaceView.
        // Do nothing.
//            gl?.glEnable(GLES20.GL_BLEND)
//            gl?.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

    }
}

object PingPongSystem{

    init{
        System.loadLibrary("ping_pong");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()


}