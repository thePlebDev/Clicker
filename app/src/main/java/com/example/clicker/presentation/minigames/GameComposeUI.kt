package com.example.clicker.presentation.minigames

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.clicker.presentation.minigames.ContainerColors.Companion.GreyContainerColors
import com.example.clicker.presentation.minigames.ContainerColors.Companion.NintendoContainerColors
import kotlin.math.min


@Composable
fun GameStartingScreen(){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PixelContainer(
            true, true,
            onClick = {

            },
            cornerSize = 4,
        ) {

            TextShadowTitle("SELECT A GAME!!")

        }
        Spacer(
            modifier = Modifier
                .height(20.dp)
                .background(Color.Transparent)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PixelContainer2(
                true, true,
                onClick = {

                },
                cornerSize = 4,
            ) {

                TextShadow("Ping Pong")

            }
            Spacer(
                modifier = Modifier
                    .width(10.dp)
                    .background(Color.Transparent)
            )
            PixelContainer2(
                true, true,
                onClick = {

                },
                cornerSize = 4,
            ) {

                TextShadow("Dino Run")

            }

        }
    }

}


@Composable
fun TextShadow(
    text:String
) {
    val offset = Offset(5.0f, 10.0f)

    // Applying retro blocky text style with multiple shadows and custom styling
    Column {
        Text(


            text = text,
            style = TextStyle(
                fontSize = 20.sp,  // Larger font size for retro impact
                fontWeight = FontWeight.Bold,  // Bold font for blocky look
                color = Color.Cyan,  // Bright color for a retro feel
                letterSpacing = 3.sp,  // Increased letter spacing for a blocky, spaced out look
                shadow = Shadow(
                    color = Color.Red,  // Red shadow for a retro 3D effect
                    offset = Offset(4f, 4f),  // Shadow offset for depth
                    blurRadius = 6f  // Blur radius to create a more retro 3D effect
                ),
            ),
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun TextShadowTitle(
    text:String
) {
    val offset = Offset(5.0f, 10.0f)

    // Applying retro blocky text style with multiple shadows and custom styling
    Column {
        Text(


            text = text,
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
            ),
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun PixelContainer2(
    enabled: Boolean = true,
    clickable: Boolean = false,
    onClick: (() -> Unit)? = null,
    cornerSize: Int,
    pixelSize: Dp = 4.dp,
    colors: ContainerColors = NintendoContainerColors,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .size(100.dp, 100.dp)
            .aspectRatio(1f) // Makes the container a square
            .clickable(
                enabled = clickable,
                onClick = { onClick?.invoke() },
                indication = null,
                interactionSource = interactionSource
            )
            .drawBehind {
                drawContainer(
                    colors = if (enabled) colors else ContainerColors.GreyContainerColors,
                    pressedState = isPressed,
                    cornerSize = cornerSize,
                    pixelSize = pixelSize
                )
            }
            .padding(
                horizontal = (cornerSize.coerceAtLeast(1) + 1).toFloat() * pixelSize,
                vertical = (cornerSize.coerceAtLeast(1) + 1).toFloat() * pixelSize // Ensuring vertical padding matches horizontal
            )
    ) {
        content()
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
                        colors = if (enabled) colors else GreyContainerColors,
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