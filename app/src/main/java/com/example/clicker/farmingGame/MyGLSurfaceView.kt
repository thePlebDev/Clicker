package com.example.clicker.farmingGame

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.clicker.nativeLibraryClasses.NativeBlurEffect
import com.example.clicker.nativeLibraryClasses.NativeLoading
import com.example.clicker.nativeLibraryClasses.NativeSquareLoading
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.opengles.GL10


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;





internal class GL2JNIView(context: Context?) : GLSurfaceView(context) {
    init {
        init2()
    }


    private fun init() {

       // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        /* Set the renderer responsible for frame rendering */
        setRenderer(Renderer())
    }
    private fun init2() { // this is the transparent on
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // Request an EGL configuration that supports transparency
        setEGLConfigChooser(8, 8, 8, 8, 16, 0) // this is needed

        // Set the surface to be translucent
        holder.setFormat(PixelFormat.TRANSLUCENT)// this is needed
        //holder.setFormat(PixelFormat.OPAQUE)

        // Set the renderer responsible for frame rendering
        setRenderer(Renderer2())

        // Ensure the background of the GLSurfaceView is transparent
        setZOrderOnTop(true) // Optional: if you want the surface on top THIS IS NEEDED
    }




    //todo: This is the renderer I am looking for
    private class Renderer : GLSurfaceView.Renderer {
        override fun onDrawFrame(gl: GL10) {
            // The system calls this method on each redraw of the GLSurfaceView
            //this is called constantly
         //   NativeSquareLoading.step() // this is the checker board

            NativeBlurEffect.step() // this is the blur effect
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            // The system calls this method when the GLSurfaceView geometry changes,
            // including changes in size of the GLSurfaceView or orientation of the device screen.
          //  NativeSquareLoading.init(width, height) // this is the checker board
            NativeBlurEffect.init(width, height) // this is the blur effect
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // The system calls this method once, when creating the GLSurfaceView.
            // Do nothing.

        }
    }
    inner class Renderer2 : GLSurfaceView.Renderer { // this is  the transparent one
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//            // Enable blending for transparency
            gl?.glEnable(GLES20.GL_BLEND)
            gl?.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        }

        override fun onDrawFrame(gl: GL10?) {
            // Clear the color buffer with the transparent color

            NativeBlurEffect.step()

            // Your rendering code here
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            NativeBlurEffect.init(width, height)
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                requestRender()
            }

            MotionEvent.ACTION_UP ->{
                //this runs when someone clicks and then releases
                NativeSquareLoading.click()
                requestRender()
            }
        }


        return true
    }


}