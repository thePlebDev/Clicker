package com.example.clicker.presentation.stream.customWebViews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView


class HorizontalClickableWebView: WebView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }
    var expanded = false
    var longPressOpen = false

    var overlayDragCeiling =471f
    var overlayDragFloor =1415f
//    var overlayDragCeiling =0f //this is how far the user can drag UP
//    var overlayDragFloor =0f //this is how far the user can drag DOWN (is the largest of the two numbers)



    var expandedMethod ={} //called to make the webView full screen
    var collapsedMethodDoubleClick={}
    var collapsedMethodLongPress={}
    var singleTapMethod={}
    var showLongClickView ={}
    var hideLongClickView ={}
    var dragFunction:(Float) ->Unit={}



    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            Log.d("HORIZONTALScrollDistanceDetection","Y -> $distanceY")
            dragFunction(distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }


        override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {

            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    if(!expanded){
                        expanded = true
                        longPressOpen = false
                        Log.d("onDoubleTapEvent","expandedMethod()")
                        expandedMethod()
                    }else{
                        expanded = false
                        longPressOpen = false
                        Log.d("onDoubleTapEvent","collapsedMethod()")
                        hideLongClickView()
                        collapsedMethodDoubleClick()
                    }
                }
            }



            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Log.d("onSingleTapConfirmed","TAPPING")
            singleTapMethod()
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            collapsedMethodLongPress()
            showLongClickView()
            expanded = false
        }

    }

    private val detector: GestureDetector = GestureDetector(context, myListener)

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return detector.onTouchEvent(event).let { result ->
            performClick()
            true
        }
    }


    override fun performClick(): Boolean {

        return super.performClick()

    }

}