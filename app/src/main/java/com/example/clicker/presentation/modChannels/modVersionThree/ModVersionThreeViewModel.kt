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
        ModArrayData(700F,1,Positions.TOP,BoxNumber.ONE,false,),
        ModArrayData(700F,2,Positions.CENTER,BoxNumber.TWO,false,),
        ModArrayData(700F,3,Positions.BOTTOM,BoxNumber.THREE,false),
    )
    )
    private val _boxesDragging = mutableStateOf(BoxDragging(false,false,false))
    val boxesDragging: State<BoxDragging> = _boxesDragging




    /****************************************BOX ONE RELATED STATE**************************************/
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
        Log.d("LOGGINGTHEDELTACHANGE","DELTA -->${0>=delta} $delta")

        boxOneOffsetY += delta
    }




    /****************************************BOX TWO RELATED STATE**************************************/
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
        boxTwoOffsetY += delta

    }

    /****************************************BOX THREE RELATED STATE**************************************/

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
        boxThreeOffsetY += delta

    }

    init {
        viewModelScope.launch {
            stateList.collect{
                for(item in stateList.value){

                }

            }
        }
    }

}

