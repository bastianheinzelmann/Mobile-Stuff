
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.experimental.and


class Main {
    fun loadRawObj(): FloatArray {
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

        val file =
            File("C:\\Users\\Bastian\\AndroidStudioProjects\\ACubeThatRotates\\app\\src\\main\\assets\\psyduck.obj")

        file.bufferedReader().useLines { lines ->
            lines.forEach {

                val elements = it.split(" ", "/")

                when (elements[0]) {
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
                    "f" -> {
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

            //println("Vertex Index: " + vertexIndex)

            //println("x: " + vertexArray[vertexIndex])
            vertexArrayResult[i * 5] = vertexArray[vertexIndex]
            //println("y: " + vertexArray[vertexIndex + 1])
            vertexArrayResult[i * 5 + 1] = vertexArray[vertexIndex + 1]
            //println("z: " + vertexArray[vertexIndex + 2])
            vertexArrayResult[i * 5 + 2] = vertexArray[vertexIndex + 2]

            vertexArrayResult[i * 5 + 3] = uvArray[uvIndex]
            vertexArrayResult[i * 5 + 4] = uvArray[uvIndex + 1]
        }

        return vertexArrayResult
    }

    companion object {

        var byteArr = byteArrayOf();
        fun floatToByteArray(value: Float): ByteArray? {
            val intBits = java.lang.Float.floatToIntBits(value)
            byteArr += byteArrayOf(
                (intBits shr 24).toByte(),
                (intBits shr 16).toByte(),
                (intBits shr 8).toByte(),
                intBits.toByte()
            )
            return null
        }

        fun byteArraytoFloat(bytes: ByteArray): Float{
            val intBits: Int =
               bytes[0].toInt() shl 24 or (bytes[1].toInt() and 0xFF shl 16) or (bytes[2].toInt() and 0xFF.toInt() shl 8.toInt()) or (bytes[3].toInt() and 0xFF)
           return java.lang.Float.intBitsToFloat(intBits)
        }

        @JvmStatic fun main(args: Array<String>) {
            val test = Main()
            val array = test.loadRawObj()


            val file = File ( "ducky.moces")

            array.forEach { it ->
                floatToByteArray(it)
                //println(floatToByteArray(it)!!)

            }
            file.writeBytes(byteArr)


            var arr = file.readBytes()

            for (x in 0 until arr.size step 4)
            {
                var teilmenge = byteArrayOf( arr[x] ,arr[x+1],arr[x+2],arr[x+3])

                println(array[x].toString() + " gusch "+ byteArraytoFloat(teilmenge).toString())
            }
           // array.forEach { it -> println(it.toByte()) }
        }
    }
}