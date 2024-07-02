package com.example.clicker.presentation.modChannels.modVersionThree

import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
)

@HiltViewModel
class ModVersionThreeViewModel @Inject constructor(): ViewModel(){

    //This stateList is what represents the states position relative to each other
    private val stateList = MutableStateFlow(listOf(
        ModArrayData(700f,1,Positions.TOP,BoxNumber.ONE,false,),
        ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,false,),
        ModArrayData(700f,3,Positions.BOTTOM,BoxNumber.THREE,false),
    )
    )
    private val _boxesDragging = mutableStateOf(BoxDragging(false,false,false))
    val boxesDragging: State<BoxDragging> = _boxesDragging





    /****************************************BOX ONE RELATED STATE*********************************************************/
    var boxOneOffsetY by mutableStateOf(0f)
    var boxOneSection by mutableStateOf(Sections.ONE)
    var boxOneIndex by mutableStateOf(1)
    fun setBoxOneOffset(newValue:Float){
        boxOneOffsetY = newValue
    }
    fun setBoxOneDragging(newValue: Boolean){
        _boxesDragging.value = _boxesDragging.value.copy(
            boxOneDragging = newValue
        )
    }

    var boxOneDragState = DraggableState { delta ->
        Log.d("LOGGINGTHEDELTACHANGE","DELTA -->${0>=delta} $delta") //true when dragging up
        Log.d("LOGGINGTHEDELTACHANGE","offset -->$boxOneOffsetY")

        /********* ENTERING SECTION THREE ************/
        if(boxOneOffsetY >= (0.6*(700f*2))){

            if(boxOneSection != Sections.THREE){
                Log.d("BoxOneStatechanging","THREE")
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter =stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val newBottom =ModArrayData(700f,1,Positions.BOTTOM,BoxNumber.ONE,true,)
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
                    val newCenter =ModArrayData(700f,1,Positions.CENTER,BoxNumber.ONE,true,)
                    val newBottom =stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else{ //means the user is dragging down when entering section 2
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter =ModArrayData(700f,1,Positions.CENTER,BoxNumber.ONE,true,)
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
                val newTop =ModArrayData(700f,1,Positions.TOP,BoxNumber.ONE,true,)
                val bottom = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)

                boxOneSection = Sections.ONE
            }

        }


        boxOneOffsetY += delta
    }




    /****************************************BOX TWO RELATED STATE**********************************************************/
    var boxTwoOffsetY by mutableStateOf(700f)
    var boxTwoSection by mutableStateOf(Sections.TWO)
    var boxTwoIndex by mutableStateOf(2)
    fun setBoxTwoOffset(newValue:Float){
        boxTwoOffsetY = newValue
    }
    fun setBoxTwoDragging(newValue: Boolean){
        _boxesDragging.value = _boxesDragging.value.copy(
            boxTwoDragging = newValue
        )
    }
    var boxTwoDragState = DraggableState { delta ->
        /********* ENTERING SECTION THREE ************/
        if(boxTwoOffsetY >= (0.6*(700f*2))){

            if(boxTwoSection !=Sections.THREE){
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val newBottom  =ModArrayData(700f,2,Positions.BOTTOM,BoxNumber.TWO,true,)

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
                    val newCenter  =ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,true,)
                    val newBottom = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else { //means the user is dragging down when entering section 2
                    Log.d("boxTwoDragStateLOGGING","SECTION 2 DOWN")
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter  =ModArrayData(700f,2,Positions.CENTER,BoxNumber.TWO,true,)
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
                val newTop  =ModArrayData(700f,2,Positions.TOP,BoxNumber.TWO,true,)
                val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)
                boxTwoSection = Sections.ONE
                Log.d("boxTwoDragStateLOGGING","1")

            }

        }
        boxTwoOffsetY += delta

    }

    /****************************************BOX THREE RELATED STATE**********************************************************/

    var boxThreeOffsetY by mutableStateOf(700f*2)
    var boxThreeSection by mutableStateOf(Sections.THREE)
    var boxThreeIndex by mutableStateOf(3)
    fun setBoxThreeOffset(newValue:Float){
        boxThreeOffsetY = newValue
    }
    fun setBoxThreeDragging(newValue: Boolean){
        _boxesDragging.value = _boxesDragging.value.copy(
            boxThreeDragging = newValue
        )
    }
    var boxThreeDragState = DraggableState { delta ->

        /********* ENTERING SECTION THREE ************/
        if(boxThreeOffsetY >= (0.6*(700f*2))){
            if(boxThreeSection != Sections.THREE){
                Log.d("boxThreeDragStateLogging","SECTION 3")
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val newBottom  =ModArrayData(700f,3,Positions.BOTTOM,BoxNumber.THREE,true,)

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
                    val newCenter  =ModArrayData(700f,3,Positions.CENTER,BoxNumber.THREE,true,)
                    val newBottom = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else { //means the user is dragging down when entering section 2
                    Log.d("boxThreeDragStateLogging","SECTION 2 DOWN")
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter  =ModArrayData(700f,3,Positions.CENTER,BoxNumber.THREE,true,)
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
                val newTop  =ModArrayData(700f,3,Positions.TOP,BoxNumber.THREE,true,)
                val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)
                boxThreeSection = Sections.ONE

            }

        }


        boxThreeOffsetY += delta

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

