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
import androidx.compose.material.icons.filled.Home
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

/**
 * ScaffoldBottomBarScope represents all the available components to a [Scaffold's](https://developer.android.com/jetpack/compose/components/scaffold)
 * bottomBar
 *
 * @property iconSize a mandatory  [Dp] unit parameter that is used to represent all of the Icon sizes inside of ScaffoldBottomBarScope
 * */
@Stable
class ScaffoldBottomBarScope(
    private val iconSize: Dp,
){


    /**
     * DualButtonNavigationBottomBarRow is a [Row] composable that is ***ONLY*** meant to be used inside of a Scaffold's bottomBar
     * - a UI demonstration can be found [HERE](https://github.com/thePlebDev/Clicker/wiki/Shared-Scopes#DualButtonNavigationBottomBarRow)
     *
     * @param fontSize a [TextUnit] used to determine the shared font size of [firstButton] and [secondButton]
     * @param horizontalArrangement a [Arrangement.Horizontal] object used to determine the layout of [firstButton] and [secondButton]
     * in this row layout
     *
     * @param firstButton a [IconScope] composable that will act as the fist button shown in this row layout
     * @param secondButton a [IconScope] composable that will act as the second button shown in this row layout
     * */
    @Composable
    fun DualButtonNavigationBottomBarRow(
        fontSize: TextUnit,
        horizontalArrangement: Arrangement.Horizontal,
        firstButton:@Composable IconScope.() -> Unit,
        secondButton:@Composable IconScope.() -> Unit,

    ){
        val firstButtonScope = remember(){IconScope(iconSize,fontSize)}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement
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

/**
 * ScaffoldTopBarScope represents all the available components to a [Scaffold's](https://developer.android.com/jetpack/compose/components/scaffold)
 * topBar
 *
 * @property iconSize a mandatory  [Dp] unit parameter that is used to represent all of the Icon sizes inside of ScaffoldBottomBarScope
 * */
@Stable
class ScaffoldTopBarScope(
    private val iconSize: Dp
){

    /**
     * IconTextTopBar is a [Row] composable that is ***ONLY*** meant to be used inside of a Scaffold's TopBar.
     *
     * @param clickableIcon a [IconScope] composable meant to be displayed on top of [text]
     * @param text a basic composable shown to the user
     * */
    @Composable
    fun IconTextTopBar(
        clickableIcon:@Composable IconScope.() -> Unit,
        text: @Composable ()->Unit ={}
    ){
        val buttonScope = remember(){IconScope(iconSize = iconSize)}

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                buttonScope.clickableIcon()
                text()
            }

    }
    /**
     * TopBarText is a [Row] composable that is ***ONLY*** meant to be used inside of a Scaffold's TopBar.
     *
     * @param text a [String] meant to display a message to the user
     * */
    @Composable
    fun TopBarText(
        text:String,

    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text =text,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }

    }

}



/**
 * IconScope is a scope used to strongly type composable parameters that require icons
 *
 * @param iconSize a [Dp] used to determine the shared size of all the icons within this scope
 * @param fontSize a [TextUnit] used to determine the shared size of all the Text's font size within this scope
 * */
