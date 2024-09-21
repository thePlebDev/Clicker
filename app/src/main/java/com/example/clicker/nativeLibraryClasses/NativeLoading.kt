package com.example.clicker.nativeLibraryClasses

object NativeLoading {

    init {
        System.loadLibrary("gl_code");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()
}

