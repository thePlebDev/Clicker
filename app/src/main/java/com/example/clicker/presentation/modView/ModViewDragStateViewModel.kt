package com.example.clicker.presentation.modView

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BoxDragStateOffsets(
    val boxOneOffsetY:Float = 0f,
    val boxTwoOffsetY:Float = 700f,
    val boxThreeOffsetY:Float = 700f *2,
)
data class IsBoxDragging(
    val boxOneDragging:Boolean = false,
    val boxTwoDragging:Boolean = false,
    val boxThreeDragging:Boolean = false,
)
data class BoxIndexes(
    val boxOneZIndex:Float = 0F,
    val boxTwoZIndex:Float = 0F,
    val boxThreeZIndex:Float = 0F,

    )

@HiltViewModel
class ModViewDragStateViewModel @Inject constructor(): ViewModel(){
    private val boxOne = "BOXONE"
    private val boxTwo = "BOXTWO"
    private val boxThree= "BOXTHREE"
    val indivBoxSize = (Resources.getSystem().displayMetrics.heightPixels/8.4).dp //264
    val sectionBreakPoint = ((Resources.getSystem().displayMetrics.heightPixels/3.20)-200).toInt() //539
    val animateToOnDragStop = (Resources.getSystem().displayMetrics.heightPixels/3.20).toFloat() //704f


    private var _dragStateOffsets: MutableState<BoxDragStateOffsets> = mutableStateOf(
        BoxDragStateOffsets()
    )
    val dragStateOffsets: State<BoxDragStateOffsets> = _dragStateOffsets

    private var _isDragging: MutableState<IsBoxDragging> = mutableStateOf(IsBoxDragging())
    val isDragging: State<IsBoxDragging> = _isDragging

    private val _boxIndexes: MutableState<BoxIndexes> = mutableStateOf(BoxIndexes())
    val boxIndexes: State<BoxIndexes> = _boxIndexes


    private val stateList = MutableStateFlow(listOf(boxOne,boxTwo,boxThree))

    init{
        _dragStateOffsets.value =_dragStateOffsets.value.copy(
            boxOneOffsetY =0f,
            boxTwoOffsetY = (Resources.getSystem().displayMetrics.heightPixels/3.20).toFloat(),
            boxThreeOffsetY = (Resources.getSystem().displayMetrics.heightPixels/3.20).toFloat() *2

        )

    }


    fun setBoxOneOffset(boxOneOffset:Float){
        _dragStateOffsets.value = _dragStateOffsets.value.copy(
            boxOneOffsetY = boxOneOffset
        )
    }
    fun setBoxTwoOffset(boxTwoOffset:Float){
        _dragStateOffsets.value = _dragStateOffsets.value.copy(
            boxTwoOffsetY = boxTwoOffset
        )
    }
    fun setBoxThreeOffset(boxThreeOffset:Float){
        _dragStateOffsets.value = _dragStateOffsets.value.copy(
            boxThreeOffsetY = boxThreeOffset
        )
    }
    fun setBoxOneDragging(isDragging:Boolean){
        _isDragging.value = _isDragging.value.copy(
            boxOneDragging = isDragging
        )
    }
    fun setBoxTwoDragging(isDragging:Boolean){
        _isDragging.value = _isDragging.value.copy(
            boxTwoDragging = isDragging
        )
    }
    fun setBoxThreeDragging(isDragging:Boolean){
        _isDragging.value = _isDragging.value.copy(
            boxThreeDragging = isDragging
        )
    }






