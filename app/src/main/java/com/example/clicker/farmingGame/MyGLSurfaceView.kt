package com.example.clicker.farmingGame

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.clicker.nativeLibraryClasses.NativeLoading
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.opengles.GL10


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;





internal class GL2JNIView(context: Context?) : GLSurfaceView(context) {
    init {
        init()
    }


    private fun init() {


        /* Setup the context factory for 2.0 rendering.
         * See ContextFactory class definition below
         */
       // setEGLContextFactory(ContextFactory())

       // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        /* Set the renderer responsible for frame rendering */
        setRenderer(Renderer())
    }




    //todo: This is the renderer I am looking for
    private class Renderer : GLSurfaceView.Renderer {
        override fun onDrawFrame(gl: GL10) {
            NativeLoading.step()
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            NativeLoading.init(width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // Do nothing.
        }
    }

    companion object {
        private const val TAG = "GL2JNIView"
        private const val DEBUG = false
        private fun checkEglError(prompt: String, egl: EGL10) {
            var error: Int
            while (egl.eglGetError().also { error = it } != EGL10.EGL_SUCCESS) {
                Log.e(TAG, String.format("%s: EGL error: 0x%x", prompt, error))
            }
        }
    }
}