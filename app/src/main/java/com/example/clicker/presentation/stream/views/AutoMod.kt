package com.example.clicker.presentation.stream.views

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R


/**
 * Used inside the [Constants][AutoMod.Constants], it is meant to help define titles and there subtitles. This makes looping
 * and displaying text easier.
 * */
data class TitleSubTitle(
    val title: String, val subTitle: String
)
//1)Rule: brief description followed by the number of implementations
/**
 * AutoMod represents all the UI composables used to build the auto moderation feature in the app
 *
 * - AutoMod contains 1 public top level implementation:
 * 1) [Settings]
 *
 * */
object AutoMod {


    //Rule: brief description followed by builders used, then parameter description
    /**
     * Settings is the implementation that contains all the individual elements that makes
     * our user auto moderation experience
     *
     * - AutoScrollChatWithTextBox implements the [AutoModRows][Builders.AutoModRows] Builder
     * */
    @Composable
    fun Settings() {

        var sliderPosition by remember { mutableFloatStateOf(0f) }
        Builders.AutoModRows(
            slider ={
                Parts.AutoModSlider(
                sliderPosition,
                changeSliderValue = {currentValue ->sliderPosition = currentValue}
            )
                    },
            hostilityRow = {
                Parts.AutoModRow(
                    title = "HOSTILITY",
                    titleList = Constants.hostilityList
                )
            },
            discriminationRow = {
                Parts.AutoModRow(
                    title ="DISCRIMINATION AND SLURS",
                    titleList = Constants.discriminationList
                )
            },
            sexualRow = {
                Parts.AutoModRow(
                    title = "SEXUAL CONTENT",
                    titleList = Constants.sexualList
                )
            },
            profanityRow={
                Parts.AutoModRow(
                    title = "PROFANITY",
                    titleList = Constants.profanityList
                )
            }
        )

    }

    /**
     * Builders represents the most generic parts of [AutoMod] and should be thought of as UI layout guides used
     * by the implementations above. There is currently one builder:
     * 1) [AutoModRows]
     * */
    private object Builders{
        //Rule: brief description followed by the UI link and then the params
        /**
         * AutoModRows  is the basic layout for the user Auto Moderation experience. A example of what the typical UI looks like
         * with this builder can be found [HERE]()
         *
         * @param slider a [Slider] that will be shown to the user and represent the current state of [AutoMod]
         * @param hostilityRow a [Row] representing all the [hostilityList][Constants.hostilityList] information
         * @param discriminationRow a [Row] representing all the [discriminationList][Constants.discriminationList] information
         * @param sexualRow a [Row] representing all the [sexualList][Constants.sexualList] information
         * @param profanityRow a [Row] representing all the [profanityList][Constants.profanityList] information
         * */
        @Composable
        fun AutoModRows(
            slider:@Composable () -> Unit,
            hostilityRow:@Composable () -> Unit,
            discriminationRow:@Composable () -> Unit,
            sexualRow:@Composable () -> Unit,
            profanityRow:@Composable () -> Unit,
        ){
            LazyColumn(modifier = Modifier.padding(5.dp)) {
                item{
                    slider()
                }
                item{
                    hostilityRow()
                }
                item{
                    discriminationRow()
                }
                item{
                    sexualRow()
                }
                item{
                    profanityRow()
                }

            }
        }
    }

    /**
     * Parts represents the most individual parts of [AutoMod] and should be thought of as the individual
     * pieces that are used inside of a [Builders] to create the top level implementations that are used in the code base
     * */
    private object Parts{
        //Rule) Brief description, followed by how many parts are used and any params


        /**
         * A composable meant to act as the layout for [ClickableRow] and [ConditionalRows]. Also, to internally hold the
         * state for both of them
         *
         * - Contains 2 extra parts: [ClickableRow] and [ConditionalRows]
         *
         * @param title a String to show the user which Row this represents
         * @param titleList a list of [TitleSubTitle] objects that will be conditionally shown to the user
         * */
        @Composable
        fun AutoModRow(
            title:String,
            titleList: List<TitleSubTitle>
        ){
            var expandedState by remember {
                mutableStateOf(false)
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)) {

                ClickableRow(
                    changeExpandedState = {
                        expandedState = !expandedState
                    },
                    title =title
                )

                ConditionalRows(
                    expandedState, titleList
                )

            }
        }

