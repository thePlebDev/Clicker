package com.example.clicker.presentation.minigames.dinoRun

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


@Composable
fun TriangleStripTesting(
    modifier:Modifier,
    context: Context,
) {
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = {
                TriangleStripView(context)

            },

            )
    }
}

class TriangleStripView(context: Context?) : GLSurfaceView(context){
    private val renderer = TriangleStripRenderer()


    init{
        init()
    }

    private fun init(){

        setEGLContextClientVersion(2)
        setRenderer(renderer)

    }
}

object TriangleStripJNI{
    init{
        //todo: I need to make this file and add it to the CMakeList
        System.loadLibrary("triangle_strip");
    }
    external fun init(width: Int, height: Int)
    external fun step()

}

class TriangleStripRenderer : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        TriangleStripJNI.init(width,height)
    }

    override fun onDrawFrame(p0: GL10?) {
        TriangleStripJNI.step()

    }


}