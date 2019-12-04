
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
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
        fun floatToByteArray(value: Float): ByteArray {
            val intBits = java.lang.Float.floatToIntBits(value)

            return byteArrayOf(
                (intBits shr 24).toByte(),
                (intBits shr 16).toByte(),
                (intBits shr 8).toByte(),
                intBits.toByte()
            )
        }

        fun floatToByteArray2(value: Float): ByteArray{
            val int = java.lang.Float.floatToIntBits(value)
            val bytes = ByteBuffer.allocate(java.lang.Integer.BYTES).putInt(int).array()
            return bytes
        }

        fun byteArraytoFloat(bytes: ByteArray): Float{
            val buffer = ByteBuffer.wrap(bytes)
            val float = buffer.getFloat()
            return float
        }

        @JvmStatic fun main(args: Array<String>) {
            val test = Main()
            val originalArray = test.loadRawObj()

            println("Float value: " + originalArray[0] + " Byte array: " + floatToByteArray2(originalArray[0]) + " Bytearray to float: " + byteArraytoFloat(floatToByteArray2(originalArray[0])))


            val file = File ( "ducky.moces")

            originalArray.forEach { it ->
                byteArr += floatToByteArray2(it)
            }
            file.writeBytes(byteArr)


            var readBytesArray = file.readBytes()
            var result: FloatArray = FloatArray(originalArray.size)

            for (x in 0 until readBytesArray.size step 4)
            {
                var subset = byteArrayOf( readBytesArray[x], readBytesArray[x+1], readBytesArray[x+2], readBytesArray[x+3])
                val float = byteArraytoFloat(subset)
                println("Float: " + float)
                result[x / 4] = float
            }

            for(i in 0 until result.size){
                println("Previous: " + originalArray[i] + " after: "  + result[i])
            }
        }


    }

}