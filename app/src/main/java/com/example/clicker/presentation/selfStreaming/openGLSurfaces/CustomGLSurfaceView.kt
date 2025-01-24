package com.example.clicker.presentation.selfStreaming.openGLSurfaces

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.clicker.nativeLibraryClasses.NativeCube
import com.example.clicker.nativeLibraryClasses.VideoEncoder
import com.example.clicker.presentation.home.testing3DCode.GL2JNIView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class CustomGLSurfaceView(context: Context) : GLSurfaceView(context) {

    init {
        init()
    }


    private fun init() {


        setEGLContextClientVersion(2)

        setRenderer(CustomVideoRenderer()) //called once and only once in the life-cycle of a GLSurfaceView
    }





}

/**
 * **Renderer** a is a [GLSurfaceView.Renderer] class used to render  the C++ UI code
 *
 * */
private class CustomVideoRenderer : GLSurfaceView.Renderer {
    override fun onDrawFrame(gl: GL10) {
        // The system calls this method on each redraw of the GLSurfaceView
        //this is called constantly

        VideoEncoder.step()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // The system calls this method when the GLSurfaceView geometry changes,
        // including changes in size of the GLSurfaceView or orientation of the device screen.

        VideoEncoder.init(width,height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // The system calls this method once, when creating the GLSurfaceView.
        // Do nothing.


    }

}