    /**********************THE DRAG STATES ARE GOING TO GO HERE**********************************/
    var boxOneDragState = DraggableState { delta ->
        Log.d("boxOneDragState","isDragging.value.boxOneDragging -->${_isDragging.value.boxOneDragging}")
        if(_isDragging.value.boxOneDragging){
            _boxIndexes.value = _boxIndexes.value.copy(
                boxOneZIndex = 1f,
                boxTwoZIndex = 0f,
                boxThreeZIndex = 0f
            )
            _isDragging.value =_isDragging.value.copy(
                boxTwoDragging = false,
                boxThreeDragging = false
            )

            val itemInPositionOne = stateList.value[0]
            val itemInPositionTwo = stateList.value[1]
            val itemInPositionThree = stateList.value[2]
            val boxOneOffsetY =_dragStateOffsets.value.boxOneOffsetY

            // Log.d("AnotherTherasdf","delta  -> ${delta >0}")

            when{


                boxOneOffsetY <sectionBreakPoint ->{

                    if(itemInPositionOne != boxOne){
                        stateList.tryEmit(listOf(boxOne,itemInPositionOne,itemInPositionThree))

                    }

                }
                boxOneOffsetY >sectionBreakPoint && boxOneOffsetY <(sectionBreakPoint*2) ->{


                    if(itemInPositionOne == boxOne){

                        stateList.tryEmit(listOf(itemInPositionTwo,itemInPositionOne,itemInPositionThree))
                        Log.d("itemInPositionOneChecking","boxOne---> $stateList")
                    }
                    else if(itemInPositionThree == boxOne){

                        stateList.tryEmit(listOf(itemInPositionOne,itemInPositionThree,itemInPositionTwo))
                        Log.d("itemInPositionOneChecking","boxThree---> $stateList")
                    }
                    else if(itemInPositionTwo == boxOne){
                        stateList.tryEmit(listOf(itemInPositionOne,itemInPositionTwo,itemInPositionThree))
                        Log.d("itemInPositionOneChecking","boxTwo---> $stateList")
                    }

                }

                boxOneOffsetY >=(sectionBreakPoint*2)->{

                    if(itemInPositionThree != boxOne){
                        stateList.tryEmit(listOf(itemInPositionOne,itemInPositionThree,boxOne))

                    }
                }
            }

            _dragStateOffsets.value =_dragStateOffsets.value.copy(
                boxOneOffsetY = boxOneOffsetY + delta
            )
        }



    }

    /*********************END OF BOX ONE DRAGGING******************************************/

    var boxTwoDragState = DraggableState { delta ->
            if(_isDragging.value.boxTwoDragging){
                _boxIndexes.value = _boxIndexes.value.copy(
                    boxOneZIndex = 0f,
                    boxTwoZIndex = 1f,
                    boxThreeZIndex = 0f
                )
                _isDragging.value =_isDragging.value.copy(
                    boxOneDragging = false,
                    boxThreeDragging = false
                )



                val itemInPositionOne = stateList.value[0]
                val itemInPositionTwo = stateList.value[1]
                val itemInPositionThree = stateList.value[2]
                val boxTwoOffsetY =_dragStateOffsets.value.boxTwoOffsetY
                when{


                    boxTwoOffsetY <sectionBreakPoint ->{

                        if(itemInPositionOne != boxTwo){
                            stateList.tryEmit(listOf(boxTwo,itemInPositionOne,itemInPositionThree))

                        }

                    }
                    boxTwoOffsetY >sectionBreakPoint && boxTwoOffsetY <(sectionBreakPoint*2) ->{


                        if(itemInPositionOne == boxTwo){

                            stateList.tryEmit(listOf(itemInPositionTwo,itemInPositionOne,itemInPositionThree))
                            Log.d("itemInPositionOneChecking","boxOne---> $stateList")
                        }
                        else if(itemInPositionThree == boxTwo){

                            stateList.tryEmit(listOf(itemInPositionOne,itemInPositionThree,itemInPositionTwo))
                            Log.d("itemInPositionOneChecking","boxThree---> $stateList")
                        }
                        else if(itemInPositionTwo == boxTwo){
                            stateList.tryEmit(listOf(itemInPositionOne,itemInPositionTwo,itemInPositionThree))
                            Log.d("itemInPositionOneChecking","boxTwo---> $stateList")
                        }

                    }

                    boxTwoOffsetY >=(sectionBreakPoint*2)->{

                        if(itemInPositionThree != boxTwo){
                            stateList.tryEmit(listOf(itemInPositionOne,itemInPositionThree,boxTwo))

                        }
                    }
                }
                _dragStateOffsets.value =_dragStateOffsets.value.copy(
                    boxTwoOffsetY = boxTwoOffsetY + delta
                )
            }

        }

    /************************THIS IS THE END OF THE BOX TWO DRAG STATE********************************************/

