package com.example.clicker.util.objectMothers

import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.MessageType

class IndividualAutoModSettingsDataObjectMother private constructor() {

    companion object {
        private var individualAutoModSettings= IndividualAutoModSettings(
        broadcasterId ="",
         moderatorId ="",
        overallLevel=null,
        sexualitySexOrGender=0,
        raceEthnicityOrReligion=0,
        sexBasedTerms=0,
        disability=0,
        aggression=0,
        misogyny=0,
        bullying=0,
        swearing=0
        )

        fun build():IndividualAutoModSettings{
            return individualAutoModSettings
        }
    }
}