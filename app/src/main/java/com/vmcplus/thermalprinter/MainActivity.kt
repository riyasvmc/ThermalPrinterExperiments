package com.vmcplus.thermalprinter

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Singleton.connect()
        buttonConnectPrinter.setOnClickListener{
            Singleton.connect()
        }

        buttonPrint.setOnClickListener{
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.riyas_halftone, BitmapFactory.Options().apply { inScaled = false })
            print(byteArrayOf(27, 64))                          // init printer

            for(i in 0 until 384 step 8) {
                val command = byteArrayOf(0x1B, 0x2A, 0, 128.toByte(), 1)
                val data = Utils.decodeBitmap(bitmap, i)
                //var data = byteArrayOf(0x1, 0, 255.toByte(), 0, 255.toByte())
                print(byteArrayOf(0x1B, 0x33, 24))                  // set line spacing 24
                print(command + data)                                         // print data
                print(byteArrayOf(0x0A))                            // feed paper
            }
        }
    }

    private fun print(data: ByteArray) {
        Singleton.outputStream.write(data)
    }
}