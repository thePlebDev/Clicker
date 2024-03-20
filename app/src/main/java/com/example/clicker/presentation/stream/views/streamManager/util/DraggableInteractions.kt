package com.example.clicker.presentation.stream.views.streamManager.util
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
    isDraggedDown:Boolean

){
    if(boxOneSection == boxThreeSection && boxOneSection == Section.ONE){
        changeBoxOneToSectionTwo()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && isDraggedDown){
        changeBoxOneToSectionThree()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && !isDraggedDown){
        changeBoxOneToSectionOne()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.THREE){
        changeBoxOneToSectionTwo()
    }
    /***********SECTION 2 CHANGES********/

    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.ONE){
        changeBoxTwoToSectionTwo()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && isDraggedDown){
        changeBoxTwoToSectionThree()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && !isDraggedDown){
        changeBoxTwoToSectionOne()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.THREE){
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
    isDraggedDown:Boolean
){
    if(boxOneSection == boxThreeSection && boxOneSection == Section.ONE){
        changeBoxThreeToSectionTwo()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && isDraggedDown){
        changeBoxThreeToSectionThree()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.TWO && !isDraggedDown){
        changeBoxThreeToSectionOne()
    }
    if(boxOneSection == boxThreeSection && boxOneSection == Section.THREE){
        changeBoxThreeToSectionTwo()
    }
    /***********SECTION 2 CHANGES********/
    if(boxOneSection == boxTwoSection && boxOneSection == Section.ONE){
        changeBoxTwoToSectionTwo()
    }
    if(boxOneSection == boxTwoSection && boxOneSection == Section.TWO && isDraggedDown){
        changeBoxTwoToSectionThree()
    }
    if(boxOneSection == boxTwoSection && boxOneSection == Section.TWO && !isDraggedDown){
        changeBoxTwoToSectionOne()
    }
    if(boxOneSection == boxTwoSection && boxOneSection == Section.THREE){
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
    isDraggedDown:Boolean
){
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.ONE){
        changeBoxThreeToSectionTwo()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && isDraggedDown){
        changeBoxThreeToSectionThree()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.TWO && !isDraggedDown){
        changeBoxThreeToSectionOne()
    }
    if(boxTwoSection == boxThreeSection && boxTwoSection == Section.THREE){
        changeBoxThreeToSectionTwo()
    }
    /***********SECTION 2 CHANGES********/

    if(boxTwoSection == boxOneSection && boxTwoSection == Section.ONE){
        changeBoxOneToSectionTwo()
    }
    if(boxTwoSection == boxOneSection && boxTwoSection == Section.TWO && isDraggedDown){
        changeBoxOneToSectionThree()
    }
    if(boxTwoSection == boxOneSection && boxTwoSection == Section.TWO && !isDraggedDown){
        changeBoxOneToSectionOne()
    }
    if(boxTwoSection == boxOneSection && boxTwoSection == Section.THREE){
        changeBoxOneToSectionTwo()
    }

}