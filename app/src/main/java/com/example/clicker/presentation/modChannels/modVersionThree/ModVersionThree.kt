package com.example.clicker.presentation.modChannels.modVersionThree

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
enum class Sections {
    ONE, TWO, THREE
}


@Composable
fun ModVersionThree(
    boxOneOffsetY: Float,
    setBoxOneOffset:(Float) ->Unit,
    boxOneDragState: DraggableState,
    boxOneSection: Sections,
    boxOneIndex:Int,
    boxOneDragging: Boolean,
    setBoxOneDragging: (Boolean) -> Unit,

/*************** BOX TWO PARAMETERS***********************************/
    boxTwoOffsetY: Float,
    setBoxTwoOffset:(Float) ->Unit,
    boxTwoDragState: DraggableState,
    boxTwoSection: Sections,
    boxTwoIndex:Int,
    boxTwoDragging: Boolean,
    setBoxTwoDragging: (Boolean) -> Unit,

    /*************** BOX THREE PARAMETERS***********************************/
    boxThreeOffsetY: Float,
    setBoxThreeOffset:(Float) ->Unit,
    boxThreeDragState: DraggableState,
    boxThreeSection: Sections,
    boxThreeIndex:Int,
    boxThreeDragging: Boolean,
    setBoxThreeDragging: (Boolean) -> Unit,



){
    //TODO: TAKE ALL OF THIS CODE AND MOVE IT TO A VIEWMODEL



    var boxHeight by remember { mutableStateOf((Resources.getSystem().displayMetrics.heightPixels/8.4).dp) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Text("ModalNavigationDrawer",color = Color.Red)
        },
    ) {

    }

    Scaffold(
        topBar = {
            CustomTopBar(
                showDrawerFunc = {
                    scope.launch { drawerState.open() }
                }
            )
        },
        bottomBar = {},
        floatingActionButton = {}
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ){
            /**************************BOX ONE **************************/
            DragBox(
                boxHeight=boxHeight,
                boxOffset =boxOneOffsetY,
                dragState = boxOneDragState,
                modifier = Modifier.zIndex(if(boxOneDragging)2f else 1f),
                onDragStoppedFuc = {
                    Log.d("BoxOneOffsetLogging","section -->${boxOneSection}")
                    setBoxOneDragging(false)
                    when(boxOneSection){
                        Sections.ONE ->{
                            setBoxOneOffset(0f)
                        }
                        Sections.TWO ->{
                            setBoxOneOffset(700f)
                        }
                        Sections.THREE ->{
                            setBoxOneOffset(700f *2)
                        }
                    }
                },
                onDoubleClick = {
                    setBoxOneDragging(true)
                }
            ){
                ContentDragBox(boxOneIndex)

            }

            /**************************BOX TWO **************************/
            DragBox(
                boxHeight=boxHeight,
                boxOffset =boxTwoOffsetY,

                dragState = boxTwoDragState,
                modifier = Modifier.zIndex(if(boxTwoDragging)2f else 1f),
                onDragStoppedFuc = {
                    setBoxTwoDragging(false)
                    when(boxTwoSection){

                        //todo: change these two box one
                        Sections.ONE ->{
                            setBoxTwoOffset(0f)
                        }
                        Sections.TWO ->{
                            setBoxTwoOffset(700f)
                        }
                        Sections.THREE ->{
                            setBoxTwoOffset(700f *2)
                        }
                    }
                },
                onDoubleClick = {
                    setBoxTwoDragging(true)
                }
            ){
                ContentDragBox(boxTwoIndex)

            }
            /**************************BOX THREE **************************/
            DragBox(
                boxHeight=boxHeight,
                boxOffset =boxThreeOffsetY,
                dragState = boxThreeDragState,
                modifier = Modifier.zIndex(if(boxThreeDragging)2f else 1f),
                onDragStoppedFuc = {
                    setBoxThreeDragging(false)
                    when(boxThreeSection){
                        //todo: change these two box one
                        Sections.ONE ->{
                            setBoxThreeOffset(0f)
                        }
                        Sections.TWO ->{
                            setBoxThreeOffset(700f)
                        }
                        Sections.THREE ->{
                            setBoxThreeOffset(700f *2)
                        }
                    }
                },
                onDoubleClick = {
                    setBoxThreeDragging(true)
                }
            ){
                ContentDragBox(boxThreeIndex)

            }


        }

    }


    /******END OF THE SCAFFOLD*********/

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragBox(
    boxHeight: Dp,
    boxOffset:Float,
    dragState: DraggableState,
    modifier: Modifier,
    onDragStoppedFuc:()->Unit,
    onDoubleClick:()->Unit,
    content: @Composable () -> Unit
){
    Box(
        modifier = modifier
            .height(boxHeight)
            .fillMaxWidth()
            .offset { IntOffset(0, boxOffset.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = dragState,
                onDragStopped = {
                    onDragStoppedFuc()
                }
            )
            .combinedClickable(
                onClick = {  },
                onDoubleClick = {
                    onDoubleClick()
                },

            )

    ){
        content()

    }
}

@Composable
fun CustomTopBar(
    showDrawerFunc:()->Unit
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary)
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(

        ){
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "close mod view",
                modifier = Modifier
                    .clickable {
                        showDrawerFunc()
                    }
                    .size(35.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text("Mod View", fontSize = MaterialTheme.typography.headlineLarge.fontSize, color = MaterialTheme.colorScheme.onPrimary)
        }

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "close mod view",
            modifier = Modifier
                .clickable {

                }
                .size(35.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )


    }
}

@Composable
fun ContentDragBox(
    contentIndex:Int
){
    when(contentIndex){
        0 ->{
            Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {

            }
        }
        1 ->{
            Column(modifier = Modifier.fillMaxSize().background(Color.Red)) {

            }
        }
        2 ->{
            Column(modifier = Modifier.fillMaxSize().background(Color.Blue)) {

            }
        }
        3 ->{
            Column(modifier = Modifier.fillMaxSize().background(Color.Green)) {

            }
        }
    }

}