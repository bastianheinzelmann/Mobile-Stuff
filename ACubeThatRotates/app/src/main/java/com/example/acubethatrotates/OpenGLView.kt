package com.example.acubethatrotates

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.DisplayMetrics
import android.util.TimingLogger
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import java.nio.ByteBuffer

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.system.measureTimeMillis

class OpenGLView: GLSurfaceView {

    var scaleGestureDetector: ScaleGestureDetector
    var scaleFactor: Float = 1.0f

    var touchStart: Float = 0.0f

    val rotationThreshold = 3.0f
    val rotationStep = 4.0f

    constructor(context: Context?): super(context){
        setEGLConfigChooser(8, 8, 8, 0, 16, 0)
        setEGLContextClientVersion(3)
        setRenderer(Renderer())
        scaleGestureDetector = ScaleGestureDetector(this.context, ScaleListener())
    }

    inner class Renderer: GLSurfaceView.Renderer{
        override fun onDrawFrame(gl: GL10?) {
            nativeCubeLib().update()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            nativeCubeLib().resize(width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            var vertexArrayObj: FloatArray? = null
            var vertexArrayMoces: FloatArray? = null
            var vertexArray: FloatArray? = null
            val loadObj = false
            println("Ich bin eine Zeitmessfunktion. Ich messe die Zeit Moces: " + measureTimeMillis {
                vertexArrayMoces = loadMoces()
            } + " ms")
            println("Ich bin eine Zeitmessfunktion. Ich messe die Zeit Obj: " + measureTimeMillis {
                vertexArrayObj = loadRawObj()
            } + " ms")

            if(loadObj)
                vertexArray = vertexArrayMoces
            else
                vertexArray = vertexArrayObj

            val displayMetrics: DisplayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            nativeCubeLib().init(displayMetrics.widthPixels, displayMetrics.heightPixels, vertexArray!!)
        }
    }

    inner class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector!!.scaleFactor
            scaleFactor = Math.max(-5.0f, Math.min(scaleFactor, 10.0f))

            nativeCubeLib().zoom(scaleFactor)
            return true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN -> {
                touchStart = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                if(touchStart + rotationThreshold < event.x){
                    // nach rechts drehen
                    nativeCubeLib().rotate(rotationStep)
                    touchStart = event.x
                }
                if(touchStart - rotationThreshold > event.x){
                    // nach links drehen
                    nativeCubeLib().rotate(-rotationStep)
                    touchStart = event.x
                }
            }
        }

        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    fun byteArraytoFloat(bytes: ByteArray): Float{
        val buffer = ByteBuffer.wrap(bytes)
        val float = buffer.getFloat()
        return float
    }

    fun loadMoces() : FloatArray{
        val fileName = "psyduck.moces"

        val byteArray: ByteArray

        byteArray = context.assets.open(fileName).readBytes()

        var result = FloatArray(byteArray.size / 4)

        for(i in 0 until byteArray.size step 4){
            var subset = byteArrayOf( byteArray[i], byteArray[i+1], byteArray[i+2], byteArray[i+3])
            val float = byteArraytoFloat(subset)
            result[i/4] = float
        }

        return result
    }

    fun loadRawObj() : FloatArray{
        val fileName = "psyduck.obj"

        // x y z
        val vertexArray: MutableList<Float> = mutableListOf<Float>()
        // x y z
        val normalArray: MutableList<Float> = mutableListOf<Float>()
        // u v
        val uvArray: MutableList<Float> = mutableListOf<Float>()

        // vertexindex uvcoord normal  vertexindex uvcoord normal   vertexindex uvcoord normal
        val vertexIndicesArray: MutableList<Int> = mutableListOf<Int>()
        val uvIndicesArray: MutableList<Int> = mutableListOf<Int>()
        val normalIndicesArray: MutableList<Int> = mutableListOf<Int>()

        context.assets.open(fileName).bufferedReader().useLines {
                lines -> lines.forEach {

            val elements = it.split(" ", "/")

            when(elements[0]){
                "v" -> {
                    vertexArray.add(elements[1].toFloat())
                    vertexArray.add(elements[2].toFloat())
                    vertexArray.add(elements[3].toFloat())
                }
                "vt" -> {
                    uvArray.add(elements[1].toFloat())
                    uvArray.add(elements[2].toFloat())
                }
                "vn" -> {
                    normalArray.add(elements[1].toFloat())
                    normalArray.add(elements[2].toFloat())
                    normalArray.add(elements[3].toFloat())
                }
                "f"  -> {
                    vertexIndicesArray.add(elements[1].toInt())
                    uvIndicesArray.add(elements[2].toInt())
                    normalIndicesArray.add(elements[3].toInt())
                    vertexIndicesArray.add(elements[4].toInt())
                    uvIndicesArray.add(elements[5].toInt())
                    normalIndicesArray.add(elements[6].toInt())
                    vertexIndicesArray.add(elements[7].toInt())
                    uvIndicesArray.add(elements[8].toInt())
                    normalIndicesArray.add(elements[9].toInt())
                }
            }
        }
        }

        var vertexArrayResult = FloatArray(vertexIndicesArray.size * 5)

        println("VertexIndices: " + vertexIndicesArray.size)


        for(i in 0 until vertexIndicesArray.size){
            // obj index starts counting at 1 = -1 and stride of vertex array is 3
            val vertexIndex = (vertexIndicesArray[i] - 1) * 3
            val uvIndex = (uvIndicesArray[i] - 1) * 2

            vertexArrayResult[i * 5] = vertexArray[vertexIndex]
            vertexArrayResult[i * 5 + 1] = vertexArray[vertexIndex + 1]
            vertexArrayResult[i * 5 + 2] = vertexArray[vertexIndex + 2]

            vertexArrayResult[i * 5 + 3] = uvArray[uvIndex]
            vertexArrayResult[i * 5 + 4] = uvArray[uvIndex + 1]
        }

        return vertexArrayResult
    }

}