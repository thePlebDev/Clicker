package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class DinoRunView(context: Context?) : GLSurfaceView(context){
    private val renderer = DinoRunRenderer()

    init{
        init()
    }

    private fun init(){

        setEGLContextClientVersion(2)
        setRenderer(renderer)

    }

}



class DinoRunRenderer : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        DinoRunJNI.init(width,height)
    }

    override fun onDrawFrame(p0: GL10?) {

    }

}