package com.example.clicker.util.objectMothers

import com.example.clicker.network.websockets.models.LoggedInUserData

class LoggedInUserDataObjectMother private constructor() {

    companion object {
        private var loggedInUserData = LoggedInUserData(
            color = null,
            displayName = "#000000",
            sub = false,
            mod = false

        )
        fun addColor(color: String) = apply {
            loggedInUserData = loggedInUserData.copy(
                color = color
            )
        }
        fun addDisplayName(displayName: String) = apply {
            loggedInUserData = loggedInUserData.copy(
                displayName = displayName
            )
        }
        fun addSub(sub: Boolean) = apply {
            loggedInUserData = loggedInUserData.copy(
                sub = sub
            )
        }
        fun addMod(mod: Boolean) = apply {
            loggedInUserData = loggedInUserData.copy(
                mod = mod
            )
        }
        fun build(): LoggedInUserData {
            return loggedInUserData
        }
    }
}