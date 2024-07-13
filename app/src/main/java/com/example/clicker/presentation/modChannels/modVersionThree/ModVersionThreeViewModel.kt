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
import androidx.compose.ui.unit.Dp
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

//todo: set up the data class called double tap. make it work. Then change its name to BoxDragging and
//switch the original BoxDragging data class to z-index themed name
data class DoubleTap(
    val boxOneDoubleTap: Boolean,
    val boxTwoDoubleTap: Boolean,
    val boxThreeDoubleTap: Boolean,
)




//ok, so we have an array of this and when one drag state crosses a threshold, a value will be emitted with this data
// it will be a hot flow and we move the boxes based on this data
data class ModArrayData(
    val height:Dp,
    val index:Int,
    val position:Positions, // this would be top center or bottom
    val boxNumber: BoxNumber,
    val dragging:Boolean,
    val doubleSize:Boolean,
    val tripleSize:Boolean
)

@HiltViewModel
class ModVersionThreeViewModel @Inject constructor(): ViewModel(){

    //This stateList is what represents the states position relative to each other
    private val stateList = MutableStateFlow(listOf(
        ModArrayData((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp,1,Positions.TOP,BoxNumber.ONE,false,false,false),
        ModArrayData((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp,2,Positions.CENTER,BoxNumber.TWO,false,false,false),
        ModArrayData((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp,3,Positions.BOTTOM,BoxNumber.THREE,false,false,false),
    )
    )


    //Todo: this should get changed over to z-index
    private val _boxesDragging = mutableStateOf(BoxDragging(false,false,false))
    val boxesDragging: State<BoxDragging> = _boxesDragging

    //todo: this should get changed to the boxesDragging
    private val _doubleTap = mutableStateOf(DoubleTap(false,false,false))
    val doubleTap: State<DoubleTap> = _doubleTap

    private val _showPlacementError = mutableStateOf(false)
    val showPlacementError: State<Boolean> = _showPlacementError

    val section2height =(Resources.getSystem().displayMetrics.heightPixels / 3.17).toFloat()
    val section3Height =((Resources.getSystem().displayMetrics.heightPixels / 3.17)*2).toFloat()
    val deleteOffset = section3Height +100

    private val _fullChat = mutableStateOf(false)
    val fullChat:State<Boolean> = _fullChat




    init{

        val height =((Resources.getSystem().displayMetrics.heightPixels / 8.4)).dp // height for the indiv boxes. 264.dp
        val nonDPHeight =((Resources.getSystem().displayMetrics.heightPixels / 3.17))  // animation height. When dragging stops. 700.0
        Log.d("TheIndivHeight","height of indiv box DP--> $height")
        Log.d("TheIndivHeight","height of indiv box NON-DP--> $nonDPHeight")
        Log.d("TheIndivHeight","section2height--> $section2height")
        Log.d("TheIndivHeight","section3Height--> $section3Height")
    }

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
        Log.d("TheBoxDraggingset","ONE")
        val boxOneDouble = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
        if(newValue || boxOneDouble){
            _boxesDragging.value = _boxesDragging.value.copy(
                boxOneDragging = true,
                boxTwoDragging = false,
                boxThreeDragging = false,
            )
        }
        else{
            _boxesDragging.value = _boxesDragging.value.copy(
                boxOneDragging = newValue,
            )
        }

    }
    fun setBoxOneDoubleTap(newValue:Boolean){
        _doubleTap.value = _doubleTap.value.copy(
            boxOneDoubleTap = newValue
        )

    }


    /**
     * */
    fun syncBoxOneIndex(newValue:Int){// called to make sure the index stays synced with statelist
       //I was wrong, we do need to worry about [top,center,bottom]
        Log.d("SyncingINdex","ONE && index -> $newValue")
        boxOneIndex = newValue
        deleteBoxOne = false

        if(newValue ==0){ //THis means that we are going to delete box two


            boxOneHeight =(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
            //todo: before this goes up I need an error UI that tells me I can not add things
            val doubleSize =stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
            val tripleSize =stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.tripleSize
            if(doubleSize){
                Log.d("syncBoxOneIndexChecking","Double")
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(doubleSize = false, index = newValue, height = (Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                //this is for boxOneIndex
                val checkBoxTwoIndex = boxTwo.index
                val checkBoxThreeIndex = boxThree.index
                if(checkBoxTwoIndex == 99){
                    Log.d("SyncingINdexBoxOne","boxTwoDouble")
                    //todo: I NEED TO IMPLEMENT MORE checks for if
                    boxTwoIndex =0
                    val newBoxOne = boxOne
                    val newBoxTwo = boxTwo.copy(index = 0)
                    val newBoxThree = boxThree
                    stateList.tryEmit(listOf(newBoxOne,newBoxTwo,newBoxThree))

                }
                else if(checkBoxThreeIndex == 99){
                    Log.d("SyncingINdexBoxOne","boxThreeDouble")
                    boxThreeIndex =0
                    val newBoxOne = boxOne
                    val newBoxTwo = boxTwo
                    val newBoxThree = boxThree.copy(index = 0)
                    stateList.tryEmit(listOf(newBoxOne,newBoxTwo,newBoxThree))
                }
                else{
                    stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
                }
                _fullChat.value = false

            }
            else if(tripleSize){
                Log.d("syncBoxOneIndexChecking","triple")
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(tripleSize = false,index = 0, height = (Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                boxOneIndex = 0
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy( index = 0,height=(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                boxTwoIndex = 0
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(index = 0,height=(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                boxThreeIndex = 0
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
                _fullChat.value = false
            }
            else{
                Log.d("syncBoxOneIndexChecking","NonDouble")
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(doubleSize = false, index = newValue,height=(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
            }


        }
       else{
            val top = stateList.value.find {it.boxNumber == BoxNumber.ONE}!!.copy(index = newValue)
            val center = stateList.value.find {it.boxNumber == BoxNumber.TWO}!!
            val bottom = stateList.value.find {it.boxNumber == BoxNumber.THREE}!!

            stateList.tryEmit(listOf(top,center,bottom))
        }



    }

    var boxOneDragState = DraggableState { delta ->
        if(_boxesDragging.value.boxOneDragging){ //ensures that the user has to double tap before dragging
            if(boxOneIndex != 99 && boxOneIndex !=0){
                val boxOneIsDoubleSize = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize
                val tripleSize =stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.tripleSize

                if(tripleSize){
                    //todo:Adding the special dragging
                    boxOneTripleSizeDragging()
                }
                else if(boxOneIsDoubleSize){
                    Log.d("BOXoNEDOUBLEDRAGGING","DOUBLEDRAG!!!!")
                    boxOneDoubleSizeDragging(delta)

                }else{
                    boxOneSingleSizeDragging(delta)
                }


                boxOneOffsetY += delta
            }
        }


    }

    fun boxOneTripleSizeDragging(){

        if(boxOneOffsetY >= (0.6*section2height) ){
            Log.d("boxTwoTripleSizeDragging","DELETING")
            if(!deleteBoxOne){
                deleteBoxOne = true
            }
        }else{
            Log.d("boxTwoTripleSizeDragging"," not DELETING")
            if(deleteBoxOne){
                deleteBoxOne = false
            }
        }
    }

    fun boxOneDoubleSizeDragging(
        delta: Float
    ){


        /********* ENTERING SECTION THREE (DELETING)************/
        if(boxOneOffsetY >= (0.6*(section3Height))){
            Log.d("boxSizeDoublingLogs","DELETE")
            if(!deleteBoxOne){
                deleteBoxOne = true
            }

            //todo: add the deletion and fix when the box stops its index is set behind the black box

        }

        /********* ENTERING SECTION TWO(BOTTOM) ************/
        else if(boxOneOffsetY >= (0.6*(section2height)) && boxOneOffsetY<=(0.6*(section3Height))){
            Log.d("boxSizeDoublingLogs","BOTTOM")
            //todo: check the delta
            if(deleteBoxOne){
                deleteBoxOne = false
            }
            if(boxOneSection!=Sections.TWO){

                if(0<=delta) { //true means dragging down when entering section 2
                    Log.d("BOXONEDOUBLEDRAGESTATE","sectionTwoEnter")
                    //todo: we need to do the index 99 checks for Tow and Three
                    val boxTwo= stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                    val newCenter  =ModArrayData(700f.dp,boxOneIndex,Positions.CENTER,BoxNumber.ONE,true,true,false)
                    //todo: this should not be bottom. I need to check to see if one or two is 99 and make it the center, the other becomes the
                    val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!

                    if(boxTwo.index == 99){
                        Log.d("BOXONEDOUBLEDRAGESTATE","2->99")

                        val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.THREE)
                        val centerAgain = newCenter
                        val newBottom =stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.TWO)
                        stateList.tryEmit(listOf(newTop,centerAgain,newBottom))

                    }else if(boxThree.index == 99){
                        Log.d("BOXONEDOUBLEDRAGESTATE","3 ->99")
                        Log.d("BOXONEDOUBLEDRAGESTATE","beforeUpdate ->${stateList.value}")
                        val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.TWO)
                        val centerAgain = newCenter
                        val newBottom =stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.THREE)
                        stateList.tryEmit(listOf(newTop,centerAgain,newBottom))
                        Log.d("BOXONEDOUBLEDRAGESTATE","AFTERUpdate ->${stateList.value}")
                    }



                }
            }
            boxOneSection = Sections.TWO

        }
        /**ENTERING SECTION ONE(TOP)*/
        // todo: the doubles index checks like what is done above.
        else if(boxOneOffsetY <= (0.6*(section2height))){
            Log.d("boxSizeDoublingLogs","TOP")
            if(boxOneSection != Sections.ONE){
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!

                val newTop =ModArrayData(700f.dp,boxOneIndex,Positions.TOP,BoxNumber.ONE,true,true,false)
                if(boxTwo.index == 99){
                    val newCenter = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.TWO)
                    val newBottom = stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.THREE)
                    stateList.tryEmit(listOf(newTop,newCenter,newBottom))
                }
                else if (boxThree.index == 99){
                    val newCenter = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.THREE)
                    val newBottom = stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.TWO)
                    stateList.tryEmit(listOf(newTop,newCenter,newBottom))
                }


            }
            boxOneSection = Sections.ONE



        }
    }

    fun boxOneSingleSizeDragging(
        delta:Float
    ){
        if(boxOneOffsetY >=( deleteOffset)){
            Log.d("DeletingDRAGSTATE","DELETE")

            if(!deleteBoxOne){
                deleteBoxOne = true
            }
        }

        /********* ENTERING SECTION THREE ************/
        else if(boxOneOffsetY >= (0.6*(section3Height))){
            if(deleteBoxOne){
                deleteBoxOne = false
            }

            if(boxOneSection != Sections.THREE){
                Log.d("BoxOneStatechanging","THREE")
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter =stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val newBottom =ModArrayData(700f.dp,1,Positions.BOTTOM,BoxNumber.ONE,true,false,false)
                val newList = listOf(top,newCenter,newBottom)
                stateList.tryEmit(newList)
                boxOneSection = Sections.THREE
            }


        }
        /********* ENTERING SECTION TWO ************/
        else if(boxOneOffsetY >= (0.6*(section2height)) && boxOneOffsetY<=(0.6*(section3Height))){
            Log.d("INSECTIONTWOLOGS","SECTION 2")


            if(boxOneSection != Sections.TWO){
                if(0>=delta){ //true means dragging up when entering section 2
                    val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                    val newCenter =ModArrayData(700f.dp,1,Positions.CENTER,BoxNumber.ONE,true,false,false)
                    val newBottom =stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else{ //means the user is dragging down when entering section 2
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter =ModArrayData(700f.dp,1,Positions.CENTER,BoxNumber.ONE,true,false,false)
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
        else if(boxOneOffsetY <= (0.6*(section2height))){

            if(boxOneSection != Sections.ONE){
                Log.d("BoxOneStatechanging","ONE")
                val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                val newTop =ModArrayData(700f.dp,1,Positions.TOP,BoxNumber.ONE,true,false,false)
                val bottom = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)

                boxOneSection = Sections.ONE
            }

        }
    }

    fun boxOneTripleSync(){
        //when this function runs we know this. Box One is in double mode
        Log.d("boxOneTripleSync","triple sync happened")

        //todo: we need to do 5 things:
        //1) increase the boxOneHeight
        boxOneHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*3).dp
        //2) set boxOne double to false and boxOne triple to true
        val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(doubleSize = false, tripleSize = true,height=((Resources.getSystem().displayMetrics.heightPixels / 8.4)*3).dp)
        //3) set the boxTwo and the boxThree index to 99
        val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(index = 99)
        boxTwoIndex =99
        val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(index = 99)
        boxThreeIndex =99



        //4 emit the new state to stateList
        stateList.tryEmit(listOf(boxTwo,boxOne,boxThree))
        //5) do special movement on boxOneOffset for when boxOneTriple is set to true
        //todo:
        //I think I need to update the section to section one and he offset to 0f
        boxOneSection = Sections.ONE
        setBoxOneOffset(0f)

    }



    /****************************************BOX TWO RELATED STATE**********************************************************/
    var boxTwoOffsetY by mutableStateOf(section2height)
    var boxTwoSection by mutableStateOf(Sections.TWO)
    var boxTwoIndex by mutableStateOf(2)
    var deleteBoxTwo by mutableStateOf(false)
    var boxTwoHeight by mutableStateOf((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
    fun setBoxTwoOffset(newValue:Float){
        boxTwoOffsetY = newValue
    }
    fun setBoxTwoDragging(newValue: Boolean){
        Log.d("TheBoxDraggingset","TWO")

        val boxTwoIsInDoubleState = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize
        if(boxTwoIsInDoubleState || newValue){
            _boxesDragging.value = _boxesDragging.value.copy(
                boxOneDragging = false,
                boxTwoDragging = true,
                boxThreeDragging = false,
            )

        }else{
            _boxesDragging.value = _boxesDragging.value.copy(
                boxTwoDragging = newValue,
            )
        }
    }
    fun setBoxTwoDoubleTap(newValue:Boolean){
        _doubleTap.value = _doubleTap.value.copy(
            boxTwoDoubleTap = newValue
        )

    }

    //todo: OK I THINK THIS IS FINALIZED NOW AND IT WORKS

    fun syncBoxTwoIndex(newValue:Int){
        Log.d("SyncingINdex","TWO && index -> $newValue")
        boxTwoIndex = newValue
        deleteBoxTwo = false
        if(newValue ==0){ //THis means that we are going to delete box two


            boxTwoHeight =(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
            //todo: before this goes up I need an error UI that tells me I can not add things
            val doubleSize =stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize
            val tripleSize =stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.tripleSize
            if(doubleSize){
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(doubleSize = false, index = newValue,height=(Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!

                val checkBoxOneIndex = boxOne.index
                val checkBoxThreeIndex = boxThree.index
                if(checkBoxThreeIndex == 99){
                    Log.d("syncBoxTwoIndexLogs","checkBoxThreeIndex == 99")

                    boxThreeIndex =0
                   val newBoxThree = boxThree.copy(index = 0)
                    stateList.tryEmit(listOf(boxOne,boxTwo,newBoxThree))
                    setBoxOneDragging(true)

                }
                if(checkBoxOneIndex == 99){
                    Log.d("syncBoxTwoIndexLogs","checkBoxOneIndex == 99")

                    boxOneIndex =0
                    val newBoxOne = boxOne.copy(index = 0)
                    stateList.tryEmit(listOf(newBoxOne,boxTwo,boxThree))
                    setBoxThreeDragging(true)
                }
                _fullChat.value = false

            }
            else if(tripleSize){
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(index = 0)
                boxOneIndex = 0
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(tripleSize = false, index = 0, height = (Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
                boxTwoIndex = 0
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(index = 0)
                boxThreeIndex = 0
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
                _fullChat.value = false
            }
            else{
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(doubleSize = false, index = newValue)
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
            }


        }//end if boxOne is 0.
        else{
            //this code is operating under the idea of if the
            val top = stateList.value.find {it.boxNumber == BoxNumber.ONE}!!
            val center = stateList.value.find {it.boxNumber == BoxNumber.TWO}!!.copy(index = newValue)
            val bottom = stateList.value.find {it.boxNumber == BoxNumber.THREE}!!

            stateList.tryEmit(listOf(top,center,bottom))

        }


    }
    //TODO: IMPLEMENT DELETION and have it reset everything
    // I should have a separate index instaed of 0. It should be 99 or something. Still makes it black but can't be added to
    var boxTwoDragState = DraggableState { delta ->
//        if(_boxesDragging.value.boxTwoDragging){
            if(boxTwoIndex != 99 && boxTwoIndex != 0){
                val boxTwoIsDoubleSize = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize
                val boxTwoIsTripleSize = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.tripleSize
                if(boxTwoIsTripleSize){
                    Log.d("BoxTwoDragging","TRIPLE")
                    boxTwoTripleSizeDragging()
                }
                else if(boxTwoIsDoubleSize){
                    Log.d("BoxTwoDragging","DOUBLE")
                    boxTwoDoubleSizeDragging(delta)
                }else{
                    Log.d("BoxTwoDragging","SINGLE")

                    boxTwoSingleSizeDragging(delta)
                }


                boxTwoOffsetY += delta
            }
//        }



    }
    fun boxTwoTripleSizeDragging(){

        if(boxTwoOffsetY >= (0.6*section2height) ){
            Log.d("boxTwoTripleSizeDragging","DELETING")
            if(!deleteBoxTwo){
                deleteBoxTwo = true
            }
        }else{
            Log.d("boxTwoTripleSizeDragging"," not DELETING")
            if(deleteBoxTwo){
                deleteBoxTwo = false
            }
        }
    }
    fun boxTwoDoubleSizeDragging(delta: Float){
        Log.d("boxSizeDoublingLogs","DOUBLE")

        /********* ENTERING SECTION THREE (DELETING)************/
        if(boxTwoOffsetY >= (0.6*(section3Height))){
            Log.d("DeletingDRAGSTATE","DELETE")

            if(!deleteBoxTwo){
                deleteBoxTwo = true
            }
        }

        /********* ENTERING SECTION TWO(BOTTOM) ************/
        else if(boxTwoOffsetY >= (0.6*(section2height)) && boxTwoOffsetY<=(0.6*(section3Height))){
            if(deleteBoxTwo){
                deleteBoxTwo = false
            }
            if(boxTwoSection!=Sections.TWO){
                Log.d("StateListUpdate","sectionTwoEnter")
                if(0<=delta) { //true means dragging down when entering section 2
                    val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                    val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                    val newCenter  =ModArrayData(700f.dp,boxTwoIndex,Positions.CENTER,BoxNumber.TWO,true,true,false)
                    if(boxThree.index == 99){
                        Log.d("BOXTWODRAGGINGBOTTOM","Three.index == 99")
                        val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.ONE)
                        val centerAgain = newCenter
                        val newBottom = stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.THREE)

                        stateList.tryEmit(listOf(newTop,centerAgain,newBottom))
                    }else if(boxOne.index ==99){
                        Log.d("BOXTWODRAGGINGBOTTOM","One.index == 99")
                        val newTop = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.TOP, boxNumber = BoxNumber.THREE)
                        val centerAgain = newCenter
                        val newBottom = stateList.value.find { it.position ==Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.ONE)
                        stateList.tryEmit(listOf(newTop,centerAgain,newBottom))
                    }
                    //todo: I guess we have to implement a version of the non-99 values


                }
            }
            boxTwoSection = Sections.TWO

        }
        /**ENTERING SECTION ONE(TOP)*/
        else if(boxTwoOffsetY <= (0.6*(section2height))){
            if(boxTwoSection != Sections.ONE){

                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val newTop =ModArrayData(700f.dp,boxTwoIndex,Positions.TOP,BoxNumber.TWO,true,true,false)

                if(boxThree.index == 99){
                    val newCenter = stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.THREE)
                    val newBottom = stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.ONE)

                    stateList.tryEmit(listOf(newTop,newCenter,newBottom))
                }
                else if(boxOne.index ==99){
                    val newCenter =stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.ONE)
                    val newBottom =stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.THREE)

                    stateList.tryEmit(listOf(newTop,newCenter,newBottom))
                }

            }
            boxTwoSection = Sections.ONE

        }
    }

    fun boxTwoSingleSizeDragging(
        delta:Float
    ){
        //todo:add the checks to determine if there is any doubles
        val boxOneIsDouble = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize //this is causing a crash
        val boxThreeIsDouble = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
       if(boxOneIsDouble|| boxThreeIsDouble){
           //here we only need to check when its deleting and the top section and the bottom section
           /********* DELETING ************/
           if(boxTwoOffsetY >= (deleteOffset)){
               Log.d("SingleMovingWhenDouble","DELETE")
               if(!deleteBoxTwo){
                   deleteBoxTwo = true
               }

           }
           else if(boxTwoOffsetY >= (0.6*(section3Height))){
               Log.d("SingleMovingWhenDouble","BOTTOM")
               if(deleteBoxTwo){
                   deleteBoxTwo = false
               }
               if(boxTwoSection !=Sections.THREE){
                   val newTop =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                   val newCenter =  stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                   val newBottom  =ModArrayData(700f.dp,boxTwoIndex,Positions.BOTTOM,BoxNumber.TWO,true,false,false)

                   val newList = listOf(newTop,newCenter,newBottom)
                   stateList.tryEmit(newList)

                   boxTwoSection = Sections.THREE
               }
           }
           /********* ENTERING SECTION ONE(TOP) ************/
            else if(boxTwoOffsetY <= (0.6*(section2height))){
                Log.d("SingleMovingWhenDouble","TOP")
               if(boxTwoSection !=Sections.ONE){
                   val newTop  =ModArrayData(700f.dp,boxTwoIndex,Positions.TOP,BoxNumber.TWO,true,false,false)
                   val newCenter=  stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                   val newBottom =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)

                   val newList = listOf(newTop,newCenter,newBottom)
                   stateList.tryEmit(newList)

                   boxTwoSection = Sections.ONE
               }


           }

       } //end of the is double check
       else  if(boxTwoOffsetY >=(deleteOffset)){
            Log.d("DeletingDRAGSTATE","DELETE")

            if(!deleteBoxTwo){
                deleteBoxTwo = true
            }
        }
        /********* ENTERING SECTION THREE ************/
        else if(boxTwoOffsetY >= (0.6*(section3Height))){

            if(deleteBoxTwo){
                deleteBoxTwo = false
            }

            if(boxTwoSection !=Sections.THREE){
                val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                val newCenter = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                val doubleSize = stateList.value.find{it.boxNumber == BoxNumber.TWO}!!.doubleSize
                val newBottom  =ModArrayData(700f.dp,boxTwoIndex,Positions.BOTTOM,BoxNumber.TWO,true,doubleSize,false)

                val newList = listOf(top,newCenter,newBottom)
                stateList.tryEmit(newList)
                boxTwoSection = Sections.THREE

            }


        }
        /********* ENTERING SECTION TWO ************/
        else if(boxTwoOffsetY >= (0.6*(section2height)) && boxTwoOffsetY<=(0.6*(section3Height))){
            if(boxTwoSection!=Sections.TWO){
                if(0>=delta) { //true means dragging up when entering section 2
                    Log.d("boxTwoDragStateLOGGING","SECTION 2 UP")
                    val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                    val doubleSize = stateList.value.find{it.boxNumber == BoxNumber.TWO}!!.doubleSize
                    val newCenter  =ModArrayData(700f.dp,boxTwoIndex,Positions.CENTER,BoxNumber.TWO,true,doubleSize,false)
                    val newBottom = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)

                }else { //means the user is dragging down when entering section 2
                    Log.d("boxTwoDragStateLOGGING","SECTION 2 DOWN")
                    val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val doubleSize = stateList.value.find{it.boxNumber == BoxNumber.TWO}!!.doubleSize
                    val newCenter  =ModArrayData(700f.dp,boxTwoIndex,Positions.CENTER,BoxNumber.TWO,true,doubleSize,false)
                    val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)
                    val newList = listOf(newTop,newCenter,bottom)
                    stateList.tryEmit(newList)

                }
                boxTwoSection = Sections.TWO
                Log.d("boxTwoDragStateLOGGING","2")

            }

        }

        /********* ENTERING SECTION ONE ************/
        else if(boxTwoOffsetY <= (0.6*(section2height))){
            if(boxTwoSection!=Sections.ONE){
                val newTop  =ModArrayData(700f.dp,boxTwoIndex,Positions.TOP,BoxNumber.TWO,true,false,false)
                val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)

                val newList = listOf(newTop,newCenter,bottom)
                stateList.tryEmit(newList)
                boxTwoSection = Sections.ONE
                Log.d("boxTwoDragStateLOGGING","1")

            }

        }

    }
    fun boxTwoTripleSync(){
        // when this function runs we know this. Box two is in double mode

        //todo: we need to do 5 things:
        // 1) increase the boxTwoheight
        boxTwoHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*3).dp
        // 2) set boxTwo double to false and boxTwo triple to true
        val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(doubleSize = false, tripleSize = true, height = ((Resources.getSystem().displayMetrics.heightPixels / 8.4)*3).dp)


        // 3) set the boxOne index and the boxTwo index to 99
        val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(index = 99)
        boxOneIndex =99
        val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(index = 99)
        boxThreeIndex = 99

        // 4) emit the new state to stateList
        stateList.tryEmit(listOf(boxTwo,boxOne,boxThree))

        // 5) do special movement on boxTwoOffset for when boxTwoTriple is set to true

    }


    /****************************************BOX THREE RELATED STATE**********************************************************/

    var boxThreeOffsetY by mutableStateOf(section3Height)
    var boxThreeSection by mutableStateOf(Sections.THREE)
    var boxThreeIndex by mutableStateOf(3)
    var deleteBoxThree by mutableStateOf(false)
    var boxThreeHeight by mutableStateOf((Resources.getSystem().displayMetrics.heightPixels / 8.4).dp)
    fun setBoxThreeOffset(newValue:Float){
        boxThreeOffsetY = newValue
    }
    fun setBoxThreeDragging(newValue: Boolean){
        Log.d("TheBoxDraggingset","THREE")
        val boxThreeIsInDoubleState = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
        if(boxThreeIsInDoubleState || newValue){
            _boxesDragging.value = _boxesDragging.value.copy(
                boxOneDragging = false,
                boxTwoDragging = false,
                boxThreeDragging = true,
            )

        }else{
            _boxesDragging.value = _boxesDragging.value.copy(
                boxTwoDragging = newValue,
            )
        }

    }
    fun setBoxThreeDoubleTap(newValue:Boolean){
        _doubleTap.value = _doubleTap.value.copy(
            boxThreeDoubleTap = newValue
        )

    }

    fun syncBoxThreeIndex(newValue:Int){
        Log.d("SyncingINdex","THREE && index -> $newValue")


        boxThreeIndex = newValue
        deleteBoxThree = false
        //todo: NOW I NEED TO ADD THE EXTRA 0 CHECKS
        if(newValue ==0) { //THis means that we are going to delete box two


            boxThreeHeight = (Resources.getSystem().displayMetrics.heightPixels / 8.4).dp
            //todo: before this goes up I need an error UI that tells me I can not add things
            val doubleSize = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
            val tripleSize =stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.tripleSize
            if (doubleSize) {
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(doubleSize = false)
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(doubleSize = false)
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(doubleSize = false, index = newValue)


                //todo:
                val checkBoxOneIndex = boxOne.index
                val checkBoxTwoIndex = boxTwo.index
                if(checkBoxTwoIndex == 99){
                    Log.d("LogThreeDelete","2 --> 99")
                    boxTwoIndex = 0
                    val newBoxOne = boxOne
                    val newBoxTwo = boxTwo.copy(index = 0)
                    val newBoxThree = boxThree
                    stateList.tryEmit(listOf(newBoxOne, newBoxTwo, newBoxThree))

                }
                else if(checkBoxOneIndex == 99){
                    Log.d("LogThreeDelete","1 --> 99")
                    boxOneIndex = 0
                    val newBoxOne = boxOne.copy(index = 0)
                    val newBoxTwo = boxTwo
                    val newBoxThree = boxThree
                    stateList.tryEmit(listOf(newBoxOne, newBoxTwo, newBoxThree))

                }
                else{
                    stateList.tryEmit(listOf(boxOne, boxTwo, boxThree))
                }
                _fullChat.value = false


            }
            else if(tripleSize){
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(index = 0)
                boxOneIndex = 0
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(tripleSize = false, index = 0)
                boxTwoIndex = 0
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(index = 0)
                boxThreeIndex = 0
                stateList.tryEmit(listOf(boxOne,boxTwo,boxThree))
                _fullChat.value = false
            }
            else {
                val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(doubleSize = false, index = newValue)
                stateList.tryEmit(listOf(boxOne, boxTwo, boxThree))
            }
        } //this is the end of the index == 0 conditional
        else{
            val top =stateList.value.find {it.boxNumber == BoxNumber.THREE}!!.copy(index = newValue)
            val center =stateList.value.find {it.boxNumber == BoxNumber.TWO}!!
            val bottom =stateList.value.find {it.boxNumber == BoxNumber.ONE}!!

            stateList.tryEmit(listOf(top,center,bottom))
        }

    }



    var boxThreeDragState = DraggableState { delta ->
//        if(_boxesDragging.value.boxThreeDragging){ //ensures that the user has to double click before the
            if(boxThreeIndex != 99  && boxThreeIndex != 0){ //todo: this need to be done with all of the drag states
                val boxThreeIsDoubleSize = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.doubleSize
                val boxThreeIsTripleSize = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.tripleSize
                if(boxThreeIsTripleSize){
                    Log.d("boxThreeStateSizeDoublingLogs","TRIPLE MOVING")
                    boxThreeTripleSizeDragging()
                }
                else if(boxThreeIsDoubleSize){
                    boxThreeDoubleSizeDragging(delta)

                }else{
                    Log.d("boxThreeStateSizeDoublingLogs","NOT DOUBLE")
                    boxThreeSingleSizeDragging(delta)
                }
                boxThreeOffsetY += delta
            }
        //}


    }
    fun boxThreeTripleSizeDragging(){

        if(boxThreeOffsetY >= (0.6*section2height) ){
            Log.d("boxTwoTripleSizeDragging","DELETING")
            if(!deleteBoxThree){
                deleteBoxThree = true
            }
        }else{
            Log.d("boxTwoTripleSizeDragging"," not DELETING")
            if(deleteBoxThree){
                deleteBoxThree = false
            }
        }
    }
    fun boxThreeDoubleSizeDragging(delta: Float){
        //todo: here it is moving BoxNumber.THREE  and BoxNumber.ONE
        //  todo: add the double check

            /********* ENTERING SECTION THREE (DELETING)*****************************************************/
            if(boxThreeOffsetY >= (0.6*(section3Height))){
                Log.d("boxThreeStateSizeDoublingLogs","DELETE")
                if(!deleteBoxThree){
                    deleteBoxThree = true
                }

            }
            /********* ENTERING SECTION TWO (BOTTOM) ************/
             //this means that the double section is entering the bottom
            //so I think the problem is that there is a sync problem
            else if(boxThreeOffsetY >= (0.6*(section2height)) && boxThreeOffsetY<=(0.6*(section3Height))){
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
                        val newCenter  =ModArrayData(700f.dp,boxThreeIndex,Positions.CENTER,BoxNumber.THREE,true,true,false)
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
            else if(boxThreeOffsetY <= (0.6*(section2height))){
                //todo: THE SAME THING AS ABOVE TO CHECK THE BOX VALUES OF 99

                if(boxThreeSection != Sections.ONE){

                    val boxTwo= stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
                    val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
                    boxOneSection = Sections.THREE
                    if(boxTwo.index == 99){

                        val newTop =ModArrayData(700f.dp,boxThreeIndex,Positions.TOP,BoxNumber.THREE,true,true,false)
                        val newCenter =stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.TWO)
                        val newBottom = stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.ONE)
                        stateList.tryEmit(listOf(newTop,newCenter,newBottom))

                    }
                    if(boxOne.index == 99){
                        val newTop =ModArrayData(700f.dp,boxThreeIndex,Positions.TOP,BoxNumber.THREE,true,true,false)
                        val newCenter =stateList.value.find { it.position ==Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER, boxNumber = BoxNumber.ONE)
                        val newBottom =stateList.value.find { it.position ==Positions.TOP }!!.copy(dragging = false, position = Positions.BOTTOM, boxNumber = BoxNumber.TWO)

                        val newList =listOf(newTop,newCenter,newBottom)
                        Log.d("StateListUpdate","sectionOneEnter ->$newList")
                        stateList.tryEmit(newList)
                    }





                }
                boxThreeSection = Sections.ONE
            }



    }
    fun boxThreeSingleSizeDragging(
        delta: Float
    ){
        //todo: making the  double checks
        val boxOneIsDouble = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.doubleSize //todo: this is causing a crash
        val boxTwoIsDouble = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.doubleSize

        if(boxOneIsDouble|| boxTwoIsDouble){
            Log.d("boxThreeDoubleSizeDragging","One --> $boxOneIsDouble")
            Log.d("boxThreeDoubleSizeDragging","Two --> $boxTwoIsDouble")
            /********* DELETING ************/
            if(boxThreeOffsetY >= ( deleteOffset)){
                Log.d("Box3SingleMovingWhenDouble","DELETE")
                if(!deleteBoxThree){
                    deleteBoxThree = true
                }
            }
            /********* ENTERING SECTION 3 (BOTTOM) ************/
            else if(boxThreeOffsetY >= (0.6*(section3Height))){
                if(deleteBoxThree){
                    deleteBoxThree = false
                }
                if(boxThreeSection != Sections.THREE){
                    Log.d("Box3SingleMovingWhenDouble","BOTTOM")
                    val newTop =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                    val newCenter =  stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                    val newBottom  =ModArrayData(700f.dp,boxThreeIndex,Positions.BOTTOM,BoxNumber.THREE,true,false,false)

                    val newList = listOf(newTop,newCenter,newBottom)
                    stateList.tryEmit(newList)

                    boxThreeSection = Sections.THREE
                }


            }
            /********* ENTERING SECTION ONE(TOP) ************/
            else if(boxThreeOffsetY <= (0.6*(section2height))){
                Log.d("Box3SingleMovingWhenDouble","TOP")
                if(boxThreeSection !=Sections.ONE){
                    val newTop  =ModArrayData(700f.dp,boxThreeIndex,Positions.TOP,BoxNumber.THREE,true,false,false)
                    val newCenter=  stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                    val newBottom =  stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)

                    val newList = listOf(newTop,newCenter,newBottom)
                    stateList.tryEmit(newList)

                    boxThreeSection = Sections.ONE
                }

                }


        } //end of the double checks
        else{
            if(boxThreeOffsetY >= deleteOffset){
                Log.d("boxThreeDragStateLogging","DELETE")
                if(!deleteBoxThree){
                    deleteBoxThree = true
                }
            }
            /********* ENTERING SECTION THREE ************/
            else if(boxThreeOffsetY >= (0.6*(section3Height))){
                if(deleteBoxThree){
                    deleteBoxThree = false
                }
                if(boxThreeSection != Sections.THREE){
                    Log.d("boxThreeDragStateLogging","SECTION 3")
                    val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                    val newCenter = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false, position = Positions.CENTER)
                    val newBottom  =ModArrayData(700f.dp,boxThreeIndex,Positions.BOTTOM,BoxNumber.THREE,true,false,false)

                    val newList = listOf(top,newCenter,newBottom)
                    stateList.tryEmit(newList)
                    boxThreeSection = Sections.THREE
                }



            }

            /********* ENTERING SECTION TWO ************/
            else if(boxThreeOffsetY >= (0.6*(section2height)) && boxThreeOffsetY<=(0.6*(section3Height))){
                if(boxThreeSection != Sections.TWO){
                    Log.d("boxThreeDragStateLogging","SECTION 2")
                    if(0>=delta) { //true means dragging up when entering section 2
                        Log.d("boxThreeDragStateLogging","SECTION 2 UP")
                        val top = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false)
                        val newCenter  =ModArrayData(700f.dp,boxThreeIndex,Positions.CENTER,BoxNumber.THREE,true,false,false)
                        val newBottom = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.BOTTOM)
                        val newList = listOf(top,newCenter,newBottom)
                        stateList.tryEmit(newList)

                    }else { //means the user is dragging down when entering section 2
                        Log.d("boxThreeDragStateLogging","SECTION 2 DOWN")
                        val newTop = stateList.value.find { it.position == Positions.CENTER }!!.copy(dragging = false, position = Positions.TOP)
                        val newCenter  =ModArrayData(700f.dp,boxThreeIndex,Positions.CENTER,BoxNumber.THREE,true,false,false)
                        val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)
                        val newList = listOf(newTop,newCenter,bottom)
                        stateList.tryEmit(newList)

                    }

