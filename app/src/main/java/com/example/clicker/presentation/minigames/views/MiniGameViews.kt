package com.example.clicker.presentation.minigames.views

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.R
import com.example.clicker.nativeLibraryClasses.NativeCube
import com.example.clicker.presentation.home.testing3DCode.TestingGLSurfaceViewComposable
import com.example.clicker.presentation.minigames.views.ContainerColors.Companion.GreyContainerColors
import com.example.clicker.presentation.minigames.views.ContainerColors.Companion.NintendoContainerColors
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

        //todo: this is to be used on final release
        //THIS DEFINETLY NEEDS HAPTIC FEED BACK ON THE CLICK
        if(showStartBox.value){
            Box(
                modifier = Modifier.align(Alignment.Center)
            ){
                PixelContainer(
                    true,true,
                    onClick = {
                        pingPongViewRef.value?.start()
                        showStartBox.value = false
                    },
                    cornerSize = 4,
                ){
                    TextShadow()

                }
            }
        }



    }


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
    fun start(){
        renderer.start()
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


}
/**----------------------RETRO BUTTON STYLES BELOW----------------------*/



@Composable
fun TextShadow() {
    val offset = Offset(5.0f, 10.0f)

    // Applying retro blocky text style with multiple shadows and custom styling
    Column {
        Text(
            text = "Start Game",
            style = TextStyle(
                fontSize = 32.sp,  // Larger font size for retro impact
                fontWeight = FontWeight.Bold,  // Bold font for blocky look
                color = Color.Cyan,  // Bright color for a retro feel
                letterSpacing = 3.sp,  // Increased letter spacing for a blocky, spaced out look
                shadow = Shadow(
                    color = Color.Red,  // Red shadow for a retro 3D effect
                    offset = Offset(4f, 4f),  // Shadow offset for depth
                    blurRadius = 6f  // Blur radius to create a more retro 3D effect
                ),
            )
        )
    }
}
@Composable
fun PixelContainer(
    enabled: Boolean = true,
    clickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    cornerSize: Int,
    pixelSize: Dp = 4.dp,
    colors: ContainerColors = NintendoContainerColors,
    content: @Composable () -> Unit,
) {
    Column {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        Box(
            modifier = Modifier
                .clickable(
                    enabled = clickable,
                    onClick = { onClick?.invoke() },
                    indication = null,
                    interactionSource = interactionSource
                )
                .drawBehind {
                    drawContainer(
                        colors = if(enabled)colors else GreyContainerColors,
                        pressedState = isPressed,
                        cornerSize = cornerSize,
                        pixelSize = pixelSize
                    )
                }
                .padding(
                    horizontal = (cornerSize.coerceAtLeast(1) + 1).toFloat() * pixelSize,
                    vertical = cornerSize.coerceAtLeast(1) * pixelSize
                )
        ) {
            content()
        }
    }
}
data class ContainerColors(
    val backgroundColor: Color,
    val pressedBackgroundColor: Color,
    val borderColor: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    companion object {
        val GreyContainerColors = ContainerColors(
            backgroundColor = Color(0xFF808080), // Grey
            pressedBackgroundColor = Color(0xFFD3D3D3), // Light Grey
            borderColor = Color(0xFF474747), // Dim Grey
            highlightColor = Color(0xFFA9A9A9), // Dark Grey
            shadowColor = Color(0xFF696969) // Charcoal Grey
        )
        val NintendoContainerColors = ContainerColors(
            backgroundColor = Color(0xFF8bac0f),
            pressedBackgroundColor = Color(0xFFc2ea1b),
            borderColor = Color(0xFF0f380f),
            highlightColor = Color(0xFF9bbc0f),
            shadowColor = Color(0xFF306230)
        )
    }
}


fun DrawScope.drawContainer(
    colors: ContainerColors,
    pixelSize: Dp = 4.dp,
    pressedState: Boolean = false,
    cornerSize: Int = 1,
) {
    val unit = pixelSize.toPx()
    //reduce cornerSize if it's greater than height or width
    val cornerSize =
        cornerSize.coerceAtMost((min(size.width, size.height) / unit / 2).toInt())

    drawBackground(
        unit,
        if (pressedState) colors.pressedBackgroundColor else colors.backgroundColor,
        cornerSize,
    )
    drawBorder(unit, cornerSize, colors.borderColor)
    drawHighlight(
        unit, cornerSize,
        if (pressedState) colors.shadowColor else colors.highlightColor
    )
    drawShadow(
        unit, cornerSize,
        if (pressedState) colors.highlightColor else colors.shadowColor
    )
}

fun DrawScope.drawBackground(
    unit: Float,
    borderColor: Color,
    cornerSize: Int
) {

    val horizontalBorderSize = this.size.width - (cornerSize + 1) * 2 * unit

    val path = Path().apply {
        moveTo((cornerSize + 1) * unit, 0f)
        lineTo(horizontalBorderSize, 0f)
        //top-right corner
        for (i in cornerSize downTo 0) {
            lineTo(
                this@drawBackground.size.width - (i * unit),
                (cornerSize - i) * unit
            )
            lineTo(
                this@drawBackground.size.width - (i * unit),
                (cornerSize - i + 1) * unit
            )
        }
        //right
        lineTo(size.width, size.height - (cornerSize * unit))
        //bottom-right corner
        for (i in 0..cornerSize) {
            lineTo(
                this@drawBackground.size.width - (i) * unit,
                this@drawBackground.size.height - ((cornerSize - i) * unit)
            )
            lineTo(
                this@drawBackground.size.width - (i + 1) * unit,
                this@drawBackground.size.height - ((cornerSize - i) * unit)
            )
        }
        lineTo((cornerSize + 1) * unit, size.height)
        //bottom-left corner
        for (i in cornerSize downTo 0) {
            lineTo(
                (i * unit),
                this@drawBackground.size.height - ((cornerSize - i) * unit)
            )
            lineTo(
                (i * unit),
                this@drawBackground.size.height - ((cornerSize - i + 1) * unit)
            )
        }
        //left
        lineTo(0f, size.height - (cornerSize * unit))
        //top-left corner
        for (i in 0..cornerSize) {
            lineTo(
                ((i) * unit),
                (cornerSize - i) * unit
            )
            lineTo(
                ((i + 1) * unit),
                (cornerSize - i) * unit
            )
        }
        close()
    }
    drawPath(path, borderColor)
}

