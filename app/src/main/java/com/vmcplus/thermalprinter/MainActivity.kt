package com.vmcplus.thermalprinter

import android.graphics.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.riyas, BitmapFactory.Options().apply { inScaled = false })
        //imageView.setImageBitmap(bitmap)

        Singleton.connect()
        buttonConnectPrinter.setOnClickListener{
            Singleton.connect()
        }

        imageView.setOnClickListener{
            drawMalayalamFont()
        }

        buttonPrint.setOnClickListener{
            print(PrinterCommands.INIT)
            print(PrinterCommands.SET_LINE_SPACING_24)
            /*print(text("" +
                            "Bank : ICICI Manjeri \n " +
                            "Name : Riyas V \n " +
                            "A/c  : 022201508775 \n " +
                            "IFSC : ICIC0000222 \n " +
                            "Mob  : 9037495473\n\n\n\n"
            ))*/

            // print(Utility.decodeBitmap(bitmap))
            print(RasterPrinting.decodeBitmap(bitmap))
        }
    }

    private fun drawMalayalamFont(){
        val width = 384
        val height = 384
        val imageBitmap = Bitmap.createBitmap(width ,height, Bitmap.Config.ARGB_8888)

        val paint2 = Paint()
        paint2.setColor(Color.WHITE);
        paint2.style = Paint.Style.FILL

        val canvas = Canvas(imageBitmap).apply {
            //rotate(90f, (imageView.width/2).toFloat(), (imageView.height/2).toFloat())
            drawPaint(paint2)
        }
        val typefaceMalayalam = ResourcesCompat.getFont(this, R.font.baloo_cheettan_regular)
        val scale = resources.displayMetrics.density
        val paint = Paint().apply {
            color = Color.BLACK
            typeface = typefaceMalayalam
            textSize = 30 * scale
        }
        val paint_price = Paint().apply {
            color = Color.BLACK
            typeface = typefaceMalayalam
            textSize = 80 * scale
        }
        canvas.drawText("തക്കാളി", 0f, 80f.toFloat(), paint)
        canvas.drawText("₹8", 0f, (height/2).toFloat(), paint_price)
        imageView.setImageBitmap(imageBitmap)
        bitmap = Bitmap.createScaledBitmap(imageBitmap, width, height/3, false)

        try {
            // print(Utility.decodeBitmap(imageBitmap))
        }catch (e: Exception){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun print(data: ByteArray) = Singleton.outputStream.write(data)

    fun text(text: String) = text.toByteArray()
}