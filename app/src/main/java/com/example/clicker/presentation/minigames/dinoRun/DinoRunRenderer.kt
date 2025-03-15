package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class DinoRunView(context: Context?) : GLSurfaceView(context), View.OnTouchListener{
    private val renderer = DinoRunRenderer()


    init{
        init()
        setOnTouchListener(this)
    }

    private fun init(){

        setEGLContextClientVersion(2)
        setRenderer(renderer)

    }



    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event ?: return false
        //v?.parent?.requestDisallowInterceptTouchEvent(true) // this remove the swiping from the horizontal pager




        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                Log.d("TESTINGACtionMoveTouch","JUMP!!!!!")
                DinoRunJNI.jump()


                return true
            }


            MotionEvent.ACTION_MOVE -> {
                Log.d("TESTINGACtionMoveTouch","MOVE!!!!!")


                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("TESTINGACtionMove","UPP")

                return true
            }
        }
        return false
    }

}



class DinoRunRenderer : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        DinoRunJNI.init(width,height)
    }

    override fun onDrawFrame(p0: GL10?) {
        DinoRunJNI.step()

    }


}