package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Stable
class ScaffoldBottomBarScope(
    private val iconSize: Dp,
){


    @Composable
    fun DualButtonNavigationBottomBar(
        bottomRowHeight: Dp,
        firstButton:@Composable ActionButtonsScope.() -> Unit,
        secondButton:@Composable ActionButtonsScope.() -> Unit,

    ){
        val firstButtonScope = remember(){ActionButtonsScope(iconSize)}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .height(bottomRowHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
            with(firstButtonScope){
                firstButton()
            }
            with(firstButtonScope){
                secondButton()
            }

        }
    }
}

@Stable
class TopScaffoldBarScope(
    private val iconSize: Dp
){
    @Composable
    fun IconTextTopBar(
        clickableIcon:@Composable ActionButtonsScope.() -> Unit,
        text: @Composable ()->Unit ={}
    ){
        val buttonScope = remember(){ActionButtonsScope(iconSize = iconSize)}
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                buttonScope.clickableIcon()
                text()
            }
        }

    }

}



@Stable
class ActionButtonsScope(
    private val iconSize: Dp,
){

    @Composable
    fun IconOverText(
        iconColor: Color,
        text:String,
        imageVector: ImageVector,
        iconContentDescription:String,
        onClick: () -> Unit
    ){
        Column(
            modifier = Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = iconContentDescription,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
            Text(text,color = MaterialTheme.colorScheme.onPrimary)
        }
    }

    @Composable
    fun PainterResourceIconOverText(
        iconColor: Color,
        text:String,
        painter: Painter,
        iconContentDescription:String,
        onClick: () -> Unit
    ){
        Column(
            modifier = Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter =painter,
                contentDescription = iconContentDescription,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )
            Text(text,color = MaterialTheme.colorScheme.onPrimary)
        }
    }

    @Composable
    fun ClickableIcon(
        iconColor: Color,
        imageVector: ImageVector,
        iconContentDescription:String,
        onClick: () -> Unit
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = iconContentDescription,
            tint = iconColor,
            modifier = Modifier.size(iconSize).clickable { onClick() }
        )
    }



}