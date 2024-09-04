package com.example.clicker.presentation.stream.customWebViews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.webkit.WebView

class VerticalWebView: WebView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }
    var expanded = false
    init{
        Log.d("onScrollDistanceDetection","INIT")
    }


    var expandedMethod ={}
    var collapsedMethod={}
    var singleTapMethod={}



    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {


            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    if(!expanded){
                        expanded = !expanded
                        expandedMethod()
                    }else{
                        expanded = !expanded
                        collapsedMethod()
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
            Log.d("onLongPress","VERTICAL LONG PRESS")
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