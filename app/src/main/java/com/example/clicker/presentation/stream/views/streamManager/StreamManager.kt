package com.example.clicker.presentation.stream.views.streamManager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R


/**
 * StreamManagerUI shows the user 3 buttons to press and display the wanted information
 * */
@Composable
fun StreamManagerUI(
    showEditStreamInfo:()->Unit,

){
    Column(
        modifier= Modifier.verticalScroll(rememberScrollState())
    ) {

        EditStreamInfoCard(
            title="Mod View ",
            contentDescription = "Edit AutoMod Info",
            iconPainter = painterResource(R.drawable.mod_view_24),
            iconTextColor =Color.White,
            showStreamManager={
                //this conditional has been moved to now mean show modview
                showEditStreamInfo()
            }
        )
    }


}

@Composable
fun EditStreamInfoCard(
    title:String,
    contentDescription:String,
    iconPainter: Painter,
    iconTextColor:Color,
    showStreamManager:()->Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                showStreamManager()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Icon(
                painter = iconPainter,
                contentDescription,
                tint= iconTextColor,
                modifier = Modifier.size(35.dp)
            )
            Text(text =title, color = iconTextColor, fontSize = MaterialTheme.typography.headlineMedium.fontSize)

        }
    }
}