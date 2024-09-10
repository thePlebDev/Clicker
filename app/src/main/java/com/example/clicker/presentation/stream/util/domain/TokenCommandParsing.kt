package com.example.clicker.presentation.stream.util.domain

import com.example.clicker.presentation.stream.util.TextCommands
import com.example.clicker.presentation.stream.util.Token


/**
 * TokenCommandParsing is the interface that acts as the API for all the methods needed to parse [Token] objects
 * that are created from the users commands 
 *
 * @property checkForSlashCommands the function that will parse the [TextCommands] objects
 * */
interface TokenCommandParsing {

    /**
     * checkForSlashCommands is used to return a single [TextCommands] object. Which is used to determine if any commands should
     * be sent from the users messaging prompts. ie, /ban,/unban or /warn
     *
     * @param tokenList a list of [Token] objects meant to represent the individual words the user typed out
     *
     * @return [TextCommands] object
     * */
    fun checkForSlashCommands(tokenList: List<Token>):TextCommands
}