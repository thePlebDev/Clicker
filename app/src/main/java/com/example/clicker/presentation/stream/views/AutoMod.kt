package com.example.clicker.presentation.stream.views

import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.clicker.presentation.stream.FilterType
import com.example.clicker.presentation.stream.views.AutoMod.Parts.DropDownRow


/**
 * Used inside the [Constants][AutoMod.Constants], it is meant to help define titles and there subtitles. This makes looping
 * and displaying text easier.
 * */
data class TitleSubTitle(
    val title: String, val subTitle: String,val Index:Int
)
//1)Rule: brief description followed by the number of implementations
/**
 * AutoMod represents all the UI composables used to build the auto moderation feature in the app
 *
 * - AutoMod contains 1 public top level implementation:
 * 1) [Settings]
 *
 * - AutoMod contains 4 top level private implementations:
 * 1) [ProfanityAutoModRow]
 * 2) [HostilityAutoModRow]
 * 3) [DiscriminationAutoModRow]
 * 4) [SexAutoModRow]
 *
 * */
object AutoMod {


    //Rule: brief description followed by builders used, then parameter description
    /**
     * Settings is the implementation that contains all the individual elements that makes
     * our user auto moderation experience
     *
     * - AutoScrollChatWithTextBox implements the [AutoModRows][Builders.AutoModRows] Builder
     *
     * @param sliderPosition a float value representing the current value of a [Slider]
     * @param changSliderPosition a function used to change the value of the [sliderPosition] to the current value
     * */
    @Composable
    fun Settings(
        sliderPosition: Float,
        changSliderPosition:(Float)->Unit,
        discriminationFilterList: List<String>,

        changeSelectedIndex:(Int,FilterType)->Unit,
        updateAutoModSettings:() ->Unit,


        swearingIndex: Int,
        sexBasedTermsIndex:Int,
        aggressionIndex:Int,
        bullyingIndex:Int,
        disabilityIndex: Int,
        sexualityIndex: Int,
        misogynyIndex: Int,
        raceIndex: Int,
        isModerator:Boolean,
        filterText: String

    ) {

        Builders.AutoModRows(
            slider ={
                Parts.AutoModSlider(
                    sliderPosition,
                    changeSliderValue = {currentValue ->changSliderPosition(currentValue)},
                    isModerator = isModerator,
                    filterText = filterText
            )
                    },
            hostilityRow = {
                HostilityAutoModRow(
                    filterLevels = discriminationFilterList,
                    changeSelectedIndex={selectedIndex,filterType -> changeSelectedIndex(selectedIndex,filterType)},
                    aggressionIndex =aggressionIndex,
                    bullyingIndex =bullyingIndex,
                )

            },
            discriminationRow = {
                DiscriminationAutoModRow(
                    filterLevels = discriminationFilterList,
                    changeSelectedIndex={selectedIndex,filterType -> changeSelectedIndex(selectedIndex,filterType)},
                    disabilityIndex =disabilityIndex,
                    sexualityIndex =sexualityIndex,
                    misogynyIndex =misogynyIndex,
                    raceIndex =raceIndex
                )
            },
            sexualRow = {
                SexAutoModRow(
                    filterLevels = discriminationFilterList,
                    changeSelectedIndex={selectedIndex,filterType -> changeSelectedIndex(selectedIndex,filterType)},
                    sexBasedTermsIndex = sexBasedTermsIndex
                )

            },
            profanityRow={

                ProfanityAutoModRow(
                    filterLevels = discriminationFilterList,
                    changeSelectedIndex={selectedIndex,filterType -> changeSelectedIndex(selectedIndex,filterType)},
                    swearingIndex = swearingIndex
                )
            },
            updateButton ={modifier ->
                Parts.UpdateSettingsButton(
                    updateAutoModSettings = {
                        updateAutoModSettings()
                    },
                    modifier=modifier
                )
            },
            isModerator = isModerator
        )
    }
    /**
     * HostilityAutoModRow is the private implementation that represents all the choices to the user regarding
     * the AutoMod Hostility settings
     *
     * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
     * @param changeSelectedIndex a function used to change the values inside of the [hostilityIndexData] object
     * @param hostilityIndexData a [HostilityIndexData] object representing the selected levels of filtering for the
     * Hostility section of AutoMod
     * */
    @Composable
    private fun HostilityAutoModRow(
        filterLevels: List<String>,
        changeSelectedIndex:(Int,FilterType)->Unit,
        aggressionIndex: Int,
        bullyingIndex: Int,
    ){
        Builders.ConditionalExpandableColumn(
            titleRow ={changeExpandableValue ->
                Parts.ClickableRow(
                    changeExpandedState = {
                        changeExpandableValue()
                    },
                    title = "Hostility"
                )
            },
            autoModColumnChoices={
                Parts.HostilityDataColumns(
                    filterLevels = filterLevels,
                    aggressionIndex =aggressionIndex,
                    bullyingIndex =bullyingIndex,
                    changeSelectedIndex ={index,filterType ->changeSelectedIndex(index,filterType)}
                )

            }
        )
    }