                    boxThreeSection = Sections.TWO
                }

            }

            /********* ENTERING SECTION ONE ************/
            else if(boxThreeOffsetY <= (0.6*(section2height))){

                if(boxThreeSection != Sections.ONE){
                    Log.d("boxThreeDragStateLogging","SECTION 1")
                    val newTop  =ModArrayData(700f.dp,boxThreeIndex,Positions.TOP,BoxNumber.THREE,true,false,false)
                    val newCenter = stateList.value.find { it.position == Positions.TOP }!!.copy(dragging = false, position = Positions.CENTER)
                    val bottom = stateList.value.find { it.position == Positions.BOTTOM }!!.copy(dragging = false)

                    val newList = listOf(newTop,newCenter,bottom)
                    stateList.tryEmit(newList)
                    boxThreeSection = Sections.ONE

                }

            }
        }

    }
    fun boxThreeTripleSync(){
        // when this function runs, we know this. Box three is in double mode
        //todo: we need to do 5 things:
        //1) increase the boxThreeHeight
        boxThreeHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*3).dp
        //2) set boxThree double to false and boxThree triple to true
        val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(doubleSize = false, tripleSize = true)
        //3) set boxOne and boxTwoIndex to 99
        val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(index = 99)
        boxOneIndex =99
        val boxTwo = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(index = 99)
        boxTwoIndex = 99
        //4) emit new state to stateList
        stateList.tryEmit(listOf(boxTwo,boxOne,boxThree))
        //5) do special movement on boxThreeOffset for when boxThreeTriple is set to true

    }
    /********************************************  END OF BOX 3 STATE   ******************************************************/

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
        Log.d("SETTINGTHEINDEXAGAIN","CLICK")

        if(boxOneIndex ==0){
            Log.d("BoxTwoTriple","1->0")

            if(checkBoxTwoForTriple(newValue)){
                //this is the one that is causing the weird UI length bug
                boxTwoTripleSync()
            }
            else if(checkBoxThreeForTriple(newValue)){
                //todo: make the a function called boxThreeTripleSync()
                // it will do the exact same things as boxTwoTripleSync() but just the box3 version of it

                boxThreeTripleSync()

            }
            else{

                Log.d("BoxOneIndexTesting","triggered")
               // check the doubles
                if(checkingBoxTwoIndexForDoubles(newValue)){
                    syncBoxTwoDouble()
                }
                else if(checkingBoxThreeIndexForDoubles(newValue)){
                    syncBoxThreeDouble()
                }
                else{
                    syncBoxOneIndex(newValue)
                }

            }

        }
        //todo: triple checks
        else if(boxTwoIndex ==0){
            Log.d("BoxTwoTriple","2->0")
            if(checkBoxThreeForTriple(newValue)){
                boxThreeTripleSync()

            }
            else if(checkBoxOneForTriple(newValue)){
                Log.d("BoxOneTriple","TRIPLE!!!!!!!")
                //todo: make the a function called boxOneTripleSync()
                // it will do the exact same things as boxTwoTripleSync() but just the boxOne version of it
                //remove the green, add the red, remove the blue and add another red
                boxOneTripleSync()

            }
            else{


                if(checkingBoxOneIndexForDoubles(newValue)){
                    Log.d("stateListIndexLogging","ONE DOUBLE")
                    syncBoxOneDouble()
                }
                else if(checkingBoxThreeIndexForDoubles(newValue)){
                    Log.d("stateListIndexLogging","THREE DOUBLE")
                    //when this function runs we know 2 things:
                    //1) boxTwo == 0
                    //2) boxThree == newValue
                    syncBoxThreeNBoxTwoForDoubles()

                }
                else{
                    syncBoxTwoIndex(newValue)// make sure the index stays synced with statelist
                }

            }

        }
        //todo: triple checks
        else if(boxThreeIndex == 0){
            Log.d("BoxTwoTriple","3->0")
            if(checkBoxOneForTriple(newValue)){
                Log.d("BoxTwoTriple","THIS FUNCTION IS RUNNING ")
                boxOneTripleSync()
            }
            else if(checkBoxTwoForTriple(newValue) ){

                boxTwoTripleSync()
            }else{
                Log.d("stateListIndexLogging","boxThree DOUBLE CHECK")
//            boxThreeIndex = newValue
                //todo: get rid of this double check

                if(checkingBoxOneIndexForDoubles(newValue)){
                    Log.d("stateListIndexLogging","ONE DOUBLE")
                    // when this function runs we know 2 things:
                    // 1) boxOne == newValue
                    //2) boxThree == 0
                    syncBoxOneNBoxThreeForDoubles()

                }
                else if(checkingBoxTwoIndexForDoubles(newValue)){
                    Log.d("stateListIndexLogging","TWO DOUBLE")
                    syncBoxTwoNBoxThreeForDoubles()
                }
                else{
                    syncBoxThreeIndex(newValue)
                }

            }

        }
        else {
            viewModelScope.launch {
                _showPlacementError.value = true
                delay(1000)
                _showPlacementError.value = false
            }

        }
    }

    fun checkBoxOneForTriple(newIndex:Int):Boolean{
        val boxOne = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!
        val boxOneIsDouble = boxOne.doubleSize
        val index = boxOne.index
        Log.d("checkingForTriple","ONE  triple -> $boxOneIsDouble")
        return boxOneIsDouble && index == newIndex
    }
    fun checkBoxTwoForTriple(newIndex:Int):Boolean{
        val boxTwo =stateList.value.find { it.boxNumber == BoxNumber.TWO }!!
        val boxTwoIsDouble = boxTwo.doubleSize
        val index = boxTwo.index
        Log.d("checkingForTriple","TWO  triple -> $boxTwoIsDouble")
        Log.d("checkingForTriple","boxTwo index from stateList -> $index")
        Log.d("checkingForTriple","actual BoxTwo index  -> $boxTwoIndex")
        Log.d("checkingForTriple","newIndex -> $newIndex")
        return boxTwoIsDouble && newIndex == index
    }
    fun checkBoxThreeForTriple(newIndex:Int):Boolean{
        val boxThree = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!
        val boxThreeIsDouble = boxThree.doubleSize
        val index = boxThree.index
        Log.d("checkingForTriple","THREE  triple -> $boxThreeIsDouble")
        return boxThreeIsDouble && index == newIndex
    }


    /********************************START OF THE NEW DOUBLE CHECKS***************************************************************/
    fun checkingBoxOneIndexForDoubles(newValue:Int):Boolean{
        return boxOneIndex == newValue
    }
    fun checkingBoxTwoIndexForDoubles(newValue:Int):Boolean{
        return boxTwoIndex == newValue
    }
    fun checkingBoxThreeIndexForDoubles(newValue:Int):Boolean{
        return boxThreeIndex == newValue
    }
    fun syncBoxTwoDouble(){
        Log.d("SyncingDoubles"," syncBoxTwoDouble()")
        Log.d("FullChatLogging","")
        //when this function runs, we know that boxTwoIndex == newValue
        val top = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true, height = ((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp)
        val topIndex = top.index
        Log.d("FullChatLogging","syncBoxTwoDouble() fullchat --> ${topIndex == 1}")
        if(topIndex == 1){
            _fullChat.value = true
        }

        val center = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.CENTER, index = 99)
        val bottom = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.BOTTOM)
        boxOneIndex =99
        boxTwoHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
        Log.d("loggindBoxTwoHeight","boxTwoHeight ->${boxTwoHeight.value}")
        Log.d("checkingForBoxOneDoublesAgain","list ->${listOf(top,center,bottom)}")

        stateList.tryEmit(listOf(top,center,bottom))

    }

    fun syncBoxThreeDouble(){
        Log.d("SyncingDoubles"," syncBoxThreeDouble()")


        val top = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true)
        val topIndex = top.index
        Log.d("FullChatLogging","syncBoxThreeDouble() fullchat --> ${topIndex == 1}")
        if(topIndex == 1){
            _fullChat.value = true
        }
        val center = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.CENTER, index = 99)
        val bottom = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.BOTTOM)
        boxOneIndex = 99
        boxThreeHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp

        stateList.tryEmit(listOf(top,center,bottom))

    }
    fun syncBoxOneDouble(){
        Log.d("SyncingDoubles"," syncBoxOneDouble()")
        //todo: this is the level that we need it as, [boxOne,boxTwo,boxThree]
        val top = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true,height=((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp)
        val topIndex = top.index
        Log.d("FullChatLogging","syncBoxOneDouble() fullchat --> ${topIndex == 1}")
        if(topIndex == 1){
            _fullChat.value = true
        }
        val center = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.CENTER, doubleSize = false, index = 99)
        val bottom = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.BOTTOM, doubleSize = false)

        boxOneHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
        boxTwoIndex = 99
        stateList.tryEmit(listOf(top,center,bottom))
        setBoxOneDragging(true)
    }
    fun syncBoxThreeNBoxTwoForDoubles(){
        Log.d("SyncingDoubles"," syncBoxThreeNBoxTwoForDoubles()")

        //todo: this is the level that we need [boxThree,boxTwo,boxOne]
        val top = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true)
        val topIndex = top.index
        Log.d("FullChatLogging","syncBoxThreeNBoxTwoForDoubles() fullchat --> ${topIndex == 1}")
        if(topIndex == 1){
            _fullChat.value = true
        }
        val center = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.CENTER, doubleSize = false, index = 99)
        val bottom = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.BOTTOM, doubleSize = false)
        boxTwoIndex = 99
        boxThreeHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp


        stateList.tryEmit(listOf(top,center,bottom))
        setBoxThreeDragging(true)
    }
    fun syncBoxOneNBoxThreeForDoubles(){
        Log.d("SyncingDoubles"," syncBoxOneNBoxThreeForDoubles()")
        //1) boxThreeIndex is 0
        //2) boxOneIndex == newValue
        val top = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true,height=((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp)
        val topIndex = top.index
        Log.d("FullChatLogging","syncBoxOneNBoxThreeForDoubles() fullchat --> ${topIndex == 1}")
        if(topIndex == 1){
            _fullChat.value = true
        }
        val center = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.CENTER, doubleSize = false, index = 99)
        val bottom = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.BOTTOM, doubleSize = false)

        boxThreeIndex = 99
        boxOneHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp

        stateList.tryEmit(listOf(top,center,bottom))
        setBoxOneDragging(true)
    }
    fun syncBoxTwoNBoxThreeForDoubles(){
        Log.d("SyncingDoubles"," syncBoxTwoNBoxThreeForDoubles()")
        //1) boxThreeIndex is 0
        //2) boxTwoIndex == newValue

        val top = stateList.value.find { it.boxNumber == BoxNumber.TWO }!!.copy(dragging = false, position = Positions.TOP, doubleSize = true,)
        val topIndex = top.index
        Log.d("FullChatLogging","syncBoxTwoNBoxThreeForDoubles() fullchat --> ${topIndex == 1}")
        if(topIndex == 1){
            _fullChat.value = true
        }
        val center = stateList.value.find { it.boxNumber == BoxNumber.THREE }!!.copy(dragging = false, position = Positions.CENTER, doubleSize = false, index = 99)
        val bottom = stateList.value.find { it.boxNumber == BoxNumber.ONE }!!.copy(dragging = false, position = Positions.BOTTOM, doubleSize = false)
        boxThreeIndex = 99
        boxTwoHeight =((Resources.getSystem().displayMetrics.heightPixels / 8.4)*2).dp
        // to avoid another stateList update, I am manually syncing the index for box 3

        stateList.tryEmit(listOf(top,center,bottom))
        setBoxTwoDragging(true)
    }


    /*************************************END OF THE NEW DOUBLE CHECKS*******************************************************************/


