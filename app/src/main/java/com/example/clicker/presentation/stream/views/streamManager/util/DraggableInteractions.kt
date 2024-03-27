package com.example.clicker.presentation.stream.views.streamManager.util

import androidx.core.view.HapticFeedbackConstantsCompat.HapticFeedbackType

enum class Section {
    ONE, TWO, THREE, OTHER
}

fun changeSectionOneNTwo(
    changeBoxOneToSectionOne:()->Unit,
    changeBoxOneToSectionTwo: () -> Unit,
    changeBoxOneToSectionThree: () -> Unit,

    changeBoxTwoToSectionOne:()->Unit,
    changeBoxTwoToSectionTwo: () -> Unit,
    changeBoxTwoToSectionThree: () -> Unit,

    boxOneSection:Section,
    boxTwoSection:Section,
    boxThreeSection:Section,
    isDraggedDown:Boolean,
    performHapticFeedbackType: ()->Unit

){
    if(boxOneSection == boxThreeSection && boxOneSection == Section.ONE){
        performHapticFeedbackType()
        changeBoxOneToSectionTwo()

    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && isDraggedDown){
        performHapticFeedbackType()
        changeBoxOneToSectionThree()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && !isDraggedDown){
        performHapticFeedbackType()
        changeBoxOneToSectionOne()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.THREE){
        performHapticFeedbackType()
        changeBoxOneToSectionTwo()
    }
    /***********SECTION 2 CHANGES********/

    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.ONE){
        performHapticFeedbackType()
        changeBoxTwoToSectionTwo()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && isDraggedDown){
        performHapticFeedbackType()
        changeBoxTwoToSectionThree()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && !isDraggedDown){
        performHapticFeedbackType()
        changeBoxTwoToSectionOne()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.THREE){
        performHapticFeedbackType()
        changeBoxTwoToSectionTwo()
    }

}

fun changeSectionTwoNThree(
    changeBoxThreeToSectionOne:()->Unit,
    changeBoxThreeToSectionTwo: () -> Unit,
    changeBoxThreeToSectionThree: () -> Unit,

    changeBoxTwoToSectionOne:()->Unit,
    changeBoxTwoToSectionTwo: () -> Unit,
    changeBoxTwoToSectionThree: () -> Unit,

    boxOneSection:Section,
    boxTwoSection:Section,
    boxThreeSection:Section,
    isDraggedDown:Boolean,
    performHapticFeedbackType: ()->Unit
){
    if(boxOneSection == boxThreeSection && boxOneSection == Section.ONE){
        performHapticFeedbackType()
        changeBoxThreeToSectionTwo()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && isDraggedDown){
        performHapticFeedbackType()
        changeBoxThreeToSectionThree()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && !isDraggedDown){
        performHapticFeedbackType()
        changeBoxThreeToSectionOne()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.THREE){
        performHapticFeedbackType()
        changeBoxThreeToSectionTwo()
    }
    /***********SECTION 2 CHANGES********/
    if(boxOneSection == boxTwoSection && boxOneSection == Section.ONE){
        performHapticFeedbackType()
        changeBoxTwoToSectionTwo()
    }
    if(boxOneSection == boxTwoSection && boxOneSection == Section.TWO && isDraggedDown){
        performHapticFeedbackType()
        changeBoxTwoToSectionThree()
    }
    if(boxOneSection == boxTwoSection && boxOneSection == Section.TWO && !isDraggedDown){
        performHapticFeedbackType()
        changeBoxTwoToSectionOne()
    }
    if(boxOneSection == boxTwoSection && boxOneSection == Section.THREE){
        performHapticFeedbackType()
        changeBoxTwoToSectionTwo()
    }

}
fun changeSectionOneNThree(
    changeBoxThreeToSectionOne:()->Unit,
    changeBoxThreeToSectionTwo: () -> Unit,
    changeBoxThreeToSectionThree: () -> Unit,

    changeBoxOneToSectionOne:()->Unit,
    changeBoxOneToSectionTwo: () -> Unit,
    changeBoxOneToSectionThree: () -> Unit,

    boxOneSection:Section,
    boxTwoSection:Section,
    boxThreeSection:Section,
    isDraggedDown:Boolean,
    performHapticFeedbackType: ()->Unit
){
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.ONE){
        performHapticFeedbackType()
        changeBoxThreeToSectionTwo()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && isDraggedDown){
        performHapticFeedbackType()
        changeBoxThreeToSectionThree()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && !isDraggedDown){
        performHapticFeedbackType()
        changeBoxThreeToSectionOne()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.THREE){
        performHapticFeedbackType()
        changeBoxThreeToSectionTwo()
    }
    /***********SECTION 2 CHANGES********/

    if(boxTwoSection == boxOneSection && boxTwoSection == Section.ONE){
        performHapticFeedbackType()
        changeBoxOneToSectionTwo()
    }
    if(boxTwoSection == boxOneSection && boxTwoSection == Section.TWO && isDraggedDown){
        performHapticFeedbackType()
        changeBoxOneToSectionThree()
    }
    if(boxTwoSection == boxOneSection && boxTwoSection == Section.TWO && !isDraggedDown){
        performHapticFeedbackType()
        changeBoxOneToSectionOne()
    }
    if(boxTwoSection == boxOneSection && boxTwoSection == Section.THREE){
        performHapticFeedbackType()
        changeBoxOneToSectionTwo()
    }

}