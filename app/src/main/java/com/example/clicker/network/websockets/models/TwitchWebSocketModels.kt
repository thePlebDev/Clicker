package com.example.clicker.network.websockets.models

import okhttp3.WebSocketListener

enum class MessageType {
    //added when a PRIVMSG command is sent
    USER,
    /**
     * Sent to indicate the outcome of an action like banning a user.
     * - more can be read [HERE](https://dev.twitch.tv/docs/irc/commands/#notice)
     * */
    NOTICE,

    USERNOTICE,

    /**
     * ANNOUNCEMENT,RESUB,SUB,MYSTERYGIFTSUB and GIFTSUB are all possible out comes when the Twitch IRC
     * server sends out a USERNOTICE command
     * - can read more about it, [HERE](https://dev.twitch.tv/docs/irc/commands/#usernotice)
     * */
    ANNOUNCEMENT,
    /**
     * ANNOUNCEMENT,RESUB,SUB,MYSTERYGIFTSUB and GIFTSUB are all possible out comes when the Twitch IRC
     * server sends out a USERNOTICE command
     * - can read more about it, [HERE](https://dev.twitch.tv/docs/irc/commands/#usernotice)
     * */
    RESUB,
    /**
     * ANNOUNCEMENT,RESUB,SUB,MYSTERYGIFTSUB and GIFTSUB are all possible out comes when the Twitch IRC
     * server sends out a USERNOTICE command
     * - can read more about it, [HERE](https://dev.twitch.tv/docs/irc/commands/#usernotice)
     * */
    SUB,
    /**
     * ANNOUNCEMENT,RESUB,SUB,MYSTERYGIFTSUB  are all possible out comes when the Twitch IRC
     * server sends out a USERNOTICE command
     * - can read more about it, [HERE](https://dev.twitch.tv/docs/irc/commands/#usernotice)
     * */
    MYSTERYGIFTSUB,
    /**
     * GIFTSUB used when the Twitch IRC
     * server sends out a USERNOTICE command stating that a gift sub has occurred
     * - can read more about it, [HERE](https://dev.twitch.tv/docs/irc/commands/#usernotice)
     * */
    GIFTSUB,


    /**
     * will be used when the [WebSocketListener] triggers its onFailure() method. A trigger of this method means that
     * the websocket has failed in some way and is disconnected
     * */
    ERROR,
    /**
     * Will be used when the Twitch irc server send a JOIN command. Telling the application that it has joined a chat room
     *
     * */
    JOIN,

    /**
     * Will be used to when a certain user was banned from the chat
     * */
    CLEARCHAT,

    /**
     * Will be used when the entire chat room is to be cleared by a moderator
     * */
    CLEARCHATALL,

    /**
     * Will be used when a user has typed a message that is their first message in chat
     * */
    FIRSTTIMECHATTER,

}