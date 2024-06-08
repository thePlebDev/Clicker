package com.example.clicker.presentation.stream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

import android.webkit.WebView
import kotlin.math.exp


class ClickableWebView: WebView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }
    var expanded = false


    var expandedMethod ={}
    var collapsedMethod={}
    var singleTapMethod={}



private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }
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


/****************************START OF THE HORIZONTAL CLICKABLE WEBVIEW**********************************************/




class HorizontalClickableWebView: WebView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }
    var expanded = false
    var longPressOpen = false



    var expandedMethod ={} //called to make the webView full screen
    var collapsedMethodDoubleClick={}
    var collapsedMethodLongPress={}
    var singleTapMethod={}
    var showLongClickView ={}
    var hideLongClickView ={}



    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
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

