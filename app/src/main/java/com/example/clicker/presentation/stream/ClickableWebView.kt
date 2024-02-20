package com.example.clicker.presentation.stream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

import android.webkit.WebView


class ClickableWebView: WebView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }
    var expanded = false


    var expandedMethod ={}
    var collapsedMethod={}



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

