package com.vmcplus.thermalprinter

import android.graphics.Bitmap

class Utility {
    companion object{
        fun decodeBitmap(bitmap: Bitmap): ByteArray {
            var bytes = byteArrayOf()

            val width = bitmap.width
            val height = bitmap.height

            for (i in 0 until height step 8){
                var list = ArrayList<String>()
                lateinit var buffer: StringBuffer
                bytes += byteArrayOf(0x1B, 0x2A, 0x01, 128.toByte(), 1) // Bit image command, change width accordingly //Todo : dynamic calculation of width argument
                for (x in 0 until width) {
                    buffer = StringBuffer()
                    for (y in i until i + 8) {
                        try {
                            val color = bitmap.getPixel(x, y)
                            val r = color shr 16 and 0xff
                            val g = color shr 8 and 0xff
                            val b = color and 0xff
                            // if color close to white，bit='0', else bit='1'
                            if (r > 160 && g > 160 && b > 160) buffer.append("0")
                            else buffer.append("1")
                        } catch (e: Exception) { }
                    }
                    list.add(buffer.toString())
                }
                val bmpHexList = Utils.binaryListToHexStringList(list)
                bytes += Utils.hexList2Byte(bmpHexList)
                bytes += byteArrayOf(0x0A)                  // feed line
            }
            return bytes
        }
    }
}