    var boxThreeDragState = DraggableState { delta ->
            if(_isDragging.value.boxThreeDragging){
                _boxIndexes.value = _boxIndexes.value.copy(
                    boxOneZIndex = 0f,
                    boxTwoZIndex = 0f,
                    boxThreeZIndex = 1f
                )
                _isDragging.value =_isDragging.value.copy(
                    boxOneDragging = false,
                    boxTwoDragging = false,

                    )



                val itemInPositionOne = stateList.value[0]
                val itemInPositionTwo = stateList.value[1]
                val itemInPositionThree = stateList.value[2]
                val boxThreeOffsetY =_dragStateOffsets.value.boxThreeOffsetY
                when{


                    boxThreeOffsetY <sectionBreakPoint ->{

                        if(itemInPositionOne != boxThree){
                            stateList.tryEmit(listOf(boxThree,itemInPositionOne,itemInPositionThree))

                        }

                    }
                    boxThreeOffsetY >sectionBreakPoint && boxThreeOffsetY <(sectionBreakPoint*2) ->{


                        if(itemInPositionOne == boxThree){

                            stateList.tryEmit(listOf(itemInPositionTwo,itemInPositionOne,itemInPositionThree))
                            Log.d("itemInPositionOneChecking","boxOne---> $stateList")
                        }
                        else if(itemInPositionThree == boxThree){

                            stateList.tryEmit(listOf(itemInPositionOne,itemInPositionThree,itemInPositionTwo))
                            Log.d("itemInPositionOneChecking","boxThree---> $stateList")
                        }
                        else if(itemInPositionTwo == boxThree){
                            stateList.tryEmit(listOf(itemInPositionOne,itemInPositionTwo,itemInPositionThree))
                            Log.d("itemInPositionOneChecking","boxTwo---> $stateList")
                        }

                    }

                    boxThreeOffsetY >=(sectionBreakPoint*2)->{

                        if(itemInPositionThree != boxThree){
                            stateList.tryEmit(listOf(itemInPositionOne,itemInPositionThree,boxThree))

                        }
                    }
                }
                _dragStateOffsets.value =_dragStateOffsets.value.copy(
                    boxThreeOffsetY = boxThreeOffsetY + delta
                )
            }

        }


    /*******************THIS IS THE BEGINNING OF THE stateList EVENT BUS************************************************/
    init {
        viewModelScope.launch {
            stateList.collect{ stateList ->

                val indexOfBoxOne = stateList.indexOf(boxOne)
                val indexOfBoxTwo = stateList.indexOf(boxTwo)
                val indexOfBoxThree = stateList.indexOf(boxThree)

                if(_isDragging.value.boxOneDragging){
                    if(indexOfBoxTwo == 0){
                        Log.d("boxOneDraggingThingers","THERE SHOULD BE A HAPTIC FEEDBACK")

                        setBoxTwoOffset(0f)
                    }
                    if(indexOfBoxTwo == 1){

                        setBoxTwoOffset(animateToOnDragStop)
                    }
                    if(indexOfBoxTwo == 2){

                        setBoxTwoOffset(animateToOnDragStop *2)
                    }
                    if(indexOfBoxThree == 0){
                        setBoxThreeOffset(0f)
                    }
                    if(indexOfBoxThree == 1){
                        setBoxThreeOffset(animateToOnDragStop)
                    }
                    if(indexOfBoxThree == 2){
                        setBoxThreeOffset(animateToOnDragStop *2)
                    }
                }
                else if(_isDragging.value.boxTwoDragging){
                    if(indexOfBoxOne == 0){
                        setBoxOneOffset(0f)
                    }
                    if(indexOfBoxOne == 1){
                        setBoxOneOffset(animateToOnDragStop)
                    }
                    if(indexOfBoxOne == 2){
                        setBoxOneOffset(animateToOnDragStop *2)
                    }


                    if(indexOfBoxThree == 0){
                        setBoxThreeOffset(0f)
                    }
                    if(indexOfBoxThree == 1){
                        setBoxThreeOffset(animateToOnDragStop)
                    }
                    if(indexOfBoxThree == 2){
                        setBoxThreeOffset(animateToOnDragStop *2)
                    }
                }
                else if(_isDragging.value.boxThreeDragging){
                    if(indexOfBoxOne == 0){
                        setBoxOneOffset(0f)
                    }
                    if(indexOfBoxOne == 1){
                        setBoxOneOffset(animateToOnDragStop)
                    }
                    if(indexOfBoxOne == 2){
                        setBoxOneOffset(animateToOnDragStop *2)
                    }

                    if(indexOfBoxTwo == 0){
                        setBoxTwoOffset(0f)
                    }
                    if(indexOfBoxTwo == 1){
                        setBoxTwoOffset(animateToOnDragStop)
                    }
                    if(indexOfBoxTwo == 2){
                        setBoxTwoOffset(animateToOnDragStop *2)
                    }
                }

            }
        }
    }

}