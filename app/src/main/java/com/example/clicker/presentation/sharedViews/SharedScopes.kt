package com.example.clicker.presentation.sharedViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.views.LiveChannelsLazyColumnScope
import com.example.clicker.presentation.modChannels.views.PullToRefresh
import com.example.clicker.presentation.modChannels.views.rememberPullToRefreshState


@Stable
class ScaffoldBottomBarScope(
    private val iconSize: Dp,
){


    @Composable
    fun DualButtonNavigationBottomBar(
        bottomRowHeight: Dp,
        firstButton:@Composable IconScope.() -> Unit,
        secondButton:@Composable IconScope.() -> Unit,

    ){
        val firstButtonScope = remember(){IconScope(iconSize)}
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
class ScaffoldTopBarScope(
    private val iconSize: Dp
){
    @Composable
    fun IconTextTopBar(
        clickableIcon:@Composable IconScope.() -> Unit,
        text: @Composable ()->Unit ={}
    ){
        val buttonScope = remember(){IconScope(iconSize = iconSize)}
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
class IconScope(
    private val iconSize: Dp,
){

    @Composable
    fun BasicIcon(
        color:Color,
        imageVector: ImageVector,
        contentDescription:String,
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(iconSize)
        )
    }

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
            modifier = Modifier
                .size(iconSize)
                .clickable { onClick() }
        )
    }



}

@Composable
fun UserMessage(){

}

@Stable
class NotificationsScope{

    @Composable
    fun NetworkStatus(
        modifier:Modifier,
        color:Color,
        networkMessage:String
    ){
        Card(
            modifier = modifier
                .clickable{ },
            elevation = 10.dp,
            backgroundColor =color.copy(alpha = 0.8f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    "home icon",
                    tint= MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
                Text(networkMessage,color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }

}
//stringResource(R.string.pull_to_refresh_icon_description)

@Composable
fun NewUserAlert(
    iconSize: Dp,
    iconContentDescription: String,
    iconColor: Color,
    iconImageVector: ImageVector,
    backgroundColor: Color,
    fontSize: TextUnit,
    textColor:Color,
    message: String,
    onClick: () -> Unit,
){
    val scope = remember(){NotifyUserScope(iconSize,fontSize)}
    with(scope){
        MatchingIconTextCard(
            textMessage =message,
            textColor= textColor,
            backgroundColor = backgroundColor,
            onClick = {onClick()}
        ) {
            BasicIcon(
                color =iconColor,
                imageVector = iconImageVector,
                contentDescription = iconContentDescription
            )
        }
    }
}
@Stable
class NotifyUserScope(
    //so if I don't want to pass anything directly to MatchingIconTextCard, I should define it here
//so put all the message, iconContentDescription, fontSize,iconSize and onClick here and then
// pass it down to the MatchingIconTextCard()
    private val iconSize:Dp,
    private val fontSize: TextUnit,


){
    @Composable
    fun MatchingIconTextCard(
       textMessage:String,
       textColor:Color,
       backgroundColor:Color,
       onClick: () -> Unit ={},
       icon:@Composable IconScope.()->Unit,

    ){
        val iconScope = remember(){IconScope(iconSize)}
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable {
                    onClick()
                },
            elevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                iconScope.icon()
                Text(textMessage, fontSize = fontSize,color=textColor)
                iconScope.icon()
            }
        }
    }
}

@Stable
class IndicatorScopes(){
    @Composable
    fun LazyListLoadingIndicator(){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }

}

@Composable
fun PullToRefreshComponent(
    padding: PaddingValues,
    refreshing:Boolean,
    refreshFunc:()->Unit,
    showNetworkMessage:Boolean,
    networkStatus:@Composable NotificationsScope.(modifier:Modifier) -> Unit,
    content:@Composable LiveChannelsLazyColumnScope.() -> Unit,
){

    val lazyColumnScope = remember() { LiveChannelsLazyColumnScope() }
    val networkStatusScope = remember() { NotificationsScope() }


    PullToRefresh(
        state = rememberPullToRefreshState(isRefreshing = refreshing),
        onRefresh = { refreshFunc()},
        indicatorPadding = padding
    ) {
        Box(modifier= Modifier
            .fillMaxSize()
            .padding(padding)){

            with(lazyColumnScope){
                content()
            }
            if(!showNetworkMessage){
                with(networkStatusScope){
                    networkStatus(Modifier.align(Alignment.BottomCenter))
                }
            }

        }

    }
}