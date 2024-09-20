package com.example.clicker.nativeLibraryClasses

class NativeLoading {

    init {
        System.loadLibrary("gl_code");
    }

    external fun init()
}