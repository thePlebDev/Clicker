package com.example.clicker.network.repository.util

import android.util.Log
import com.example.clicker.R
import com.example.clicker.presentation.modView.ModActionData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ModActionParsing {

    fun parseActionFromString(stringToParse:String):String?{

        val messageTypeRegex = "\"action\":\"([^\"]*)\"".toRegex()
        return messageTypeRegex.find(stringToParse)?.groupValues?.get(1)

    }

    fun getModeratorUsername(stringToParse:String):String{
        val messageTypeRegex = "\"moderator_user_name\":\"([^\"]*)\"".toRegex()
        val parsedModeratorUserName = messageTypeRegex.find(stringToParse)?.groupValues?.get(1)?:""

        return parsedModeratorUserName
        // this also works but I understand it less --> (.*?)
    }

    fun getUserId(stringToParse: String):String?{
        val messageTypeRegex = "\"user_id\":\"([^\"]*)".toRegex()
        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1)
        return foundString

    }
    fun getUserName(stringToParse: String):String{
        val messageTypeRegex = "\"user_name\":\"([^\"]*)".toRegex()
        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1)
        return foundString?:"A user"

    }

    fun getReason(stringToParse: String):String{
        val messageTypeRegex = "\"reason\":\"([^\"]*)".toRegex()

        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1) ?:""
        return foundString
    }
    fun getExpiresAt(stringToParse: String):String{

        val messageTypeRegex = "\"expires_at\":\"([^\"]*)".toRegex()

        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1)
        if(foundString != null){
            return convertToReadableDate(foundString) ?:""
        }
        else return ""
    }
    fun getMessageBody(stringToParse: String):String?{
        //"reason":"stinky",
        val messageTypeRegex = "\"message_body\":\"([^\"]*)".toRegex()

        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1)
        return foundString
    }
    fun getBlockedTerms(stringToParse: String):String{

        val messageTypeRegex = "\"terms\":\\[\"([^\"\\]]*)".toRegex()
        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1) ?:""
        return foundString
    }
    fun getFollowerTime(stringToParse: String):String{
        val followersTime ="\"followers\":{\"follow_duration_minutes\":10},"
        val messageTypeRegex = "\"follow_duration_minutes\":(\\d+)".toRegex()

        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1) ?:""
        return foundString

    }
    fun getSlowModeTime(stringToParse: String):String{
        //wait_time_seconds

        val messageTypeRegex = "\"wait_time_seconds\":(\\d+)".toRegex()
        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1) ?:""
        return foundString
    }

    fun convertToReadableDate(timestamp: String): String {
        // Define the date format expected for the timestamp
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse the timestamp to a Date object
        val date: Date
        try {
            date = dateFormat.parse(timestamp)
        } catch (e: Exception) {
            return ""
        }

        // Get the current date and time
        val currentDate = Calendar.getInstance().time

        // Calculate the difference in seconds
        val bannedSeconds = (date.time - currentDate.time) / 1000

        return bannedSeconds.toString()
    }

    fun whenAction(
        action:String?,
        stringToParse: String,
        emitData:(data: ModActionData)->Unit,
    ){
        when(action){
            "untimeout" ->{
                //moderator name, user id, username
                Log.d("TimeoutActions","untimeout")
                val data = ModActionData(
                    title = getUserName(stringToParse),
                    message="Timeout removed by ${getModeratorUsername(stringToParse)}",
                    iconId = R.drawable.baseline_check_24
                )
                emitData(data)

            }

            "timeout" ->{
                Log.d("TimeoutActions","text ->$stringToParse")
                val data = ModActionData(
                    title = getUserName(stringToParse) ,
                    message="Timed out by ${getModeratorUsername(stringToParse)} for ${getExpiresAt(stringToParse)} seconds. ${getReason(stringToParse)}",
                    iconId = R.drawable.time_out_24
                )
                emitData(data)

            }
            "ban"->{
                println("BAN ACTION")
                val data = ModActionData(
                    title = getUserName(stringToParse),
                    message="Banned by ${getModeratorUsername(stringToParse)}. ${getReason(stringToParse)}",
                    iconId = R.drawable.clear_chat_alt_24
                )
                emitData(data)
            }
            "unban" ->{
                val data = ModActionData(
                    title = getUserName(stringToParse),
                    message="Unbanned  by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.baseline_check_24
                )
                emitData(data)

            }
            "delete"->{
                val data = ModActionData(
                    title = getUserName(stringToParse),
                    message="Message deleted by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.delete_outline_24,
                    secondaryMessage = getMessageBody(stringToParse)
                )
                emitData(data)

            }
            "subscribers"->{
                val data = ModActionData(
                    title = "Subscribers-Only Chat",
                    message="Enabled by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.person_outline_24,
                )
                emitData(data)

            }
            "subscribersoff"->{
                val data = ModActionData(
                    title = "Subscribers-Only Off",
                    message="Removed by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.person_outline_24,
                )
                emitData(data)

            }

            "remove_blocked_term"->{
                val data = ModActionData(
                    title = getBlockedTerms(stringToParse),
                    message="Removed as Blocked Term by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.lock_open_24,
                )
                emitData(data)
            }

            "add_blocked_term"->{
                val data = ModActionData(
                    title = getBlockedTerms(stringToParse),
                    message="Added as Blocked Term by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.lock_24,
                )
                emitData(data)

            }
            "emoteonly"->{
                val data = ModActionData(
                    title = "Emote-Only Chat",
                    message="Enabled by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.emote_face_24,
                )
                emitData(data)

            }
            "followers"->{
                val data = ModActionData(
                    title = "Follower-Only Chat",
                    message="Enabled with ${getFollowerTime(stringToParse)} min following age, by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.favorite_24,
                )
                emitData(data)
            }
            "slow" ->{
                val data = ModActionData(
                    title = "Slow Mode",
                    message="Enabled with ${getSlowModeTime(stringToParse)}s wait time, by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.baseline_hourglass_empty_24,
                )
                emitData(data)


            }
            "slowoff"->{
                val data = ModActionData(
                    title = "Slow Mode Off",
                    message="Removed by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.baseline_hourglass_empty_24,
                )
                emitData(data)

            }
            "followersoff"->{
                val data = ModActionData(
                    title = "Followers-Only Off",
                    message="Removed by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.favorite_24,
                )
                emitData(data)

            }
            "emoteonlyoff"->{
                val data = ModActionData(
                    title = "Emotes-Only Off",
                    message="Removed by ${getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.emote_face_24,
                )
                emitData(data)

            }
            else ->{

            }
        }

    }
}