        /**
         * A composable meant to display a [DropdownMenu] and show the user a list of the [filteringLevels][Constants.filteringLevels]
         *
         * - Contains 1 extra part: [DropDownRow]
         * */
        @Composable
        fun DropdownRowMenu() {
            var expanded by remember { mutableStateOf(false) }
            val filterLevels = Constants.filteringLevels
            var selectedIndex by remember { mutableStateOf(0) }
            Box(modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .padding(horizontal = 10.dp)) {
                DropDownRow(
                    selectedText = filterLevels[selectedIndex],
                    expandMenu = {expanded = true}
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Red
                        )
                ) {
                    filterLevels.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            selectedIndex = index
                            expanded = false
                        },
                            text = {
                                Text(text = s)
                            }
                        )
                    }
                }
            }

        }
        /**
         * A composable meant to conditionally display information from [titleList]. Shown based on the current state of [expandedState]
         *
         * - Contains 1 extra part: [DropdownRowMenu]
         *
         * @param expandedState a boolean that is used to determine if infromation from [titleList] should be shown to the user
         * @param titleList a list of [TitleSubTitle] objects that will be conditionally shown to the user
         * */
        @Composable
        fun ConditionalRows(
            expandedState:Boolean,
            titleList: List<TitleSubTitle>
        ){
            if(expandedState){
                for(item in titleList){
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)){
                        Column(){
                            Text(item.title, fontSize = 20.sp, fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onPrimary)
                            Text(item.subTitle, fontSize = 18.sp,color = MaterialTheme.colorScheme.onPrimary ,modifier = Modifier.padding(horizontal =10.dp))
                            DropdownRowMenu()
                        }
                    }
                }
            }
        }

        /**
         * A composable meant to show and give the user access to a [Slider] and show information based on the value of the slider
         *
         * @param sliderPosition a float value that represents the current value of the slider
         * @param changeSliderValue a function used to change the value of the old [sliderPosition] to the current value
         * */
        @Composable
        fun AutoModSlider(
            sliderPosition:Float,
            changeSliderValue:(Float) ->Unit
        ){
            Column(modifier =Modifier.fillMaxWidth()){
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    Text("Your AutoMod Settings", fontSize = 20.sp,color = MaterialTheme.colorScheme.onPrimary)
                    Slider(
                        value = sliderPosition,
                        onValueChange = { changeSliderValue(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.onPrimary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        steps = 3,
                        valueRange = 0f..10f
                    )
                }


                Text(text = sliderPosition.toString(),color = MaterialTheme.colorScheme.onPrimary)
            }


        }


        /**
         * A Row composable meant to be clicked on by the user
         *
         *
         * @param changeExpandedState a function that will called when the user clicks on this composable
         * @param title a string that will be shown to the user and allow the user to identify what they are clicking on
         * */
        @Composable
        fun ClickableRow(
            changeExpandedState:() ->Unit,
            title:String,

            ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    )
                    .clickable { changeExpandedState() },

                verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.close_icon_description),
                    tint = Color.White,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(title, fontSize = 20.sp,modifier = Modifier.padding(end = 5.dp),color = MaterialTheme.colorScheme.onPrimary)
                Column(modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 5.dp)) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(shape = CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary)
                    )
                }
                Divider(thickness = 1.dp, color = Color.Black, modifier = Modifier.fillMaxWidth())

            }
        }


        /**
         * A composable meant to hold the hold the current value of the drop down menu and open the drop down menu when clicked
         *
         * @param selectedText  a String representing the current value of the selected value
         * @param expandMenu a function that will run when the Row is clicked
         * */
        @Composable
        fun DropDownRow(
            selectedText:String,
            expandMenu:() ->Unit
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expandMenu() })
                    .background(Color.Gray),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.close_icon_description),
                    tint = Color.White,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    selectedText,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.close_icon_description),
                    tint = Color.White,
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
        }

    }/**END OF PARTS***/

    //Rule: Constants declarations should always come after the Parts declaration
    /**
     * Constants represents all of the constant values that are used inside the [AutoMod] experience. There are
     * currently 5 constants:
     *
     * 1) [discriminationList]
     * 2) [hostilityList]
     * 3) [sexualList]
     * 4) [profanityList]
     * 5) [filteringLevels]
     * */
    private object Constants{
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as discrimination
         * */
        val discriminationList = listOf<TitleSubTitle>(
            TitleSubTitle("Disability","Demonstrating hatred or prejudice based on perceived or actual mental or physical abilities"),
            TitleSubTitle("Sexuality, sex, or gender","Demonstrating hatred or prejudice based on sexual identity, sexual orientation, gender identity, or gender expression"),
            TitleSubTitle("Misogyny","Demonstrating hatred or prejudice against women, including sexual objectification"),
            TitleSubTitle("Race, ethnicity, or religion","Demonstrating hatred or prejudice based on race, ethnicity, or religion"),
        )
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as hostility
         * */
        val hostilityList = listOf<TitleSubTitle>(
            TitleSubTitle("Aggression","Threatening, inciting, or promoting violence or other harm"),
            TitleSubTitle("Bullying","Demonstrating hatred or prejudice based on sexual identity, sexual orientation, gender identity, or gender expression"),
        )
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as sexual
         * */
        val sexualList = listOf<TitleSubTitle>(
            TitleSubTitle("Sex-based terms","Sexual acts, anatomy"),
        )
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as profanity
         * */
        val profanityList = listOf<TitleSubTitle>(
            TitleSubTitle("Swearing","Swear words, &*^!#@%*"),
        )
        /**
         * A list of Strings representing all the available levels of filtering
         * */
        val filteringLevels = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering")
    }



} /**END OF AUTOMOD***/