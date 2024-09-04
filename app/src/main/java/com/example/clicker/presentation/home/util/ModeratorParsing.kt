package com.example.clicker.presentation.home.util

import com.example.clicker.network.models.twitchClient.GetModChannelsData
import com.example.clicker.network.models.twitchRepo.StreamData

/**
 * createOfflineAndOnlineLists() is used to create the list of online mod channels and offline mod channels
 *
 * @param modChannelList is a list of [GetModChannelsData] objects representing all the channels the user is a moderator for
 * @param liveFollowedStreamers is a list of [StreamData] objects representing all the live channels that the user follows
 *
 * @return [ModeratorOfflineOnlineLists]
 * */
 fun createOfflineAndOnlineLists(
    modChannelList: List<GetModChannelsData>,
    liveFollowedStreamers:List<StreamData>
):ModeratorOfflineOnlineLists{
    //todo: return both of these
    val offlineModList = mutableListOf<String>()
    val onlineList = mutableListOf<StreamData>()

    val listOfModName = modChannelList.map{it.broadcasterName}
    val listOfStreamerName = liveFollowedStreamers.map { it.userName }

    for (name in listOfModName){
        if(listOfStreamerName.contains(name)){
            val item = liveFollowedStreamers.first { it.userName == name }
            onlineList.add(item)
        }else{
            val offlineItem = modChannelList.first{it.broadcasterName ==name}
            offlineModList.add(offlineItem.broadcasterName)
        }
    }
    return ModeratorOfflineOnlineLists(
        offlineModList =offlineModList,
        onlineList=onlineList
    )
}

/**
 * - ModeratorOfflineOnlineLists is used to represent the offline and online moderation channels
 * @param offlineModList a list containing on the names of the offline mod channels
 * @param onlineList a list of [StreamData] objects that represent all of the online mod channels
 * */
data class ModeratorOfflineOnlineLists(
    val offlineModList: MutableList<String>,
    val onlineList: MutableList<StreamData>
)