    /**
     * DiscriminationAutoModRow is the private implementation that represents all the choices to the user regarding
     * the AutoMod Discrimination settings
     *
     * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
     * @param changeSelectedIndex a function used to change the values inside of the [discriminationIndexData] object
     * @param discriminationIndexData a [DiscriminationIndexData] object representing the selected levels of filtering for the
     * Discrimination section of AutoMod
     * */
    @Composable
    private fun DiscriminationAutoModRow(
        filterLevels: List<String>,
        changeSelectedIndex:(Int,FilterType)->Unit,
        disabilityIndex: Int,
        sexualityIndex: Int,
        misogynyIndex: Int,
        raceIndex: Int,
    ){
        Builders.ConditionalExpandableColumn(
            titleRow = {changeExpandableValue ->
                Parts.ClickableRow(
                    changeExpandedState = {
                        changeExpandableValue()
                    },
                    title = "DISCRIMINATION AND SLURS"
                )
            },
            autoModColumnChoices={
                Parts.DiscriminationDataColumns(
                    filterLevels,
                    disabilityIndex,
                    sexualityIndex,
                    misogynyIndex,
                    raceIndex,
                    changeSelectedIndex
                )
            }
        )
    }

    /**
     * SexAutoModRow is the private implementation that represents all the choices to the user regarding
     * the AutoMod Sexuality settings
     *
     * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
     * @param changeSelectedIndex a function used to change the values inside of the [sexualIndexData] object
     * @param sexualIndexData a [SexualIndexData] object representing the selected levels of filtering for the
     * Sexuality section of AutoMod
     * */
    @Composable
    private fun SexAutoModRow(
        filterLevels: List<String>,
        changeSelectedIndex:(Int,FilterType)->Unit,
        sexBasedTermsIndex:Int
    ){
        Builders.ConditionalExpandableColumn(
            titleRow ={changeExpandableValue->
                Parts.ClickableRow(
                    changeExpandedState = {
                        changeExpandableValue()
                    },
                    title = "SEXUAL CONTENT"
                )
            },
            autoModColumnChoices={
                Parts.SexDataColumns(
                    filterLevels,
                    sexBasedTermsIndex,
                    changeSelectedIndex
                )

            }
        )

    }

