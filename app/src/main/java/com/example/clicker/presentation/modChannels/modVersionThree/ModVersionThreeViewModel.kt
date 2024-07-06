package com.example.clicker.presentation.modChannels.modVersionThree

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class Positions{
    TOP,CENTER, BOTTOM
}
enum class BoxNumber{
    ONE,TWO,THREE
}

data class BoxDragging(
    val boxOneDragging: Boolean,
    val boxTwoDragging: Boolean,
    val boxThreeDragging: Boolean,
)


//ok, so we have an array of this and when one drag state crosses a threshold, a value will be emitted with this data
// it will be a hot flow and we move the boxes based on this data
data class ModArrayData(
    val height:Float,
    val index:Int,
    val position:Positions, // this would be top center or bottom
    val boxNumber: BoxNumber,
    val dragging:Boolean,
    val doubleSize:Boolean,
)

@HiltViewModel
class ModVersionThreeViewModel @Inject constructor(): ViewModel(){

    //This stateList is what represents the states position relative to each other
    private val stateList = MutableStateFlow(listOf(
        ModArrayData(700f,1,Positions.TOP,BoxNumber.ONE,false,false),
        ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,false,false),
        ModArrayData(700f,3,Positions.BOTTOM,BoxNumber.THREE,false,false),
    )
    )
    val publicStateList: StateFlow<List<ModArrayData>> = stateList
    //todo: I think I can delete this but not 1000%
    private val _boxesDragging = mutableStateOf(BoxDragging(false,false,false))
    val boxesDragging: State<BoxDragging> = _boxesDragging

    private val _showPlacementError = mutableStateOf(false)
    val showPlacementError: State<Boolean> = _showPlacementError



    /****************************************BOX ONE RELATED STATE*********************************************************/
    var boxOneOffsetY by mutableStateOf(0f)
    var boxOneSection by mutableStateOf(Sections.ONE)
    var boxOneIndex by mutableIntStateOf(1)
    var deleteBoxOne by mutableStateOf(false)
    var boxOneHeight by mutableStateOf((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)

    fun setBoxOneOffset(newValue:Float){
        boxOneOffsetY = newValue
    }
    fun setBoxOneDragging(newValue: Boolean){
        val boxOneDouble = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
        if(boxOneDouble){
            _boxesDragging.value = _boxesDragging.value.copy(
                boxOneDragging = true,
                boxTwoDragging = false,
                boxThreeDragging = false,
            )

        }else{
            _boxesDragging.value = _boxesDragging.value.copy(
                boxOneDragging = newValue,
            )
        }

    }


    /**
     * */
    fun syncBoxOneIndex(newValue:Int){// called to make sure the index stays synced with statelist
       //I was wrong, we do need to worry about [top,center,bottom]
        boxOneIndex = newValue
        deleteBoxOne = false

        if(newValue ==0){ //THis means that we are going to delete box two

            boxOneHeight =(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
            //todo: before this goes up I need an error UI that tells me I can not add things
            val doubleSize =stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
            if(doubleSize){
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(doubleSize = false, index = 0)
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                //this is for boxOneIndex
                val checkBoxTwoIndex = boxTwo.index
                val checkBoxThreeIndex = boxThree.index
                if(checkBoxTwoIndex == 99){
                    syncBoxTwoIndex(0)
                }
                if(checkBoxThreeIndex == 99){
                    syncBoxThreeIndex(0)
                }


                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))

            }
            else{
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(doubleSize = false, index = 0)
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
            }


        }else{
            val top = stateList.value.find {it.position == Positions.TOP}!!.let {item ->
                if(item.boxNumber == BoxNumber.ONE){
                    item.copy(index = newValue)
                }else{
                    item
                }
            }
            val center = stateList.value.find {it.position == Positions.CENTER}!!.let {item ->
                if(item.boxNumber == BoxNumber.ONE){
                    item.copy(index = newValue)
                }else{
                    item
                }
            }
            val bottom = stateList.value.find {it.position == Positions.BOTTOM}!!.let {item ->
                if(item.boxNumber == BoxNumber.ONE){
                    item.copy(index = newValue)
                }else{
                    item
                }
            }
            stateList.tryEmit(listOf(top,center,bottom))
        }



    }

    var boxOneDragState = DraggableState { delta ->
        val boxOneIsDoubleSize = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
        if(boxOneIsDoubleSize){
            //todo: I need top, I need bottom and I need delete


            /********* ENTERING SECTION THREE (DELETING)************/
            if(boxOneOffsetY >= (0.6*(700f*2))){
                Log.d("boxSizeDoublingLogs","DELETE")
                if(!deleteBoxOne){
                    deleteBoxOne = true
                }

                //todo: add the deletion and fix when the box stops its index is set behind the black box

            }

            /********* ENTERING SECTION TWO(BOTTOM) ************/
            else if(boxOneOffsetY >= (0.6*(700f)) && boxOneOffsetY<=(0.6*(700f*2))){
                Log.d("boxSizeDoublingLogs","BOTTOM")
                //todo: check the delta
                if(deleteBoxOne){
                    deleteBoxOne = false
                }
                if(boxOneSection!=Sections.TWO){
                    Log.d("StateListUpdate","sectionTwoEnter")
                    if(0<=delta) { //true means dragging down when entering section 2
                        //top and center now become center and bottom
                        //bottom now becomes top
                        val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.THREE)
                        val newCenter  =ModArrayData(700f,2,Positions.CENTER,BoxNumber.ONE,true,true)
                        val newBottom = stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.TWO)

                        stateList.tryEmit(listOf(newTop,newCenter,newBottom))


                    }
                }
                boxOneSection = Sections.TWO

            }
            /**ENTERING SECTION ONE(TOP)*/
            else if(boxOneOffsetY <= (0.6*(700f))){
                Log.d("boxSizeDoublingLogs","TOP")
                if(boxOneSection != Sections.ONE){
                    val newTop =ModArrayData(700f,2,Positions.TOP,BoxNumber.ONE,true,true)
                    val newCenter = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.TWO)
                    val newBottom = stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.THREE)

                    stateList.tryEmit(listOf(newTop,newCenter,newBottom))
                }
                boxOneSection = Sections.ONE



            }

        }else{
            boxOneSingleSizeDragging(delta)
        }


        boxOneOffsetY += delta
    }

    fun boxOneSingleSizeDragging(
        delta:Float
    ){
        if(boxOneOffsetY >=1550f){
            Log.d("DeletingDRAGSTATE","DELETE")

            if(!deleteBoxOne){
                deleteBoxOne = true
            }
        }

        /********* ENTERING SECTION THREE ************/
        else if(boxOneOffsetY >= (0.6*(700f*2))){
            if(deleteBoxOne){
                deleteBoxOne = false
            }

            if(boxOneSection != Sections.THREE){
                Log.d("BoxOneStatechanging","THREE")
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter =stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val newBottom =ModArrayData(700f,1,Positions.BOTTOM,BoxNumber.ONE,true,false,)
                val newList = listOf(top,newCenter,newBottom)
                stateList.tryEmit(newList)
                boxOneSection = Sections.THREE
            }


        }
        /********* ENTERING SECTION TWO ************/
        else if(boxOneOffsetY >= (0.6*(700f)) && boxOneOffsetY<=(0.6*(700f*2))){
            Log.d("INSECTIONTWOLOGS","SECTION 2")


            if(boxOneSection != Sections.TWO){
                if(0>=delta){ //true means dragging up when entering section 2
                    val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                    val newCenter =ModArrayData(700f,1,Positions.CENTER,BoxNumber.ONE,true,false,)
                    val newBottom =stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else{ //means the user is dragging down when entering section 2
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter =ModArrayData(700f,1,Positions.CENTER,BoxNumber.ONE,true,false,)
                    val bottom = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false)
                    val newList = listOf(newTop,newCenter,bottom)
                    stateList.tryEmit(newList)

                }
                Log.d("BoxOneStatechanging","TWO")
                //get what ever is in the center and move it to the top

                boxOneSection = Sections.TWO
            }

        }
        /********* ENTERING SECTION ONE ************/
        else if(boxOneOffsetY <= (0.6*(700f))){

            if(boxOneSection != Sections.ONE){
                Log.d("BoxOneStatechanging","ONE")
                val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                val newTop =ModArrayData(700f,1,Positions.TOP,BoxNumber.ONE,true,false,)
                val bottom = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)

                boxOneSection = Sections.ONE
            }

        }
    }




    /****************************************BOX TWO RELATED STATE**********************************************************/
    var boxTwoOffsetY by mutableStateOf(700f)
    var boxTwoSection by mutableStateOf(Sections.TWO)
    var boxTwoIndex by mutableStateOf(2)
    var deleteBoxTwo by mutableStateOf(false)
    var boxTwoHeight by mutableStateOf((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
    fun setBoxTwoOffset(newValue:Float){
        boxTwoOffsetY = newValue
    }
    fun setBoxTwoDragging(newValue: Boolean){
        _boxesDragging.value = _boxesDragging.value.copy(
            boxTwoDragging = newValue
        )
    }

    //todo: OK I THINK THIS IS FINALIZED NOW AND IT WORKS
    fun syncBoxTwoIndex(newValue:Int){
        boxTwoIndex = newValue
        deleteBoxTwo = false
        if(newValue ==0){ //THis means that we are going to delete box two

            boxTwoHeight =(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
            //todo: before this goes up I need an error UI that tells me I can not add things
            val doubleSize =stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize
            if(doubleSize){
                syncBoxOneIndex(0)
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(doubleSize = false, index = 0)
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))

            }
            else{
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(doubleSize = false, index = 0)
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
            }


        }//end if boxOne is 0.
        else{
            //this code is operating under the idea of if the

            val top = stateList.value.find {it.position == Positions.TOP}!!.let {item ->
                if(item.boxNumber == BoxNumber.TWO){
                    item.copy(index = newValue)
                }else{
                    item
                }
            }
            val center = stateList.value.find {it.position == Positions.CENTER}!!.let {item ->
                if(item.boxNumber == BoxNumber.TWO){
                    item.copy(index = newValue)
                }else{
                    item
                }
            }
            val bottom = stateList.value.find {it.position == Positions.BOTTOM}!!.let {item ->
                if(item.boxNumber == BoxNumber.TWO){
                    item.copy(index = newValue)
                }else{
                    item
                }
            }
            stateList.tryEmit(listOf(top,center,bottom))

        }


    }
    //TODO: IMPLEMENT DELETION and have it reset everything
    // I should have a separate index instaed of 0. It should be 99 or something. Still makes it black but can't be added to
    var boxTwoDragState = DraggableState { delta ->
        val boxTwoIsDoubleSize = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize
        if(boxTwoIsDoubleSize){
            boxTwoDoubleSizeDragging(delta)
        }else{
            Log.d("boxSizeDoublingLogs","NOT DOUBLE")

            boxTwoSingleSizeDragging(delta)
        }


        boxTwoOffsetY += delta

    }
    fun boxTwoDoubleSizeDragging(delta: Float){
        Log.d("boxSizeDoublingLogs","DOUBLE")

        /********* ENTERING SECTION THREE (DELETING)************/
        if(boxTwoOffsetY >= (0.6*(700f*2))){
            Log.d("DeletingDRAGSTATE","DELETE")

            if(!deleteBoxTwo){
                deleteBoxTwo = true
            }
        }

        /********* ENTERING SECTION TWO(BOTTOM) ************/
        else if(boxTwoOffsetY >= (0.6*(700f)) && boxTwoOffsetY<=(0.6*(700f*2))){
            if(deleteBoxTwo){
                deleteBoxTwo = false
            }
            if(boxTwoSection!=Sections.TWO){
                Log.d("StateListUpdate","sectionTwoEnter")
                if(0<=delta) { //true means dragging down when entering section 2
                    val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.THREE)
                    val newCenter  =ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,true,true)
                    val newBottom = stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.ONE)

                    stateList.tryEmit(listOf(newTop,newCenter,newBottom))

                }
            }
            boxTwoSection = Sections.TWO

        }
        /**ENTERING SECTION ONE(TOP)*/
        else if(boxTwoOffsetY <= (0.6*(700f))){
            if(boxTwoSection != Sections.ONE){
                val newTop =ModArrayData(700f,2,Positions.TOP,BoxNumber.TWO,true,true)
                val newCenter =stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.ONE)
                val newBottom =stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.THREE)

                val newList =listOf(newTop,newCenter,newBottom)
                Log.d("StateListUpdate","sectionOneEnter ->$newList")
                stateList.tryEmit(newList)
            }
            boxTwoSection = Sections.ONE

        }
    }
    fun boxTwoSingleSizeDragging(
        delta:Float
    ){
        //todo:add the checks to determine if there is any doubles
        val boxOneIsDouble = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
        val boxThreeIsDouble = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
       if(boxOneIsDouble|| boxThreeIsDouble){
           //here we only need to check when its deleting and the top section and the bottom section
           /********* DELETING ************/
           if(boxTwoOffsetY >= 1550f){
               Log.d("SingleMovingWhenDouble","DELETE")
               if(!deleteBoxTwo){
                   deleteBoxTwo = true
               }

           }
           else if(boxTwoOffsetY >= (0.6*(700f*2))){
               Log.d("SingleMovingWhenDouble","BOTTOM")
               if(boxTwoSection !=Sections.THREE){
                   val newTop =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                   val newCenter =  stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                   val newBottom  =ModArrayData(700f,boxTwoIndex,Positions.BOTTOM,BoxNumber.TWO,true,false,)

                   val newList = listOf(newTop,newCenter,newBottom)
                   stateList.tryEmit(newList)

                   boxTwoSection = Sections.THREE
               }
           }
           /********* ENTERING SECTION ONE(TOP) ************/
            else if(boxTwoOffsetY <= (0.6*(700f))){
                Log.d("SingleMovingWhenDouble","TOP")
               if(boxTwoSection !=Sections.ONE){
                   val newTop  =ModArrayData(700f,2,Positions.TOP,BoxNumber.TWO,true,false,)
                   val newCenter=  stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                   val newBottom =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)

                   val newList = listOf(newTop,newCenter,newBottom)
                   stateList.tryEmit(newList)

                   boxTwoSection = Sections.ONE
               }


           }

       } //end of the is double check
       else  if(boxTwoOffsetY >=1550f){
            Log.d("DeletingDRAGSTATE","DELETE")

            if(!deleteBoxTwo){
                deleteBoxTwo = true
            }
        }
        /********* ENTERING SECTION THREE ************/
        else if(boxTwoOffsetY >= (0.6*(700f*2))){

            if(deleteBoxTwo){
                deleteBoxTwo = false
            }

            if(boxTwoSection !=Sections.THREE){
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val doubleSize = stateList.value.find{it.boxNumber == BoxNumber.TWO}!!.doubleSize
                val newBottom  =ModArrayData(700f,2,Positions.BOTTOM,BoxNumber.TWO,true,doubleSize,)

                val newList = listOf(top,newCenter,newBottom)
                stateList.tryEmit(newList)
                boxTwoSection = Sections.THREE

            }


        }
        /********* ENTERING SECTION TWO ************/
        else if(boxTwoOffsetY >= (0.6*(700f)) && boxTwoOffsetY<=(0.6*(700f*2))){
            if(boxTwoSection!=Sections.TWO){
                if(0>=delta) { //true means dragging up when entering section 2
                    Log.d("boxTwoDragStateLOGGING","SECTION 2 UP")
                    val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                    val doubleSize = stateList.value.find{it.boxNumber == BoxNumber.TWO}!!.doubleSize
                    val newCenter  =ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,true,doubleSize,)
                    val newBottom = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else { //means the user is dragging down when entering section 2
                    Log.d("boxTwoDragStateLOGGING","SECTION 2 DOWN")
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val doubleSize = stateList.value.find{it.boxNumber == BoxNumber.TWO}!!.doubleSize
                    val newCenter  =ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,true,doubleSize,)
                    val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)
                    val newList = listOf(newTop,newCenter,bottom)
                    stateList.tryEmit(newList)

                }
                boxTwoSection = Sections.TWO
                Log.d("boxTwoDragStateLOGGING","2")

            }

        }

        /********* ENTERING SECTION ONE ************/
        else if(boxTwoOffsetY <= (0.6*(700f))){
            if(boxTwoSection!=Sections.ONE){
                val newTop  =ModArrayData(700f,2,Positions.TOP,BoxNumber.TWO,true,false,)
                val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)
                boxTwoSection = Sections.ONE
                Log.d("boxTwoDragStateLOGGING","1")

            }

        }

    }

    /****************************************BOX THREE RELATED STATE**********************************************************/

    var boxThreeOffsetY by mutableStateOf(700f*2)
    var boxThreeSection by mutableStateOf(Sections.THREE)
    var boxThreeIndex by mutableStateOf(3)
    var deleteBoxThree by mutableStateOf(false)
    var boxThreeHeight by mutableStateOf((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
    fun setBoxThreeOffset(newValue:Float){
        boxThreeOffsetY = newValue
    }
    fun setBoxThreeDragging(newValue: Boolean){
        _boxesDragging.value = _boxesDragging.value.copy(
            boxThreeDragging = newValue
        )
    }

    fun syncBoxThreeIndex(newValue:Int){
        boxThreeIndex = newValue
        deleteBoxThree = false
        //todo: NOW I NEED TO ADD THE EXTRA 0 CHECKS
        if(newValue ==0) { //THis means that we are going to delete box two

            boxThreeHeight = (Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
            //todo: before this goes up I need an error UI that tells me I can not add things
            val doubleSize = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
            if (doubleSize) {
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                    .copy(doubleSize = false)
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                    .copy(doubleSize = false)
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                    .copy(doubleSize = false, index = 0)
                Log.d("LogThreeDelete","$boxOne , $boxTwo , $boxThree")

                //todo:
                val checkBoxOneIndex = boxOne.index
                val checkBoxTwoIndex = boxTwo.index
                if(checkBoxTwoIndex == 99){
                    syncBoxTwoIndex(0)
                }
                if(checkBoxOneIndex == 99){
                    syncBoxOneIndex(0)
                }
                stateList.tryEmit(listOf(boxOne, boxTwo, boxThree))

            } else {
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(doubleSize = false, index = 0)
                stateList.tryEmit(listOf(boxOne, boxTwo, boxThree))
            }
        }

            //ADD ABOVE HERE
        val top = stateList.value.find {it.position == Positions.TOP}!!.let {item ->
            if(item.boxNumber == BoxNumber.THREE){
                item.copy(index = newValue)
            }else{
                item
            }
        }
        val center = stateList.value.find {it.position == Positions.CENTER}!!.let {item ->
            if(item.boxNumber == BoxNumber.THREE){
                item.copy(index = newValue)
            }else{
                item
            }
        }
        val bottom = stateList.value.find {it.position == Positions.BOTTOM}!!.let {item ->
            if(item.boxNumber == BoxNumber.THREE){
                item.copy(index = newValue)
            }else{
                item
            }
        }
        stateList.tryEmit(listOf(top,center,bottom))
    }



    var boxThreeDragState = DraggableState { delta ->
        Log.d("boxThreeDragStateLogging","MOVING!!!!!!!!!!!!!!!!!!!!!!!!!!!")


        val boxThreeIsDoubleSize = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
        if(boxThreeIsDoubleSize){
            boxThreeDoubleSizeDragging(delta)

        }else{
            Log.d("boxThreeStateSizeDoublingLogs","NOT DOUBLE")
            boxThreeSingleSizeDragging(delta)
        }
        //below here should go into boxThreeSingleSizeDragging



        boxThreeOffsetY += delta

    }
    fun boxThreeDoubleSizeDragging(delta: Float){
        //todo: here it is moving BoxNumber.THREE  and BoxNumber.ONE
        //  todo: add the double check

            /********* ENTERING SECTION THREE (DELETING)*****************************************************/
            if(boxThreeOffsetY >= (0.6*(700f*2))){
                Log.d("boxThreeStateSizeDoublingLogs","DELETE")
                if(!deleteBoxThree){
                    deleteBoxThree = true
                }

            }
            /********* ENTERING SECTION TWO (BOTTOM) ************/
             //this means that the double section is entering the bottom
            //so I think the problem is that there is a sync problem
            else if(boxThreeOffsetY >= (0.6*(700f)) && boxThreeOffsetY<=(0.6*(700f*2))){
               // Log.d("boxThreeStateSizeDoublingLogs","BOTTOM")

                if(deleteBoxThree){
                    deleteBoxThree = false
                }
                if(boxThreeSection!=Sections.TWO){
                    Log.d("boxThreeStateSizeDoublingLogs","BOTTOM")
                    Log.d("boxThreeStateSizeDoublingLogs","boxOne index ->$boxOneIndex")
                    Log.d("boxThreeStateSizeDoublingLogs","boxOne section ->$boxOneSection")
                    Log.d("boxThreeStateSizeDoublingLogs","boxTwo index ->$boxTwoIndex")

                    if(0<=delta) { //true means dragging down when entering section 2(the bottom)
                        //

                        val boxTwo= stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                        val newCenter  =ModArrayData(700f,3,Positions.CENTER,BoxNumber.THREE,true,true)
                        //todo: this should not be bottom. I need to check to see if one or two is 99 and make it the center, the other becomes the
                        val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!


                        if(boxTwo.index == 99){
                            Log.d("boxThreeStateSizeDoublingLogs","boxTwo 99")
                            val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.ONE)
                            val centerAgain = newCenter
                            val newBottom =stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.TWO)
                            stateList.tryEmit(listOf(newTop,centerAgain,newBottom))
                        }
                        else if(boxOne.index ==99){
                            Log.d("boxThreeStateSizeDoublingLogs","boxONE 99")
                            val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.TWO)
                            val centerAgain = newCenter
                            val newBottom = stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.ONE)
                            stateList.tryEmit(listOf(newTop,centerAgain,newBottom))
                        }



                    }
                }
                boxThreeSection = Sections.TWO
            }
            /****DELETE BELOW*/



            /***********************************************ENTERING SECTION ONE(TOP)***************************************************************************************/
            else if(boxThreeOffsetY <= (0.6*(700f))){
                //todo: THE SAME THING AS ABOVE TO CHECK THE BOX VALUES OF 99

                if(boxThreeSection != Sections.ONE){
                    boxOneSection = Sections.THREE
                    val newTop =ModArrayData(700f,3,Positions.TOP,BoxNumber.THREE,true,true)
                    val newCenter =stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.ONE)
                    val newBottom =stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.TWO)

                    val newList =listOf(newTop,newCenter,newBottom)
                    Log.d("StateListUpdate","sectionOneEnter ->$newList")
                    stateList.tryEmit(newList)
                }
                boxThreeSection = Sections.ONE
            }



    }
    fun boxThreeSingleSizeDragging(
        delta: Float
    ){
        //todo: making the  double checks
        val boxOneIsDouble = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
        val boxTwoIsDouble = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize

        if(boxOneIsDouble|| boxTwoIsDouble){
            Log.d("boxThreeDoubleSizeDragging","One --> $boxOneIsDouble")
            Log.d("boxThreeDoubleSizeDragging","Two --> $boxTwoIsDouble")
            /********* DELETING ************/
            if(boxThreeOffsetY >= 1550f){
                Log.d("Box3SingleMovingWhenDouble","DELETE")
                if(!deleteBoxThree){
                    deleteBoxThree = true
                }
            }
            /********* ENTERING SECTION 3 (BOTTOM) ************/
            else if(boxThreeOffsetY >= (0.6*(700f*2))){
                if(boxThreeSection != Sections.THREE){
                    Log.d("Box3SingleMovingWhenDouble","BOTTOM")
                    val newTop =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter =  stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                    val newBottom  =ModArrayData(700f,boxThreeIndex,Positions.BOTTOM,BoxNumber.THREE,true,false,)

                    val newList = listOf(newTop,newCenter,newBottom)
                    stateList.tryEmit(newList)

                    boxThreeSection = Sections.THREE
                }


            }
            /********* ENTERING SECTION ONE(TOP) ************/
            else if(boxThreeOffsetY <= (0.6*(700f))){
                Log.d("Box3SingleMovingWhenDouble","TOP")
                if(boxThreeSection !=Sections.ONE){
                    val newTop  =ModArrayData(700f,boxThreeIndex,Positions.TOP,BoxNumber.THREE,true,false,)
                    val newCenter=  stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                    val newBottom =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)

                    val newList = listOf(newTop,newCenter,newBottom)
                    stateList.tryEmit(newList)

                    boxThreeSection = Sections.ONE
                }

                }


        } //end of the double checks
        else{
            if(boxThreeOffsetY >=1550f){
                Log.d("boxThreeDragStateLogging","DELETE")
                if(!deleteBoxThree){
                    deleteBoxThree = true
                }
            }
            /********* ENTERING SECTION THREE ************/
            else if(boxThreeOffsetY >= (0.6*(700f*2))){
                if(deleteBoxThree){
                    deleteBoxThree = false
                }
                if(boxThreeSection != Sections.THREE){
                    Log.d("boxThreeDragStateLogging","SECTION 3")
                    val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                    val newCenter = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                    val newBottom  =ModArrayData(700f,3,Positions.BOTTOM,BoxNumber.THREE,true,false,)

                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)
                    boxThreeSection = Sections.THREE
                }



            }

            /********* ENTERING SECTION TWO ************/
            else if(boxThreeOffsetY >= (0.6*(700f)) && boxThreeOffsetY<=(0.6*(700f*2))){
                if(boxThreeSection != Sections.TWO){
                    Log.d("boxThreeDragStateLogging","SECTION 2")
                    if(0>=delta) { //true means dragging up when entering section 2
                        Log.d("boxThreeDragStateLogging","SECTION 2 UP")
                        val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                        val newCenter  =ModArrayData(700f,3,Positions.CENTER,BoxNumber.THREE,true,false,)
                        val newBottom = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                        val newList = listOf(top,newCenter,newBottom)
                        stateList.tryEmit(newList)

                    }else { //means the user is dragging down when entering section 2
                        Log.d("boxThreeDragStateLogging","SECTION 2 DOWN")
                        val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                        val newCenter  =ModArrayData(700f,3,Positions.CENTER,BoxNumber.THREE,true,false,)
                        val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)
                        val newList = listOf(newTop,newCenter,bottom)
                        stateList.tryEmit(newList)

                    }

                    boxThreeSection = Sections.TWO
                }

            }

            /********* ENTERING SECTION ONE ************/
            else if(boxThreeOffsetY <= (0.6*(700f))){

                if(boxThreeSection != Sections.ONE){
                    Log.d("boxThreeDragStateLogging","SECTION 1")
                    val newTop  =ModArrayData(700f,3,Positions.TOP,BoxNumber.THREE,true,false,)
                    val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                    val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)

                    val newList = listOf(newTop,newCenter,bottom)
                    stateList.tryEmit(newList)
                    boxThreeSection = Sections.ONE

                }

            }
        }

    }

    init {
        viewModelScope.launch {
            stateList.collect{
                Log.d("StateListUpdate","stateList ->${stateList.value}")
                for(item in stateList.value){
                    checkBoxOneState(item)
                    checkBoxTwoState(item)
                    checkBoxThreeState(item)
                }

            }
        }
    }

    //This is called when a user clicks an item on the navigation side modal
    fun setIndex(newValue:Int){

        if(boxOneIndex ==0){
            //todo: the first thing we should do is to check for doubles and triples
            Log.d("stateListIndexLogging","boxOne should be 0 -> ${stateList.value}")
            //todo: I think There could be a lot of unnecessary state emmissions
            //also, not 100% sure if this is neccessary: boxOneIndex = newValue
//            boxOneIndex = newValue
            syncBoxOneIndex(newValue)// make sure the index stays synced with statelist
            checkForBoxOneDoubles(newValue)
        }
        else if(boxTwoIndex ==0){
            Log.d("stateListIndexLogging","boxTwo should be 0 -> ${stateList.value}")
//            boxTwoIndex = newValue
            syncBoxTwoIndex(newValue)// make sure the index stays synced with statelist
            checkForBoxTwoDoubles(newValue)
        }
        else if(boxThreeIndex == 0){
            Log.d("stateListIndexLogging","boxThree should be 0 -> ${stateList.value}")
//            boxThreeIndex = newValue
            syncBoxThreeIndex(newValue) // make sure the index stays synced with statelist
        }
        else {
            viewModelScope.launch {
                _showPlacementError.value = true
                delay(1000)
                _showPlacementError.value = false
            }

        }
    }

    // here is the height for a normal individual box:
    //Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
    //the value that is 0 should always be the one that gets placed at the top
    fun checkForBoxOneDoubles(newValue:Int){
        //todo: make this work
        //this function guarantees that boxOne is a index of 0
        if(boxTwoIndex == newValue){ // this definitely works
            // - when this conditional is run, we know 2 things 100%
            //1) boxOne index is 0
            //2) boxTwo index is equal to newValue
            Log.d("checkingForBoxOneDoublesAgain","boxTwoIndex == ${boxTwoIndex == newValue}")

            val top = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true)
            //todo: these two need to be set to a new index of 99
            val center = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.CENTER, index = 0)
            syncBoxOneIndex(99)
            boxTwoHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
            Log.d("loggindBoxTwoHeight","boxTwoHeight ->${boxTwoHeight.value}")
            val bottom = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.BOTTOM)
            Log.d("checkingForBoxOneDoublesAgain","list ->${listOf(top,center,bottom)}")

            stateList.tryEmit(listOf(top,center,bottom))
            //setBoxTwo to 0 and increse the height of BoxOne
            //get box two and one, set them as new Top and new center and set box two 0 and increase the height of boxOne to double

        }
        //todo: this is what I am working on
        else if(boxThreeIndex == newValue){
            //todo: MAKE THIS MORE GENERIC AND WORK WITH checkForBoxTwoDoubles() VERSION
            //todo: So I need to walk through what happens inside of this function
            //- When this conditional is run, we know 2 things 100%
            //1) boxOne is 0
            //2) boxThree index is equal to newValue
            Log.d("checkingForBoxOneDoublesAgain","boxThreeIndex == ${boxTwoIndex == newValue}")

            val top = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true)
            syncBoxOneIndex(99)
            boxThreeHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
            val center = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.CENTER)
            val bottom = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.BOTTOM)
            stateList.tryEmit(listOf(top,center,bottom))
            //get box Three and one, set them as new Top and new center and set box Three 0 and increase the height of boxOne to double
        }

    }


    fun checkForBoxTwoDoubles(newValue:Int){
        //todo: so we need to check one and 3
        //update: to keep things consistent, I need to make the boxOneIndex as the top item
        if(boxOneIndex == newValue){
            Log.d("checkForBoxTwoDoubles","boxOneIndex == newValue")
            //todo: adding the boxone double dragging stuff

            //when this conditional runs, we know two things
            //1) boxTwoIndex is 0
            //2) boxOneIndex == newValue
            //todo: this is the level that we need it as, [boxOne,boxTwo,boxThree]
            val top = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true)
            syncBoxTwoIndex(99)
            boxOneHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
            val center = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.CENTER, doubleSize = false)
            val bottom = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.BOTTOM, doubleSize = false)



            stateList.tryEmit(listOf(top,center,bottom))
            setBoxOneDragging(true)


        }
        //update: to keep things consistent, I need to make the boxThreeIndex as the top item
        else if(boxThreeIndex == newValue){
            Log.d("checkForBoxTwoDoubles","box3Index == newValue")
            //added with the green
            //when this conditional runs, we know two things
            //1) boxTwoIndex is 0
            //2) boxThreeIndex == newValue
            //todo: this is the level that we need [boxThree,boxTwo,boxOne]
            val top = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true)
            syncBoxTwoIndex(99)
            boxThreeHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
            val center = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.CENTER, doubleSize = false)
            val bottom = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.BOTTOM, doubleSize = false)
            boxOneSection = Sections.THREE
            Log.d("checkForBoxTwoDoubles","BoxOneIndex ->$boxOneIndex")
            Log.d("checkForBoxTwoDoubles","BoxOneSection->$boxOneSection")
            Log.d("checkForBoxTwoDoubles","actual BoxOne -> $bottom")
            stateList.tryEmit(listOf(top,center,bottom))
            setBoxThreeDragging(true)


        }


        }


    fun checkBoxOneState(item: ModArrayData){
        if(item.boxNumber == BoxNumber.ONE && item.position == Positions.TOP  && item.dragging == false){
            boxOneOffsetY = 0f
            boxOneSection = Sections.ONE
        }
        else if(item.boxNumber == BoxNumber.ONE && item.position == Positions.CENTER  && item.dragging == false){
            boxOneOffsetY = 700f
            boxOneSection = Sections.TWO
        }
        else if(item.boxNumber == BoxNumber.ONE && item.position == Positions.BOTTOM  && item.dragging == false){
            boxOneOffsetY = 700f *2
            boxOneSection = Sections.THREE
        }

    }
    fun checkBoxTwoState(item: ModArrayData){
        if(item.boxNumber == BoxNumber.TWO && item.position == Positions.TOP  && item.dragging == false){
            boxTwoOffsetY = 0f
            boxTwoSection = Sections.ONE
        }
        else if(item.boxNumber == BoxNumber.TWO && item.position == Positions.CENTER  && item.dragging == false){
            boxTwoOffsetY = 700f
            boxTwoSection = Sections.TWO
        }
        else if(item.boxNumber == BoxNumber.TWO && item.position == Positions.BOTTOM  && item.dragging == false){
            boxTwoOffsetY = 700f *2
            boxTwoSection = Sections.THREE
        }

    }

    fun checkBoxThreeState(item: ModArrayData){
        if(item.boxNumber == BoxNumber.THREE && item.position == Positions.TOP  && item.dragging == false){
            boxThreeOffsetY = 0f
            boxThreeSection = Sections.ONE
        }
        else if(item.boxNumber == BoxNumber.THREE && item.position == Positions.CENTER  && item.dragging == false){
            boxThreeOffsetY = 700f
            boxThreeSection = Sections.TWO
        }
        else if(item.boxNumber == BoxNumber.THREE && item.position == Positions.BOTTOM  && item.dragging == false){
            boxThreeOffsetY = 700f *2
            boxThreeSection = Sections.THREE
        }

    }


}

