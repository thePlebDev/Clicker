package com.example.clicker.presentation.modView

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
data class BoxZIndexes(
    val boxOneZIndex:Float = 0F,
    val boxTwoZIndex:Float = 0F,
    val boxThreeZIndex:Float = 0F,

    )
data class BoxTypeIndex(
    val boxOneIndex:Int = 1,
    val boxTwoIndex:Int = 2,
    val boxThreeIndex:Int = 3,
)
data class IndivBoxHeight(
    val boxOne:Dp = 0.dp,
    val boxTwo:Dp = 0.dp,
    val boxThree:Dp = 0.dp
)

@HiltViewModel
class ModViewDragStateViewModel @Inject constructor(): ViewModel(){
    private val boxOne = "BOXONE"
    private val boxTwo = "BOXTWO"
    private val boxThree= "BOXTHREE"
    private var fullModeActive = false
    val indivBoxSize = (Resources.getSystem().displayMetrics.heightPixels/8.4).dp //264
    val sectionBreakPoint = ((Resources.getSystem().displayMetrics.heightPixels/3.20)-200).toInt() //539
    val animateToOnDragStop = (Resources.getSystem().displayMetrics.heightPixels/3.20).toFloat() //704f


    private var _dragStateOffsets: MutableState<BoxDragStateOffsets> = mutableStateOf(
        BoxDragStateOffsets()
    )
    val dragStateOffsets: State<BoxDragStateOffsets> = _dragStateOffsets

    private var _isDragging: MutableState<IsBoxDragging> = mutableStateOf(IsBoxDragging())
    val isDragging: State<IsBoxDragging> = _isDragging

    private val _boxIndexes: MutableState<BoxZIndexes> = mutableStateOf(BoxZIndexes())
    val boxIndexes: State<BoxZIndexes> = _boxIndexes

    private val _boxTypeIndex: MutableState<BoxTypeIndex> = mutableStateOf(BoxTypeIndex())
    val boxTypeIndex: State<BoxTypeIndex> = _boxTypeIndex

    private val _indivBoxHeight: MutableState<IndivBoxHeight> = mutableStateOf(
        IndivBoxHeight(
            boxOne = (Resources.getSystem().displayMetrics.heightPixels/8.4).dp,
            boxTwo = (Resources.getSystem().displayMetrics.heightPixels/8.4).dp,
            boxThree = (Resources.getSystem().displayMetrics.heightPixels/8.4).dp
        )
    )
    val indivBoxHeight: State<IndivBoxHeight> = _indivBoxHeight

    private val _deleteOffset: MutableState<Float> = mutableStateOf(0f)
    val deleteOffset: State<Float> = _deleteOffset

    private val _showDrawerError: MutableState<Boolean> = mutableStateOf(false)
    val showDrawerError: State<Boolean> = _showDrawerError


    private val _showModView: MutableState<Boolean> = mutableStateOf(false)
    val showModView: State<Boolean> = _showModView


    private val stateList = MutableStateFlow(listOf(boxOne,boxTwo,boxThree))

    fun setShowModView(value:Boolean){
        _showModView.value = value
    }

    init{
        _dragStateOffsets.value =_dragStateOffsets.value.copy(
            boxOneOffsetY =0f,
            boxTwoOffsetY = (Resources.getSystem().displayMetrics.heightPixels/3.20).toFloat(),
            boxThreeOffsetY = (Resources.getSystem().displayMetrics.heightPixels/3.20).toFloat() *2

        )
        Log.d("bottomOfTheScreen","indiv box height --> ${indivBoxSize}")
        Log.d("bottomOfTheScreen","indiv box height float --> ${indivBoxSize.value}")
        Log.d("bottomOfTheScreen","box 3 offset --> ${_dragStateOffsets.value.boxThreeOffsetY}")
        Log.d("bottomOfTheScreen",
            "combined values --> ${indivBoxSize.value + _dragStateOffsets.value.boxThreeOffsetY}")
        Log.d("bottomOfTheScreen",
            "boxThreeOffsetY --> ${indivBoxSize.value + _dragStateOffsets.value.boxThreeOffsetY}")
        val combinedOffset = (indivBoxSize.value + _dragStateOffsets.value.boxThreeOffsetY) - 150f
        _deleteOffset.value =combinedOffset


    }

    fun changeBoxTypeIndex(box:String,value:Int){
        if(box == "ONE"){
            _boxTypeIndex.value = _boxTypeIndex.value.copy(
                boxOneIndex = value
            )

        }
        if(box == "TWO"){
            _boxTypeIndex.value = _boxTypeIndex.value.copy(
                boxTwoIndex = value
            )

        }
        if(box == "THREE"){
            _boxTypeIndex.value = _boxTypeIndex.value.copy(
                boxThreeIndex = value
            )
        }
    }

