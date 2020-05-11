package com.vmcplus.thermalprinter

import android.graphics.Bitmap
import java.math.BigInteger

class Decoder {

    companion object {
        fun byteArrayOf(bitmap: Bitmap): ByteArray { // with should be multiple of 8
            val width = bitmap.width
            val height = bitmap.height

            var byteCommands = byteArrayOf()
            byteCommands += byteArrayOf(
                0x1D,
                0x76,
                0x30,
                0x00,
                0x30,
                0x00,
                0x00,
                0x02
            ) // GS v 0 m xL xH yL yH d1...dk

            var stringBuffer = StringBuffer()
            for (i in 0 until height) {
                for (j in 0 until width) {
                    val color = bitmap.getPixel(j, i)
                    val r = color shr 16 and 0xff
                    val g = color shr 8 and 0xff
                    val b = color and 0xff
                    // if color close to white，bit='0', else bit='1'
                    if (r > 160 && g > 160 && b > 160) stringBuffer.append("0")
                    else stringBuffer.append("1")
                }
            }
            byteCommands += binaryToByteArray(stringBuffer.toString())
            return byteCommands
        }

        private fun binaryToByteArray(string: String) =
            BigInteger(string.toString(), 2).toByteArray()
    }
}