package com.example.clicker.presentation.minigames.dinoRun

import android.util.Log


object DinoRunJNI{


    init{
        //todo: I need to make this file and add it to the CMakeList
        System.loadLibrary("dino_run");
    }


    private var onTextUpdate: ((String) -> Unit)? = null

    private var onSpeedIncrease: (() -> Unit)? = null

    @JvmStatic
    fun setOnTextUpdateCallback(callback: (String) -> Unit) {
        onTextUpdate = callback
    }

    @JvmStatic
    fun updateTextFromNative(newText: String) {
        onTextUpdate?.invoke(newText)
    }
    @JvmStatic
    fun setOnSpeedIncreaseCallback(callback: () -> Unit) {
        onSpeedIncrease = callback
    }

    @JvmStatic
    fun updateOnSpeedIncreaseFromNative() {
        onSpeedIncrease?.invoke()
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