    fun checkBoxIndexAvailability(newBoxIndex:Int){
        val boxOneIndex = _boxTypeIndex.value.boxOneIndex
        val boxTwoIndex = _boxTypeIndex.value.boxTwoIndex
        val boxThreeIndex = _boxTypeIndex.value.boxThreeIndex

        if(boxOneIndex != 0 && boxTwoIndex != 0 && boxThreeIndex != 0){
            _showDrawerError.value = true
            viewModelScope.launch {
                delay(1000)
                _showDrawerError.value = false
            }
        }else{
            if(fullModeActive){
                resetBodySizes()
            }
            if(boxOneIndex == 0){
                checkTripleIndexBoxOne(newBoxIndex)
            }
            else if(boxTwoIndex == 0){
                checkTripleIndexBoxTwo(newBoxIndex)

            }
            else if(boxThreeIndex == 0){
                checkTripleIndexBoxThree(newBoxIndex)

            }
        }

    }
    private fun checkDoubleMode(newBoxIndex: Int):Boolean{
        val boxOneIndex = _boxTypeIndex.value.boxOneIndex
        val boxTwoIndex = _boxTypeIndex.value.boxTwoIndex
        val boxThreeIndex = _boxTypeIndex.value.boxThreeIndex
       return when{
            boxOneIndex == newBoxIndex ->{
                true
            }
            boxTwoIndex == newBoxIndex ->{
                true
            }
            boxThreeIndex == newBoxIndex ->{
               true
            }

           else -> {false}
       }

    }
    fun resetBodySizes(){
        _boxTypeIndex.value = _boxTypeIndex.value.copy(
            boxOneIndex = 0,
            boxTwoIndex = 0,
            boxThreeIndex = 0
        )
        _indivBoxHeight.value = _indivBoxHeight.value.copy(
            boxOne = (Resources.getSystem().displayMetrics.heightPixels/8.4).dp,
            boxTwo =(Resources.getSystem().displayMetrics.heightPixels/8.4).dp,
            boxThree =(Resources.getSystem().displayMetrics.heightPixels/8.4).dp,
        )
        fullModeActive = false
    }

    fun checkTripleIndexBoxOne(newBoxIndex: Int){
//        private val boxOne = "BOXONE"
//        private val boxTwo = "BOXTWO"
//        private val boxThree= "BOXTHREE"
        val boxTwoIndex = _boxTypeIndex.value.boxTwoIndex
        val boxThreeIndex = _boxTypeIndex.value.boxThreeIndex
        val boxOneIndex = _boxTypeIndex.value.boxOneIndex

        if(boxTwoIndex == newBoxIndex && boxThreeIndex == newBoxIndex) {
            Log.d("checkTripleIndexBoxOne","FULL MODE ")
            fullModeActive = true
            _boxTypeIndex.value = _boxTypeIndex.value.copy(boxOneIndex = newBoxIndex)
            setBottomBoxLarge()


        }else{
            Log.d("checkTripleIndexBoxOne","NORMAL")

            _boxTypeIndex.value = _boxTypeIndex.value.copy(boxOneIndex = newBoxIndex)
        }

    }
    fun checkTripleIndexBoxTwo(newBoxIndex: Int){
//        private val boxOne = "BOXONE"
//        private val boxTwo = "BOXTWO"
//        private val boxThree= "BOXTHREE"
        val boxOneIndex = _boxTypeIndex.value.boxOneIndex
        val boxThreeIndex = _boxTypeIndex.value.boxThreeIndex
        val boxTwoIndex = _boxTypeIndex.value.boxTwoIndex

        if(boxOneIndex == newBoxIndex && boxThreeIndex == newBoxIndex) {
            fullModeActive = true
            Log.d("checkTripleIndexBoxOne","FULL MODE ")
            _boxTypeIndex.value = _boxTypeIndex.value.copy(boxTwoIndex = newBoxIndex)
            setBottomBoxLarge()

        }else{

            _boxTypeIndex.value = _boxTypeIndex.value.copy(boxTwoIndex = newBoxIndex)
        }

    }
    //todo: find who has the same index and where is it on the array

    fun checkTripleIndexBoxThree(newBoxIndex: Int){

//        private val boxOne = "BOXONE"
//        private val boxTwo = "BOXTWO"
//        private val boxThree= "BOXTHREE"
        val boxTwoIndex = _boxTypeIndex.value.boxTwoIndex
        val boxOneIndex = _boxTypeIndex.value.boxOneIndex
        val boxThreeIndex = _boxTypeIndex.value.boxThreeIndex

        if(boxTwoIndex == newBoxIndex && boxOneIndex == newBoxIndex) {
            fullModeActive = true
            Log.d("checkTripleIndexBoxOne","FULL MODE ")
            _boxTypeIndex.value = _boxTypeIndex.value.copy(boxThreeIndex = newBoxIndex)
            setBottomBoxLarge()

        }else{
            Log.d("checkTripleIndexBoxOne","NORMAL")
            _boxTypeIndex.value = _boxTypeIndex.value.copy(boxThreeIndex = newBoxIndex)
        }

    }

    fun setBottomBoxLarge(){
        when(stateList.value[0]){
            "BOXONE" ->{
                _indivBoxHeight.value = _indivBoxHeight.value.copy(
                    boxOne = ((Resources.getSystem().displayMetrics.heightPixels/8.4).dp * 3),
                    boxTwo = 0.dp,
                    boxThree = 0.dp
                )
                // todo: I think that this is causing the problem
                _boxTypeIndex.value = _boxTypeIndex.value.copy(
                    boxTwoIndex = 7,
                    boxThreeIndex = 7
                )

            }
            "BOXTWO" ->{
                _indivBoxHeight.value = _indivBoxHeight.value.copy(
                    boxTwo = ((Resources.getSystem().displayMetrics.heightPixels/8.4).dp * 3),
                    boxOne = 0.dp,
                    boxThree = 0.dp
                )
                _boxTypeIndex.value = _boxTypeIndex.value.copy(
                    boxOneIndex = 7,
                    boxThreeIndex = 7
                )

            }
            "BOXTHREE" ->{
                _indivBoxHeight.value = _indivBoxHeight.value.copy(
                    boxThree = ((Resources.getSystem().displayMetrics.heightPixels/8.4).dp * 3),
                    boxOne = 0.dp,
                    boxTwo = 0.dp
                )
                _boxTypeIndex.value = _boxTypeIndex.value.copy(
                    boxOneIndex = 7,
                    boxTwoIndex = 7
                )
            }
        }
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