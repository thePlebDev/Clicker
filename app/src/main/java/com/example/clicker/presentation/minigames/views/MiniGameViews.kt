package com.example.clicker.presentation.minigames.views

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.R
import com.example.clicker.nativeLibraryClasses.NativeCube
import com.example.clicker.presentation.home.testing3DCode.TestingGLSurfaceViewComposable
import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.roundToInt


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
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
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
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    Log.d("TESTINGoNUPTHINGERS","onpress")
                          },
                onTap = {

                    Log.d("TESTINGoNUPTHINGERS","TAP")
                }
            )
        }
    )
}

class PingPongView(context: Context?) : GLSurfaceView(context), View.OnTouchListener{
    val renderer = Renderer()



    init{
        init()
        setOnTouchListener(this)
    }
    private fun init(){

        setEGLContextClientVersion(2)
        setRenderer(renderer)

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event ?: return false
        v?.parent?.requestDisallowInterceptTouchEvent(true)
        // Get screen dimensions
        val width = width.toFloat()
        val height = height.toFloat()

        // Convert screen coordinates to OpenGL coordinates
        val glX = (2.0f * event.x / width) - 1.0f
        val glY = 1.0f - (2.0f * event.y / height)



        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                Log.d("TESTINGACtionMoveTouch","glX->$glX glY->$glY")
                renderer.checkIfPaddleClicked(glX,glY)

                return true
            }


            MotionEvent.ACTION_MOVE -> {


                renderer.setXValue(glX)
                renderer.setYValue(glY)

                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("TESTINGACtionMove","UPP")
               // renderer.onTouch(event.x, event.y, isDragging = false)

                renderer.setClicked(false)
                return true
            }
        }
        return false
    }


}


class Renderer : GLSurfaceView.Renderer {

    private var xValue =0f
    private var yValue =0f



    fun setXValue(value: Float) {
        val newX = xValue + value

        // Clamp xValue between -1.0 and 1.0
        xValue = newX.coerceIn(-1.0f, 1.0f)

        Log.d("GLSurfaceViewRender", "xValue -> $xValue")
    }
    fun setYValue(value:Float){
        yValue =value
    }

    fun setClicked(value:Boolean){
        PingPongSystem.bottomPaddleClicked(value)
    }
    fun checkIfPaddleClicked(xValue:Float,yValue: Float){
        PingPongSystem.checkIfPaddleClicked(xValue, yValue)

    }

    override fun onDrawFrame(gl: GL10) {
        // The system calls this method on each redraw of the GLSurfaceView
        //this is called constantly
        //   NativeSquareLoading.step() // this is the checker board

        //  NativeBlurEffect.step() // this is the blur effect
        PingPongSystem.move(xValue,yValue)
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

    external fun move(xValue:Float,yValue:Float)

    external fun bottomPaddleClicked(clicked:Boolean)

    external fun checkIfPaddleClicked(xValue:Float,yValue:Float)


}