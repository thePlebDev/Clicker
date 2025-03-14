package com.example.clicker.presentation.minigames.dinoRun

import android.util.Log


object DinoRunJNI{


    init{
        //todo: I need to make this file and add it to the CMakeList
        System.loadLibrary("dino_run");
    }


    private var onTextUpdate: ((String) -> Unit)? = null

    @JvmStatic
    fun setOnTextUpdateCallback(callback: (String) -> Unit) {
        onTextUpdate = callback
    }

    @JvmStatic
    fun updateTextFromNative(newText: String) {
        onTextUpdate?.invoke(newText)
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()
    external fun jump()
//    external fun triggerUpdate()




}