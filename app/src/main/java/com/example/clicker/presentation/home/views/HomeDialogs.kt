package com.example.clicker.presentation.home.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.presentation.home.disableClickAndRipple
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Stable
import com.example.clicker.presentation.sharedViews.ButtonScope


@Stable
class HomeDialogs(){

    @Composable
    fun LogoutDialog(
        logoutDialogIsOpen:Boolean,
        closeDialog:()->Unit,
        logout:() ->Unit,
        currentUsername:String
    ){
        val fontSize =MaterialTheme.typography.headlineSmall.fontSize
        val buttonScope = remember(){ ButtonScope(fontSize) }

        if(logoutDialogIsOpen){
            Dialog(onDismissRequest = { closeDialog() }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    border = BorderStroke(2.dp,  MaterialTheme.colorScheme.secondary)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .background( MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Currently logged in as: $currentUsername",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Row(modifier=Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ){
                            with(buttonScope){
                                this.Button(
                                    text ="Cancel",
                                    onClick = { closeDialog()},
                                    )
                                Spacer(modifier =Modifier.width(15.dp))
                                this.Button(
                                    text ="Logout",
                                    onClick = { logout()},
                                )
                            }
                        }
                    }
                }
            }
        }

    }


}