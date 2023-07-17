package com.example.clicker.network.models

import com.google.gson.annotations.SerializedName

data class StreamData(
    val id:String,
    @SerializedName("user_id")
    val userId:String,
    @SerializedName("user_login")
    val userLogin:String,
    @SerializedName("user_name")
    val userName:String,
    @SerializedName("game_id")
    val gameId:String,
    @SerializedName("game_name")
    val gameName:String,
    val type:String,
    val title:String,
    @SerializedName("viewer_count")
    val viewerCount:Int,
    @SerializedName("started_at")
    val startedAt:String,
    val language:String,
    @SerializedName("thumbnail_url")
    val thumbNailUrl:String,
    @SerializedName("tag_ids")
    val tagIds:List<String>,
    val tags:List<String>,
    @SerializedName("is_mature")
    val isMature:Boolean

)