//todo: I need to add the height animations
    fun checkBoxOneState(item: ModArrayData){
        if(item.boxNumber == BoxNumber.ONE && item.position == Positions.TOP  && item.dragging == false){
            boxOneOffsetY = 0f
            boxOneSection = Sections.ONE

        }
        else if(item.boxNumber == BoxNumber.ONE && item.position == Positions.CENTER  && item.dragging == false){
            boxOneOffsetY = section2height
            boxOneSection = Sections.TWO
        }
        else if(item.boxNumber == BoxNumber.ONE && item.position == Positions.BOTTOM  && item.dragging == false){
            boxOneOffsetY = section3Height
            boxOneSection = Sections.THREE

        }

    }
    fun checkBoxTwoState(item: ModArrayData){
        if(item.boxNumber == BoxNumber.TWO && item.position == Positions.TOP  && item.dragging == false){
            boxTwoOffsetY = 0f
            boxTwoSection = Sections.ONE

        }
        else if(item.boxNumber == BoxNumber.TWO && item.position == Positions.CENTER  && item.dragging == false){
            boxTwoOffsetY = section2height
            boxTwoSection = Sections.TWO

        }
        else if(item.boxNumber == BoxNumber.TWO && item.position == Positions.BOTTOM  && item.dragging == false){
            boxTwoOffsetY = section3Height
            boxTwoSection = Sections.THREE

        }

    }

    fun checkBoxThreeState(item: ModArrayData){
        if(item.boxNumber == BoxNumber.THREE && item.position == Positions.TOP  && item.dragging == false){
            boxThreeOffsetY = 0f
            boxThreeSection = Sections.ONE

        }
        else if(item.boxNumber == BoxNumber.THREE && item.position == Positions.CENTER  && item.dragging == false){
            boxThreeOffsetY = section2height
            boxThreeSection = Sections.TWO

        }
        else if(item.boxNumber == BoxNumber.THREE && item.position == Positions.BOTTOM  && item.dragging == false){
            boxThreeOffsetY = section3Height
            boxThreeSection = Sections.THREE

        }

    }


}

