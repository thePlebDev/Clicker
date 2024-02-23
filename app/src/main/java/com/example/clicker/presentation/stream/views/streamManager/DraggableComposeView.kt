package com.example.clicker.presentation.stream.views.streamManager

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MotionEventCompat


class DraggableComposeView: ConstraintLayout {

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }
    var whereUserTouched:Float =0f
    val height = Resources.getSystem().displayMetrics.heightPixels.toFloat()
    val halfOfScreen = height/2
    var topOfScreen = height






    // The "active pointer" is the one moving the object.



    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events.


        val action = ev.action

        Log.d("ACTIONDOWN","change in X -->${ev.rawX} change in Y   -->${ev.rawY}")
        when (action) {


            MotionEvent.ACTION_UP-> {
                performClick();
//                Log.d("ACTIONMOVE","topOfScreen -->${topOfScreen} half height -->${halfOfScreen}")
//                Log.d("ACTIONMOVE","topOfScreen > halfOfScreen -->${topOfScreen > halfOfScreen}")
//                if (ev.rawY < whereUserTouched){
//
//                }
                if(topOfScreen > halfOfScreen){
                    topOfScreen = height
                    Log.d("ACTIONMOVE","Y TO 0")
                    this.y =0f
                }else{

                    Log.d("ACTIONMOVE","Y TO HEIGHT")
                    topOfScreen = height
                    this.y=height
                }
            }
            MotionEvent.ACTION_DOWN ->{
                whereUserTouched =ev.rawY
//                Log.d("ACTIONDOWN","change in X -->${ev.rawX} change in Y   -->${ev.rawY}")
            }
            MotionEvent.ACTION_MOVE ->{
            //    Log.d("ACTIONMOVE","change in Y -->${ev.rawY}")
                if(ev.rawY < whereUserTouched){
                   // this.y = (ev.rawY- whereUserTouched)/10
                    topOfScreen += (ev.rawY- whereUserTouched)/35
                }
                else{
                   // Log.d("ACTIONMOVE","distance from touch -->${(ev.rawY- whereUserTouched)}")
                    topOfScreen -= (ev.rawY- whereUserTouched)/35
                    this.y = ev.rawY- whereUserTouched
                }

            }

        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()

        return true
    }
}