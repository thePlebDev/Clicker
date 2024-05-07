package com.example.clicker.presentation.stream.util

import android.util.Log
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class TokenMonitoring @Inject constructor(){


     fun runMonitorToken(
        tokenCommand: TextCommands,
        chatMessage:String,
        isMod: Boolean,
        currentUsername:String,
        sendToWebSocket:(String) ->Unit,
        addMessageToListChats:(TwitchUserData)->Unit,
        banUserSlashCommandTest:(String,String)->Unit,
        getUserId:((TwitchUserData)->Boolean)->String?,
        unbanUserSlashTest:(String)->Unit,
        addToMonitorUser:(String) ->Unit,
        removeFromMonitorUser:(String) ->Unit

    ){
         Log.d("monitoringTokens", "username ->${tokenCommand.username}")

            when(tokenCommand){
                is TextCommands.UNRECOGNIZEDCOMMAND ->{
                    Log.d("monitoringTokens", "UNRECOGNIZEDCOMMAND")
                    Log.d("monitoringTokens", "username -->${tokenCommand.username}")
                    Log.d("monitoringTokens", "reason -->${tokenCommand.reason}")
                    Log.d("monitoringTokens", "chatMessage -->${chatMessage}")
                    val message = TwitchUserDataObjectMother
                        .addUserType(chatMessage)
                        .addColor("#BF40BF")
                        .addSystemMessage("")
                        .addDisplayName("Unrecognized command")
                        .addMod("mod")
                        .addMessageType(MessageType.ANNOUNCEMENT)
                        .build()
                    addMessageToListChats(message)
                }
                is TextCommands.Ban ->{


                    //  val userId = listChats.find { it.displayName == tokenCommand.username }?.userId
                    val userId =getUserId { it.displayName == tokenCommand.username }
                    if(isMod){
                        if(userId ==null){
                            val message = TwitchUserDataObjectMother
                                .addUserType("${tokenCommand.username} not found in this session")
                                .addColor("#BF40BF")
                                .addDisplayName("Unrecognized username")
                                .addMod("mod")
                                .addSystemMessage("")
                                .addMessageType(MessageType.ANNOUNCEMENT)
                                .build()
                            addMessageToListChats(message)
                        }
                        else{
                            Log.d("monitoringTokens", "userId -->$userId")
                            val message = TwitchUserDataObjectMother
                                .addUserType(chatMessage)
                                .addColor("#BF40BF")
                                .addDisplayName(currentUsername)
                                .addMod("mod")
                                .addMessageType(MessageType.USER)
                                .build()
                            addMessageToListChats(message)
                            banUserSlashCommandTest(userId,tokenCommand.reason)
                        }
                    }else{
                        val message = TwitchUserDataObjectMother
                            .addUserType("You are not a moderator in this chat. You do not have the proper permissions for this command")
                            .addColor("#BF40BF")
                            .addDisplayName("System message")
                            .addMod("mod")
                            .addSystemMessage("")
                            .addMessageType(MessageType.ERROR)
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
                            val message = TwitchUserDataObjectMother
                                .addUserType("${tokenCommand.username} not found in this session")
                                .addColor("#BF40BF")
                                .addDisplayName("Unrecognized username")
                                .addSystemMessage("")
                                .addMod("mod")
                                .addMessageType(MessageType.ANNOUNCEMENT)
                                .build()
                            addMessageToListChats(message)
                        }else{
                            //todo: add the unban features
                            val message = TwitchUserDataObjectMother
                                .addUserType(chatMessage)
                                .addColor("#BF40BF")
                                .addDisplayName(currentUsername)
                                .addMod("mod")
                                .addMessageType(MessageType.USER)
                                .build()
                            addMessageToListChats(message)
                            unbanUserSlashTest(userId)
                            Log.d("monitoringTokens", "userId -->$userId")
                        }
                    }else{
                        val message = TwitchUserDataObjectMother
                            .addUserType("You are not a moderator in this chat")
                            .addColor("#BF40BF")
                            .addDisplayName("System message")
                            .addMod("mod")
                            .addSystemMessage("")
                            .addMessageType(MessageType.ANNOUNCEMENT)
                            .build()
                        addMessageToListChats(message)
                    }

                }
                is TextCommands.NOUSERNAME ->{
                    Log.d("monitoringTokens", "NOUSERNAME")
                    Log.d("monitoringTokens", "username -->${tokenCommand.username}")
                    val message = TwitchUserDataObjectMother
                        .addUserType(chatMessage)
                        .addColor("#BF40BF")
                        .addDisplayName("Username not found")
                        .addMod("mod")
                        .addSystemMessage("")
                        .addMessageType(MessageType.ANNOUNCEMENT)
                        .build()
                    addMessageToListChats(message)
                }
                is TextCommands.NORMALMESSAGE ->{
                    Log.d("monitoringTokens", "NORMALMESSAGE ---> ${tokenCommand.username}")
                    sendToWebSocket(tokenCommand.username)
                    Log.d("monitoringTokens", "username -->${tokenCommand.username}")
                    val message = TwitchUserDataObjectMother
                        .addUserType(chatMessage)
                        .addColor("#BF40BF")
                        .addDisplayName(currentUsername)
                        .addMod("mod")
                        .addMessageType(MessageType.USER)
                        .build()
                    addMessageToListChats(message)
                }
                is TextCommands.MONITOR ->{
                    Log.d("MONITOR","Monitor --> ${tokenCommand.username}")
                    addToMonitorUser(tokenCommand.username)

                    val message = TwitchUserDataObjectMother
                        .addUserType(chatMessage)
                        .addColor("#BF40BF")
                        .addDisplayName(currentUsername)
                        .addMod("mod")
                        .addMessageType(MessageType.USER)
                        .build()
                    addMessageToListChats(message)
                }
                is TextCommands.UnMONITOR ->{
                    Log.d("MONITOR","UN-MONITOR --> ${tokenCommand.username}")
                    removeFromMonitorUser(tokenCommand.username)

                    val message = TwitchUserDataObjectMother
                        .addUserType(chatMessage)
                        .addColor("#BF40BF")
                        .addDisplayName(currentUsername)
                        .addMod("mod")
                        .addMessageType(MessageType.USER)
                        .build()
                    addMessageToListChats(message)
                }

                //todo: should have a normal command that just sends information to the websocket
                is TextCommands.INITIALVALUE ->{
                    Log.d("monitoringTokens", "INITIALVALUE")

                }
            }


    }
}