package com.example.clicker.presentation.home.testing3DCode

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clicker.farmingGame.GL2JNIView
import com.example.clicker.farmingGame.NewTestingGLSurfaceView


@Composable
fun TestingGLSurfaceViewComposable(
    context: Context,
    modifier: Modifier
) {
    AndroidView(
        factory = {
            GL2JNIView(context)
        },
        modifier = modifier
    )
}

@Composable
fun TestingGLSurfaceViewUnderstandingTriangle(
    context: Context,
    modifier: Modifier
) {
    AndroidView(
        factory = {
            NewTestingGLSurfaceView(context)
        },
        modifier = modifier
    )
}



//TestingGLSurfaceViewComposable(context,Modifier.fillMaxSize())
//TestingGLSurfaceViewUnderstandingTriangle(context,Modifier.fillMaxSize())