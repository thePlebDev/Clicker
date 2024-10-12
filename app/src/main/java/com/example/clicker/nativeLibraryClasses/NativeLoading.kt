package com.example.clicker.nativeLibraryClasses

object NativeLoading {

    init {
        System.loadLibrary("gl_code");
        System.loadLibrary("my_class");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()
}

object NativeSquareLoading{

    init{
        System.loadLibrary("square_code");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()

    external fun click()

}


object NativeBlurEffect{

    init{
        System.loadLibrary("blur_effect");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()


}

object NativeCube{

    init{
        System.loadLibrary("cube_code");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()


}

object NativeUnderstandTriangle{

    init{
        System.loadLibrary("understand_triangle");
    }


    /**
     * @param width the current view width
     * @param height the current view height
     */
    external fun init(width: Int, height: Int)
    external fun step()


}
