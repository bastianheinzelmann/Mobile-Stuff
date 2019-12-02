package com.example.acubethatrotates

import java.lang.System.loadLibrary

class nativeCubeLib{

    external fun init(width: Int, height: Int, vertexArray: FloatArray)
    external fun resize(width: Int, height: Int)
    external fun update()
    external fun zoom(zoomfactor: Float)
    external fun rotate(angle: Float)

    companion object {
        init{
            loadLibrary("native_cube")
        }
    }
}