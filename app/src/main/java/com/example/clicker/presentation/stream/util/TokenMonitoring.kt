package com.example.clicker.presentation.stream.util

import android.util.Log
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.models.MessageToken
import com.example.clicker.network.websockets.models.MessageType
import com.example.clicker.presentation.stream.util.domain.TokenParsing
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 *
 *
 * This class is used to determine which [TextCommands] was entered and execute the proper function through [runMonitorToken]
 *
 * @property runMonitorToken a function that depending on the type of [TextCommands] will inform the user of the action

 */
class TokenMonitoring @Inject constructor():TokenParsing{



     override fun runMonitorToken(
         tokenCommand: TextCommands,
         chatMessage:String,
         isMod: Boolean,
         currentUsername:String,
         sendToWebSocket:(String) ->Unit,
         addMessageToListChats:(TwitchUserData)->Unit,
         banUserSlashCommand:(String,String)->Unit,
         getUserId:((TwitchUserData)->Boolean)->String?,
         unbanUserSlash:(String)->Unit,
         messageTokenList: List<MessageToken>,
         warnUser:(String,String,String) ->Unit,

         ){
         Log.d("monitoringTokens", "username ->${tokenCommand.username}")

            when(tokenCommand){
                is TextCommands.UnrecognizedCommand ->{

                    val message = Messages.unrecognizedCommand
                        .addMessageTokens(messageTokenList)
                        .addUserType(chatMessage)
                        .build()

                    addMessageToListChats(message)
                }
                is TextCommands.Ban ->{


                    //  val userId = listChats.find { it.displayName == tokenCommand.username }?.userId
                    val userId =getUserId { it.displayName == tokenCommand.username }
                    if(isMod){
                        if(userId ==null){
                            val usernameMessageNotFound = Messages.usernameNotFound
                                .addMessageTokens(messageTokenList)
                                .addUserType("${tokenCommand.username} not found in this session")
                                .addSystemMessage("${tokenCommand.username} not found in this session")
                                .build()
                            addMessageToListChats(usernameMessageNotFound)
                        }
                        else{
                            Log.d("monitoringTokens", "userId -->$userId")
                            val message = Messages.normalMessage
                                .addUserType(chatMessage)
                                .addDisplayName(currentUsername)
                                .addMessageType(MessageType.USER)
                                .addMessageTokens(messageTokenList)
                                .build()
                            addMessageToListChats(message)
                            banUserSlashCommand(userId,tokenCommand.reason)
                        }
                    }else{
                        val message = Messages.youAreNotAModerator
                            .addMessageType(MessageType.ANNOUNCEMENT)
                            .addMessageTokens(messageTokenList)
                            .build()
                        addMessageToListChats(message)

                    }


                }
                is TextCommands.UnBan ->{
                    Log.d("monitoringTokens", "tokenCommand.username -->${tokenCommand.username}")
                    //  val userId = listChats.find { it.displayName == tokenCommand.username }?.userId
                    val userId =getUserId { it.displayName == tokenCommand.username }

                    if(isMod){
                        if(userId ==null){
                            val usernameMessageNotFound = Messages.usernameNotFound
                                .addMessageTokens(messageTokenList)
                                .addUserType("${tokenCommand.username} not found in this session")
                                .addSystemMessage("${tokenCommand.username} not found in this session")
                                .build()
                            addMessageToListChats(usernameMessageNotFound)
                        }else{
                            //todo: add the unban features
                            val message = Messages.normalMessage
                                .addUserType(chatMessage)
                                .addDisplayName(currentUsername)
                                .addMessageType(MessageType.USER)
                                .addMessageTokens(messageTokenList)
                                .build()
                            addMessageToListChats(message)
                            unbanUserSlash(userId)
                            Log.d("monitoringTokens", "userId -->$userId")
                        }
                    }else{
                        val message = Messages.youAreNotAModerator
                            .addMessageType(MessageType.ANNOUNCEMENT)
                            .addMessageTokens(messageTokenList)
                            .build()
                        addMessageToListChats(message)
                    }

                }
                is TextCommands.NoUsername ->{

                    val usernameMessageNotFound = Messages.usernameNotFound
                        .addMessageTokens(messageTokenList)
                        .addUserType("${tokenCommand.username} not found in this session")
                        .addSystemMessage("${tokenCommand.username} not found in this session")
                        .build()
                    addMessageToListChats(usernameMessageNotFound)
                }
                is TextCommands.NormalMessage ->{
                    sendToWebSocket(tokenCommand.username)
                    val message = Messages.normalMessage
                        .addUserType(chatMessage)
                        .addDisplayName(currentUsername)
                        .addMessageType(MessageType.USER)
                        .addMessageTokens(messageTokenList)
                        .build()
                    addMessageToListChats(message)
                }

                //todo: should have a normal command that just sends information to the websocket
                is TextCommands.InitialValue ->{
                    Log.d("monitoringTokens", "INITIALVALUE")

                }

                is TextCommands.Warn ->{
                    //todo: this is where I am going to make the warn commands

                    val userId =getUserId { it.displayName == tokenCommand.username }

                    if(isMod){
                        if(userId ==null){
                            val usernameMessageNotFound = Messages.usernameNotFound
                                .addMessageTokens(messageTokenList)
                                .addUserType("${tokenCommand.username} not found in this session")
                                .addSystemMessage("${tokenCommand.username} not found in this session")
                                .build()

                            addMessageToListChats(usernameMessageNotFound)
                        }else{

                            if(tokenCommand.reason.isEmpty()){
                                warnUser(userId,"Warning",tokenCommand.username)
                            }else{
                                warnUser(userId,tokenCommand.reason,tokenCommand.username)
                            }
                        }
                    }else{
                        val message = Messages.youAreNotAModerator
                            .addMessageType(MessageType.ANNOUNCEMENT)
                            .addMessageTokens(messageTokenList)
                            .build()
                        addMessageToListChats(message)
                    }


                }
            }


    }
}
/**
 * a class representing all the possible messages that a user will see once [runMonitorToken][TokenMonitoring.runMonitorToken]
 * is run
 * */
object Messages{
    val unrecognizedCommand = TwitchUserDataObjectMother
        .addColor("#BF40BF")
        .addSystemMessage("Unrecognized command ")
        .addDisplayName("Unrecognized command")
        .addMod("mod")
        .addMessageType(MessageType.ANNOUNCEMENT)

    val usernameNotFound =  TwitchUserDataObjectMother
        .addColor("#BF40BF")
        .addDisplayName("Unrecognized username")
        .addMod("mod")
        .addMessageType(MessageType.ANNOUNCEMENT)

    val youAreNotAModerator =TwitchUserDataObjectMother
        .addUserType("You are not a moderator in this chat")
        .addColor("#BF40BF")
        .addDisplayName("System message")
        .addMod("mod")
        .addSystemMessage("You are not a moderator in this chat. You do not have the proper permissions for this command")
        .addMessageType(MessageType.ANNOUNCEMENT)

    val normalMessage = TwitchUserDataObjectMother
        .addMod("mod")
        .addColor("#BF40BF")


}