private fun DrawScope.drawBorder(
    unit: Float,
    cornerSize: Int,
    borderColor: Color,
) {

    // Function to generate border segments for a given corner size
    fun getBorderSegments(cornerSize: Int): List<Segment> {
        val segments = mutableListOf<Segment>()

        val horizontalBorderSize = this.size.width - (cornerSize) * 2 * unit
        val verticalBorderSize = this.size.height - (cornerSize) * 2 * unit

        // Add top, bottom, left, and right border segments
        segments.add(
            Segment(
                Offset(cornerSize * unit, 0f),
                Size(horizontalBorderSize, unit)
            )
        )
        segments.add(
            Segment(
                Offset(cornerSize * unit, this.size.height - unit),
                Size(horizontalBorderSize, unit)
            )
        )
        segments.add(
            Segment(
                Offset(0f, cornerSize * unit),
                Size(unit, verticalBorderSize)
            )
        )
        segments.add(
            Segment(
                Offset(this.size.width - unit, cornerSize * unit),
                Size(unit, verticalBorderSize)
            )
        )
        // Add corner segments using loops
        for (i in 1..cornerSize) {
            for (j in 1..cornerSize) {
                if (i + j == cornerSize) { // Condition for corner segments
                    // Top-left corner
                    segments.add(
                        Segment(
                            Offset(i * unit, j * unit),
                            Size(unit, unit)
                        )
                    )
                    // Top-right corner
                    segments.add(
                        Segment(
                            Offset(
                                this.size.width - (i + 1) * unit,
                                j * unit
                            ), Size(unit, unit)
                        )
                    )
                    // Bottom-right corner
                    segments.add(
                        Segment(
                            Offset(
                                this.size.width - (i + 1) * unit,
                                this.size.height - (j + 1) * unit
                            ), Size(unit, unit)
                        )
                    )
                    // Bottom-left corner
                    segments.add(
                        Segment(
                            Offset(
                                i * unit,
                                this.size.height - (j + 1) * unit
                            ), Size(unit, unit)
                        )
                    )
                }
            }
        }


        return segments
    }

    // Draw the border
    drawSegments(getBorderSegments(cornerSize), borderColor)
}

private fun DrawScope.drawHighlight(
    unit: Float,
    cornerSize: Int,
    borderColor: Color,
) {

    // Function to generate border segments for a given corner size
    fun getBorderSegments(cornerSize: Int): List<Segment> {
        val segments = mutableListOf<Segment>()

        val horizontalBorderSize = this.size.width - (cornerSize + 1) * 2 * unit

        // Add top segment
        segments.add(
            Segment(
                Offset((cornerSize + 1) * unit, unit),
                Size(horizontalBorderSize, unit)
            )
        )
        // Add corner segments using loops
        for (i in 0..cornerSize) {
            for (j in 1..cornerSize) {
                if (i + j == cornerSize) { // Condition for corner segments
                    // Top-left corner
                    segments.add(
                        Segment(
                            Offset(i * unit + unit, j * unit),
                            Size(unit, unit)
                        )
                    )
                }
            }
        }
        return segments
    }

    drawSegments(getBorderSegments(cornerSize), borderColor)
}

@OptIn(ExperimentalStdlibApi::class)
private fun DrawScope.drawShadow(
    unit: Float,
    cornerSize: Int,
    borderColor: Color,
) {
    // Function to generate border segments for a given corner size
    fun getBorderSegments(cornerSize: Int): List<Segment> {
        val segments = mutableListOf<Segment>()

        val horizontalBorderSize = this.size.width - (cornerSize + 1) * 2 * unit

        // Add bottom segment
        segments.add(
            Segment(
                Offset((cornerSize + 1) * unit, this.size.height - 2 * unit),
                Size(horizontalBorderSize, unit)
            )
        )
        // Add corner segments using loops
        for (i in 0..cornerSize) {
            for (j in 0..cornerSize) {
                if (i + j == cornerSize) { // Condition for corner segments

                    // Bottom-right corner
                    segments.add(
                        Segment(
                            Offset(
                                this.size.width - (i + 2) * unit,
                                this.size.height - (j + 1) * unit
                            ), Size(unit, unit)
                        )
                    )
                }
            }
        }


        return segments
    }

    // Draw the border
    drawSegments(getBorderSegments(cornerSize), borderColor)
}

// Define a data class to represent a border segment
private data class Segment(val topLeft: Offset, val size: androidx.compose.ui.geometry.Size)

// Function to draw a list of border segments
private fun DrawScope.drawSegments(segments: List<Segment>, color: Color) {
    segments.forEach { segment ->
        drawRect(
            color = color,
            topLeft = segment.topLeft,
            size = segment.size
        )
    }
}