@Stable
class IconScope(
    private val iconSize: Dp,
    private val fontSize: TextUnit = 20.sp, //this is equal to the medium font size of MaterialTheme.typography.headlineMedium
){


    /**
     * BasicIcon is a [Icon] composable. Used to represent the most standard and basic icon
     * - The size of the icon is determined by [iconSize]
     *
     * @param color a [Color] used to determine the color of the icon
     * @param imageVector a [ImageVector] which represents the image of the icon shown to the user
     * @param contentDescription a [String] used to determine the content description of the icon
     * @param onClick a optional function that will be called when the user clicks on the icon
     * */
    @Composable
    fun BasicIcon(
        color:Color,
        imageVector: ImageVector,
        contentDescription:String,
        onClick: () -> Unit ={}
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(iconSize).clickable { onClick() }
        )
    }


    /**
     * BasicIcon is a [Column] composable. Used to represent a [Icon] and a [Text] displayed in a column format
     * - A UI demonstration can be found [HERE]()
     *
     * @param iconColor a [Color] used to determine the color of the icon
     * @param imageVector a [ImageVector] which represents the image of the icon shown to the user
     * @param iconContentDescription a [String] used to determine the content description of the icon
     * @param text a [String] used to represent the message that will be displayed in the text below the icon
     * @param fontColor a [Color] used to represent the color of the text
     * @param onClick a function which will be called when the column surrounding the Icon and Text is clicked
     * */
    @Composable
    fun IconOverTextColumn(
        iconColor: Color,
        imageVector: ImageVector,
        iconContentDescription:String,
        text:String,
        fontColor:Color,
        onClick: () -> Unit,
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
            Text(
                text,
                color = fontColor,
                fontSize = fontSize
            )
        }
    }


    /**
     * PainterResourceIconOverTextColumn is a [Column] composable. Used to represent a [Icon] and a [Text] displayed in a column format
     * - A UI demonstration can be found [HERE]()
     *
     * @param iconColor a [Color] used to determine the color of the icon
     * @param painter a [Painter] which represents the image of the icon shown to the user
     * @param iconContentDescription a [String] used to determine the content description of the icon
     * @param text a [String] used to represent the message that will be displayed in the text below the icon
     * @param fontColor a [Color] used to represent the color of the text
     * @param onClick a function which will be called when the column surrounding the Icon and Text is clicked
     * */
    @Composable
    fun PainterResourceIconOverTextColumn(
        iconColor: Color,
        painter: Painter,
        iconContentDescription:String,
        fontColor:Color,
        text:String,
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
            Text(
                text,
                fontSize=fontSize,
                color = fontColor,
            )
        }
    }




}


/**
 * NotificationsScope is a scope used to strongly type composable parameters that requires notification
 *
 * */
@Stable
class NotificationsScope(
    private val iconSize: Dp,
    private val fontSize: TextUnit

){

    /**
     * NetworkStatus is a [Card] composable. Used to alert the user of a status change
     * - A UI demonstration can be found [HERE]()
     *
     * @param color a [Color] used to determine the background color of the card
     * @param modifier a [Modifier] used to position the composable
     * @param networkMessage a [String] used to determine the message shown to the user
     * */
    @Composable
    fun NetworkStatusCard(
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
                    modifier = Modifier.size(iconSize)
                )
                Text(
                    networkMessage,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize =fontSize
                )
            }
        }
    }

    /**
     * MatchingIconTextCard is a [Card] composable. Used to alert the user of a message
     * - A UI demonstration can be found [HERE]()
     *
     * @param textMessage a [String] used to determine the message sent to the user
     * @param textColor a [Color] used to determine the color of the text shown to the user
     * @param backgroundColor a [Color] used to determine the entire background color of the card
     * @param onClick a function called when the user clicks the card
     * @param icon a [IconScope] composable that be the icons shown to the user
     * */
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


/**
 * IndicatorScopes is a scope used to strongly type composable parameters that requires Loading indicators
 *
 * */
@Stable
class IndicatorScopes(){
    /**
     * LazyListLoadingIndicator is a [Row] composable meant to show a [CircularProgressIndicator] to the user
     *
     * */
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


/**
 * TODO: NONE OF THE COMPOSABLES BELOW ARE IN THE PROPER DOCUMENTATION FORMAT.NEED TO BE REWORKED AFTER TESTING
 * */

@Composable
fun PullToRefreshComponent(
    padding: PaddingValues,
    refreshing:Boolean,
    refreshFunc:()->Unit,
    showNetworkMessage:Boolean,
    networkStatus:@Composable NotificationsScope.(modifier:Modifier) -> Unit,
    content:@Composable LiveChannelsLazyColumnScope.() -> Unit,
){

    val mediumFontSize = MaterialTheme.typography.headlineMedium.fontSize
    val lazyColumnScope = remember() { LiveChannelsLazyColumnScope() }
    val networkStatusScope = remember() { NotificationsScope(fontSize = mediumFontSize,iconSize=30.dp) }


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
    val scope = remember(){NotificationsScope(iconSize,fontSize)}
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