    /**
     * ProfanityAutoModRow is the private implementation that represents all the choices to the user regarding
     * the AutoMod Profanity settings
     *
     * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
     * @param changeSelectedIndex a function used to change the values inside of the [profanityIndexData] object
     * @param profanityIndexData a [ProfanityIndexData] object representing the selected levels of filtering for the
     * Profanity section of AutoMod
     * */
    @Composable
    private fun ProfanityAutoModRow(
        filterLevels: List<String>,
        changeSelectedIndex:(Int,FilterType)->Unit,
        swearingIndex: Int,
    ){
        Builders.ConditionalExpandableColumn(
            titleRow ={changeExpandableValue->
                Parts.ClickableRow(
                    changeExpandedState = {
                        changeExpandableValue()
                    },
                    title = "PROFANITY"
                )
            },
            autoModColumnChoices={
                Parts.ProfanityDataColumns(
                    filterLevels,
                    swearingIndex,
                    changeSelectedIndex
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
         * AutoModRows is the basic layout for the user Auto Moderation experience. A example of what the typical UI looks like
         * with this builder can be found [HERE](https://theplebdev.github.io/Modderz-style-guide/#AutoModRows)
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
            updateButton:@Composable (modifier:Modifier) -> Unit,
            isModerator: Boolean
        ){
            Box(){
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    slider()
                    hostilityRow()
                    discriminationRow()
                    sexualRow()
                    profanityRow()
                }
                if(isModerator){
                    updateButton(modifier =Modifier.align(Alignment.TopEnd))
                }


            }

        }



        /**
         * ConditionalExpandableColumn is the basic layout for each individual AutoMod choice shown to the user.
         *
         * A example of what the typical UI looks like
         * with this builder can be found [HERE](https://theplebdev.github.io/Modderz-style-guide/#AutoModRows)
         *
         * @param titleRow a composable that will be shown to the user and identifying what level of filtering they have
         * chosen. Also, titleRow is given a function, ***changeExpandedState: () -> Unit***, which is used to change a
         * conditional state determining when [autoModColumnChoices] is shown to the user
         * @param autoModColumnChoices a composable that will show the user their AutoMod choices when [titleRow] is clicked
         * */
        @Composable
        fun ConditionalExpandableColumn(
            titleRow:@Composable (changeExpandedState: () -> Unit) -> Unit,
            autoModColumnChoices:@Composable () -> Unit,
        ){
            var expandedState by remember {
                mutableStateOf(false)
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)) {
                titleRow(
                    changeExpandedState = {
                        expandedState = !expandedState
                    }
                )
                if(expandedState){
                    autoModColumnChoices()
                }
            }

        }
    }/**END OF BUILDER*/

    /**
     * Parts represents the most individual parts of [AutoMod] and should be thought of as the individual
     * pieces that are used inside of a [Builders] to create the top level implementations that are used in the code base
     * */
    private object Parts{
        //Rule) Brief description, followed by how many parts are used and any params

        /**
         * ProfanityDataColumns represents each individual choice the user has under the Profanity setting of their AutoMod
         *
         * - Contains 1 extra part: [IndividualDropDownItem]
         *
         * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
         * @param changeSelectedIndex a function used to change the values inside of the [profanityIndexData] object
         * @param profanityIndexData a [ProfanityIndexData] object representing the selected levels of filtering for the
         * Profanity section of AutoMod
         * */
        @Composable
        fun ProfanityDataColumns(
            filterLevels: List<String>,
            swearingIndex: Int,
            changeSelectedIndex: (Int, FilterType) -> Unit,
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)) {

                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = swearingIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title = "Swearing",
                    subTitle = "Swear words, &*^!#@%*",
                    filterType = FilterType.SWEARING
                )

            }
        }

        /**
         * SexDataColumns represents each individual choice the user has under the Sexuality setting of their AutoMod
         *
         * - Contains 1 extra part: [IndividualDropDownItem]
         *
         * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
         * @param changeSelectedIndex a function used to change the values inside of the [sexualIndexData] object
         * @param sexualIndexData a [SexualIndexData] object representing the selected levels of filtering for the
         * Sexuality section of AutoMod
         * */
        @Composable
        fun SexDataColumns(
            filterLevels: List<String>,
            sexBasedTermsIndex:Int,
            changeSelectedIndex: (Int, FilterType) -> Unit,
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)) {

                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = sexBasedTermsIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title = "Sex-based terms",
                    subTitle = "Sexual acts, anatomy",
                    filterType = FilterType.SEXBASEDTERMS
                )

            }
        }

        /**
         * DiscriminationDataColumns represents each individual choice the user has under the Discrimination setting of their AutoMod
         *
         * - Contains 1 extra part: [IndividualDropDownItem]
         *
         * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
         * @param changeSelectedIndex a function used to change the values inside of the [discriminationIndexData] object
         * @param discriminationIndexData a [DiscriminationIndexData] object representing the selected levels of filtering for the
         * Discrimination section of AutoMod
         * */
        @Composable
        fun DiscriminationDataColumns(
            filterLevels: List<String>,
            disabilityIndex: Int,
            sexualityIndex: Int,
            misogynyIndex: Int,
            raceIndex: Int,
            changeSelectedIndex: (Int, FilterType) -> Unit,
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)){

                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = disabilityIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title ="Disability",
                    subTitle = "Demonstrating hatred or prejudice based on perceived or actual mental or physical abilities",
                    filterType = FilterType.DISABILITY
                )
                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = sexualityIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title ="Sexuality, sex, or gender",
                    subTitle = "Demonstrating hatred or prejudice based on sexual identity, sexual orientation, gender identity, or gender expression",
                    filterType = FilterType.SEXUALITY
                )
                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = misogynyIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title ="Misogyny",
                    subTitle = "Demonstrating hatred or prejudice against women, including sexual objectification",
                    filterType = FilterType.MISOGYNY
                )
                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = raceIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title ="Race, ethnicity, or religion",
                    subTitle = "Demonstrating hatred or prejudice based on race, ethnicity, or religion",
                    filterType = FilterType.RACE
                )
            }
        }

        /**
         * HostilityDataColumns represents each individual choice the user has under the Hostility setting of their AutoMod
         *
         * - Contains 1 extra part: [IndividualDropDownItem]
         *
         * @param filterLevels a List of String representing all the levels of filtering available to the AutoMod feature
         * @param changeSelectedIndex a function used to change the values inside of the [hostilityIndexData] object
         * @param hostilityIndexData a [HostilityIndexData] object representing the selected levels of filtering for the
         * Discrimination section of AutoMod
         * */
        @Composable
        fun HostilityDataColumns(
            filterLevels: List<String>,
            aggressionIndex: Int,
            bullyingIndex: Int,
            changeSelectedIndex: (Int, FilterType) -> Unit,
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)){
                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = aggressionIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title ="Aggression",
                    subTitle = "Threatening, inciting, or promoting violence or other harm",
                    filterType = FilterType.AGGRESSION
                )

                Parts.IndividualDropDownItem(
                    filterLevels = filterLevels,
                    selectedIndex = bullyingIndex,
                    changeSelectedIndex = { selectedIndex, filterType ->
                        changeSelectedIndex(
                            selectedIndex,
                            filterType
                        )
                    },
                    title ="Bullying",
                    subTitle = "Name-calling, insults, or antagonization",
                    filterType = FilterType.BULLYING
                )

            }

        }

        /**
         * IndividualDropDownItem represents a category in the user's AutoMod settings. Each IndividualDropDownItem will have a
         * [title] telling the user what category it represents and a [subTitle] going into further detail. IndividualDropDownItem
         * also contains a [DropdownRowMenu][Parts.DropdownRowMenu] which the user can click and adjust their AutoMod settings
         * accordingly to their moderation needs.
         *
         * - Contains 1 extra part: [DropdownRowMenu][Parts.DropdownRowMenu]
         *
         * @param filterLevels a list representing the levels of moderation available to the user
         * @param selectedIndex a Int used to determine which level of moderation the user has selected for this IndividualDropDownItem
         * @param changeSelectedIndex a function used to change the [selectedIndex] through the user of Int and [filterType]
         * @param title a String used to identify to the user which category of AutoMod this represents
         * @param subTitle a String expanding on the [title]
         * @param filterType a [FilterType] object used by the [changeSelectedIndex] function to identify which AutoMod category this
         * IndividualDropDownItem represents in the ViewModel and change it
         * */
        @Composable
        fun IndividualDropDownItem(
            filterLevels: List<String>,
            selectedIndex: Int,
            changeSelectedIndex: (Int, FilterType) -> Unit,
            title: String,
            subTitle:String,
            filterType:FilterType
        ){
            Column() {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onPrimary)
                Text(subTitle, fontSize = 18.sp,color = MaterialTheme.colorScheme.onPrimary ,modifier = Modifier.padding(horizontal =10.dp))
                Parts.DropdownRowMenu(
                    filterLevels = filterLevels,
                    selectedIndex = selectedIndex,
                    changeSelectedIndex = { selectedIndex, filter ->
                        changeSelectedIndex(
                            selectedIndex,
                            filter
                        )
                    },
                    filterType = filterType
                )
            }

        }

        /**
         * A composable meant to display a [DropdownMenu] and show the user a list of the [filteringLevels][Constants.filteringLevels]
         *
         * - Contains 1 extra part: [DropDownRow]
         * */
        @Composable
        fun DropdownRowMenu(
            filterLevels:List<String>,
            selectedIndex: Int,
            changeSelectedIndex:(Int,FilterType)->Unit,
            filterType: FilterType,
        ) {
            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .padding(horizontal = 10.dp)) {
                DropDownRow(
                    selectedText = filterLevels[selectedIndex],
                    expandMenu = {expanded = true}
                )

                //todo: This determines the width of the expanded menu to choose from
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                ) {
                    filterLevels.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            onClick = {
                            changeSelectedIndex(index,filterType)
                            expanded = false
                        },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically){
                                    Text(text = s, color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp)
                                   repeat(index+1){
                                       Icon(
                                           imageVector = Icons.Default.Delete,
                                           contentDescription = stringResource(R.string.close_icon_description),
                                           tint = Color.Red,
                                           modifier = Modifier.padding(end = 5.dp)
                                       )
                                   }
                                }
                            }
                        )
                    }
                }
            }

        }

        @Composable
        fun UpdateSettingsButton(
            updateAutoModSettings:() ->Unit,
            modifier: Modifier
        ){
            Button(
                modifier = modifier.padding(end=20.dp,top=70.dp),
                shape = RoundedCornerShape(10.dp),
                onClick = {updateAutoModSettings()},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Update",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
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
            changeSliderValue:(Float) ->Unit,
            isModerator: Boolean,
            filterText:String,
        ){
            var sliderValue by remember{ mutableFloatStateOf(0f) }
            Log.d("AutoModSlider","slider value --> $sliderValue")
            Column(modifier =Modifier.fillMaxWidth()){
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    Text("AutoMod Settings", fontSize = 20.sp,color = MaterialTheme.colorScheme.onPrimary)
                }
                Slider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    value = sliderPosition,
                    enabled = isModerator,
                    onValueChange = { changeSliderValue(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onPrimary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    valueRange = 0f..4f,
                    steps = 3,
                )
                Text(text = filterText,modifier = Modifier.padding(horizontal = 20.dp),color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)




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
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.fillMaxWidth())

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
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp
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
            TitleSubTitle("Disability","Demonstrating hatred or prejudice based on perceived or actual mental or physical abilities",0),
            TitleSubTitle("Sexuality, sex, or gender","Demonstrating hatred or prejudice based on sexual identity, sexual orientation, gender identity, or gender expression",0),
            TitleSubTitle("Misogyny","Demonstrating hatred or prejudice against women, including sexual objectification",0),
            TitleSubTitle("Race, ethnicity, or religion","Demonstrating hatred or prejudice based on race, ethnicity, or religion",0),
        )
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as hostility
         * */
        val hostilityList = listOf<TitleSubTitle>(
            TitleSubTitle("Aggression","Threatening, inciting, or promoting violence or other harm",0),
            TitleSubTitle("Bullying","Demonstrating hatred or prejudice based on sexual identity, sexual orientation, gender identity, or gender expression",0),
        )
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as sexual
         * */
        val sexualList = listOf<TitleSubTitle>(
            TitleSubTitle("Sex-based terms","Sexual acts, anatomy",0),
        )
        /**
         * A list of [TitleSubTitle] objects used to explain to the user what constitutes as profanity
         * */
        val profanityList = listOf<TitleSubTitle>(
            TitleSubTitle("Swearing","Swear words, &*^!#@%*",0),
        )
        /**
         * A list of Strings representing all the available levels of filtering
         * */
        val filteringLevels = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering")
    }



} /**END OF AUTOMOD***/