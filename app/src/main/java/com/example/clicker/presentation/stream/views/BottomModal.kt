package com.example.clicker.presentation.stream.views

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.repository.models.EmoteListMap

import com.example.clicker.presentation.stream.BottomModalStateImmutable
import com.example.clicker.presentation.stream.models.ClickedUserBadgesImmutable
import com.example.clicker.presentation.stream.models.ClickedUsernameChatsWithDateSentImmutable
import com.example.clicker.presentation.stream.views.dialogs.ImprovedBanDialog
import com.example.clicker.presentation.stream.views.dialogs.ImprovedTimeoutDialog
import kotlinx.coroutines.delay


import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TestingNewBottomModal(
    clickedUsernameChatsDateSentImmutable: ClickedUsernameChatsWithDateSentImmutable,
    clickedUsername: String,
    textFieldValue: MutableState<TextFieldValue>,
    closeBottomModal: () -> Unit,
    banned: Boolean,
    isMod: Boolean,
    unbanUser: () -> Unit,
    openTimeoutDialog: () -> Unit,
    openBanDialog: () -> Unit,
//
    openWarnDialog:()->Unit,
    clickedUserBadgeList: ClickedUserBadgesImmutable, //badgeList
    badgeInlineContentMap: EmoteListMap,
    globalTwitchEmoteContentMap:EmoteListMap,
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap,
    sharedBetterTTVEmoteContentMap:EmoteListMap,
){
    Log.d("TestingNewBottomModalRecomp","RECOMP")


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        TestingNewContentBanner(
            clickedUsername = clickedUsername,
            textFieldValue = textFieldValue,
            hideModal = {closeBottomModal()},
            clickedUserBadgeList=clickedUserBadgeList, //badges
            badgeInlineContentMap=badgeInlineContentMap
        )
        TestingNewContentBottomPart(
            banned =banned,
            isMod =isMod,
            closeBottomModal ={closeBottomModal()},
            unbanUser ={unbanUser()},
            openTimeoutDialog={
                openTimeoutDialog()
            },
            openBanDialog ={openBanDialog()},

            openWarnDialog={openWarnDialog()},
        )
        TestingNewClickedUserMessages(
            clickedUsernameChatsWithDateSentImmutable =clickedUsernameChatsDateSentImmutable,
            globalTwitchEmoteContentMap=globalTwitchEmoteContentMap,
            channelTwitchEmoteContentMap=channelTwitchEmoteContentMap,
            globalBetterTTVEmoteContentMap=globalBetterTTVEmoteContentMap,
            channelBetterTTVEmoteContentMap=channelBetterTTVEmoteContentMap,
            sharedBetterTTVEmoteContentMap=sharedBetterTTVEmoteContentMap,
        )

    }


}



@Composable
fun WarnButton(
    openWarnDialog:()->Unit,
){
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
        onClick = {
          openWarnDialog()
        },
        modifier = Modifier.padding(end = 20.dp)
    ) {
        Text("Warn",color = MaterialTheme.colorScheme.onSecondary)
    }
}

@Composable
fun TimeoutButton(
    openTimeoutDialog:()->Unit,
){
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
        onClick = {
            openTimeoutDialog()
        },
        modifier = Modifier.padding(end = 20.dp)
    ) {
        Text(stringResource(R.string.timeout),color = MaterialTheme.colorScheme.onSecondary)
    }
}

@Composable
fun BanUnBanButtons(
    banned: Boolean,
    closeBottomModal: () -> Unit,
    unbanUser: () -> Unit,
    openBanDialog: () -> Unit
){
    if (banned) {
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
            onClick = {
                closeBottomModal()
                unbanUser()
            }) {
            Text(stringResource(R.string.unban),color = MaterialTheme.colorScheme.onSecondary)
        }
    } else {
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
            onClick = {
                openBanDialog()
            }) {
            Text(stringResource(R.string.ban),color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}
/*********NEW MODAL ITEMS THAT ARE NOT IN THE COMPANION OBJECT*************/
@Composable
fun TestingNewContentBanner(
    clickedUsername: String,
    textFieldValue: MutableState<TextFieldValue>,
    hideModal:()->Unit,
    clickedUserBadgeList: ClickedUserBadgesImmutable,
    badgeInlineContentMap: EmoteListMap

    ){

    var clickedBadgeName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.user_icon_description),
                    modifier = Modifier
                        .clickable { }
                        .size(35.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    clickedUsername,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }

            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                onClick = {

                    textFieldValue.value = TextFieldValue(
                        text = textFieldValue.value.text + "@$clickedUsername ",
                        selection = TextRange(textFieldValue.value.selection.start + "@$clickedUsername ".length)
                    )

                    hideModal()
                }) {
                Text(stringResource(R.string.reply), color = MaterialTheme.colorScheme.onSecondary)
            }
        }
        if(!clickedBadgeName.isEmpty()){
            Text(clickedBadgeName,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                modifier = Modifier.padding(horizontal =5.dp)
            )
        }

        Row(){
            for(item in clickedUserBadgeList.clickedBadges){
                val text = buildAnnotatedString {
                    if (badgeInlineContentMap.map.containsKey(item)) {
                        withStyle(style = SpanStyle(fontSize = 10.sp)) {
                            appendInlineContent(item, item)
                        }
                    }
                }
                androidx.compose.material3.Text(
                    text = text,
                    inlineContent = badgeInlineContentMap.map,
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            clickedBadgeName = text.text
                            scope.launch {
                                delay(2000)
                                clickedBadgeName = ""
                            }
                        }
                    ,
                    color = MaterialTheme.colorScheme.onPrimary,
                )

            }
        }



    }
}

@Composable
fun TestingNewContentBottomPart(
    banned: Boolean,
    isMod: Boolean,
    closeBottomModal: () -> Unit,
    unbanUser: () -> Unit,
    openTimeoutDialog:() -> Unit,
    openBanDialog:() -> Unit,
    openWarnDialog:()->Unit,
){
    Log.d("ContentBottomModerator","isMod --> $isMod")


    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isMod) {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                    WarnButton(
                        openWarnDialog={openWarnDialog()}
                    )
                    TimeoutButton(
                        openTimeoutDialog={openTimeoutDialog()}
                    )
                    BanUnBanButtons(
                        banned = banned,
                        closeBottomModal={
                            closeBottomModal()
                        },
                        openBanDialog={
                            openBanDialog()
                        },
                        unbanUser = {
                            unbanUser()
                        }
                    )
                }
            }
        }


    }/**End of the column**/

}

@Composable
fun TestingNewClickedUserMessages(

    clickedUsernameChatsWithDateSentImmutable: ClickedUsernameChatsWithDateSentImmutable,
    globalTwitchEmoteContentMap:EmoteListMap,
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap,
    sharedBetterTTVEmoteContentMap:EmoteListMap,
){
    val newMap = globalTwitchEmoteContentMap.map +channelTwitchEmoteContentMap.map + globalBetterTTVEmoteContentMap.map +channelBetterTTVEmoteContentMap.map +sharedBetterTTVEmoteContentMap.map

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        items(clickedUsernameChatsWithDateSentImmutable.clickedChats) {message->

            val annotatedString =buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontSize = MaterialTheme.typography.headlineSmall.fontSize)) {
                    append("${message.dateSent} ")
                }

                for(item in message.messageTokenList){
                    withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.headlineSmall.fontSize, color = MaterialTheme.colorScheme.onPrimary)) {
                        appendInlineContent("${item.messageValue}","${item.messageValue} ")
                    }
                }


            }

            Text(
                text =annotatedString,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                inlineContent = newMap
            )



        }
    }
}




