package com.example.clicker.presentation.minigames.dinoRun



object DinoRunJNI{

    init{
        //todo: I need to make this file and add it to the CMakeList
        System.loadLibrary("dino_run");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()



}