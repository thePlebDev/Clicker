package com.example.clicker.presentation.minigames.views

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.R
import com.example.clicker.presentation.minigames.PixelContainer
import com.example.clicker.presentation.minigames.TextShadowTitle

import com.example.clicker.presentation.sharedViews.NoDrawerScaffold
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min
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
        bottomBar = {},
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
    // Store a reference to PingPongView
    val pingPongViewRef = remember { mutableStateOf<PingPongView?>(null) }
    val showStartBox = remember { mutableStateOf<Boolean>(true) }
    Box(){
        AndroidView(
            factory = {
                PingPongView(context).apply {
                    pingPongViewRef.value = this // Store reference
                }
            },
            modifier = modifier
        )
        // Run only once when showStartBox becomes true
        LaunchedEffect(showStartBox.value) {
            if (showStartBox.value) {
                pingPongViewRef.value?.restart()
            }
        }

        //todo: this is to be used on final release
        //THIS DEFINETLY NEEDS HAPTIC FEED BACK ON THE CLICK
        if(showStartBox.value){
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                PixelContainer(
                    true,true,
                    onClick = {
                        pingPongViewRef.value?.start()
                        showStartBox.value = false
                    },
                    cornerSize = 4,
                ){

                    TextShadowTitle("START GAME")

                }

            }
        }



    }


}


class PingPongView(context: Context?) : GLSurfaceView(context), View.OnTouchListener{
    private val renderer = Renderer()



    init{
        init()
        setOnTouchListener(this)
    }
    private fun init(){

        setEGLContextClientVersion(2)
        setRenderer(renderer)

    }
    fun start(){
        renderer.start()
    }
    fun restart(){
        renderer.restart()
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
                renderer.moveBottomPaddle(glX)


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
    fun moveBottomPaddle(xValue: Float){

        PingPongSystem.moveBottomPaddle(xValue)
    }
    fun start(){
        PingPongSystem.start()
    }
    fun restart(){
        PingPongSystem.restart()
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
    external fun moveBottomPaddle(xValue:Float)

    external fun start()
    external fun restart()


}
/**----------------------RETRO BUTTON STYLES BELOW----------------------*/

