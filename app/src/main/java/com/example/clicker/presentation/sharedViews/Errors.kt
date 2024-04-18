package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.clicker.R


@Stable
class ErrorScope(
    val fontSize: TextUnit
) {

    @Composable
    fun ChatErrorMessage(){

        val sideFade = Brush.horizontalGradient(
            listOf(
                Color.Red, Color.Red.copy(alpha = 0.8f), Color.Red.copy(alpha = 0.6f),
                Color.Red.copy(alpha = 0.4f), Color.Red.copy(alpha = 0.2f), Color.Red.copy(alpha = 0.0f)
            ),
            startX = 0.0f,
            endX = 130.0f
        )
        Box(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
        ){
            Spacer(modifier = Modifier.align(Alignment.CenterStart)
                .width(130.dp)
                .fadingEdge(sideFade)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Red)
                .height(80.dp)

            )
            Row(){
                Spacer(modifier = Modifier
                    .height(80.dp)

                )
                Spacer(modifier = Modifier
                    .width(17.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text("Error", color = MaterialTheme.colorScheme.onPrimary,fontSize=fontSize)
                        Spacer(modifier = Modifier
                            .width(6.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.error_outline_24),
                            contentDescription = "Chat error",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text("Disconnected from Chat. Please reconnect with chat", color = MaterialTheme.colorScheme.onPrimary,fontSize=fontSize)
                }
            }

        }


    }// end or chat error message

}


fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }